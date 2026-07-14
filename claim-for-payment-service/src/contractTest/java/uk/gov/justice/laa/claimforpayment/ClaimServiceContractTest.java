package uk.gov.justice.laa.claimforpayment;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.justice.laa.claimforpayment.config.ClaimsApiPactTestConfig;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimEvidenceMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimEvidenceRequestBodyMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimPageMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimRequestBodyMapperImpl;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {
            ClaimService.class,
            ClaimsApiPactTestConfig.class,
            CivilClaimMapperImpl.class,
            CivilClaimEvidenceMapperImpl.class,
            ClaimPageMapperImpl.class,
            ClaimRequestBodyMapperImpl.class,
            ClaimEvidenceRequestBodyMapperImpl.class
        },
        properties = {
            "civilclaims.api.base-url=http://localhost:9999"
        }
)
@PactConsumerTest
@PactTestFor(providerName = "civil-claims-api", port = "9999")
class ClaimServiceContractTest {

  @Autowired
  ClaimService claimService;

  private static final UUID CLAIM_ID = UUID.randomUUID();

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getClaimById(PactDslWithProvider builder) {
    return builder
            .given(String.format("Claim with ID %s exists", CLAIM_ID))
            .uponReceiving(String.format("A request to get claim with ID %s", CLAIM_ID))
            .path(String.format("/api/v1/claims/%s", CLAIM_ID))
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(claimBody(CLAIM_ID))
            .toPact(V4Pact.class);
  }

  @Test
  @PactTestFor(pactMethod = "getClaimById")
  void shouldReturnClaimForGivenId() {
    Claim result = claimService.getClaim(CLAIM_ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(CLAIM_ID);
    assertThat(result.getClient()).isNotBlank();
    assertThat(result.getUfn()).isNotBlank();
  }

  private PactDslJsonBody claimBody(UUID id) {
    return new PactDslJsonBody()
            .uuid("id", id)
            .stringType("ufn", "123456/001")
            .uuid("providerUserId")
            .stringType("client", "John Doe")
            .stringType("category", "CIVIL")
            .stringType("feeType", "STANDARD")
            .booleanType("escaped", false)
            .stringType("counselPayment", "NONE")
            .decimalType("claimed", 100.50)
            .stringMatcher("concluded", "\\d{4}-\\d{2}-\\d{2}", "2026-01-01");
  }
}
