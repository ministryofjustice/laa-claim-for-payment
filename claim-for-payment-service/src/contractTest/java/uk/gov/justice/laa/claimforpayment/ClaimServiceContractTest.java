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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.config.ClaimsApiPactTestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ClaimsApiPactTestConfig.class)
@TestPropertySource(properties = "civilclaims.api.base-url=http://localhost:9999")
@PactConsumerTest
@PactTestFor(providerName = "civil-claims-api", port = "9999")
class ClaimServiceContractTest {

  @Autowired CivilClaimsApi civilClaimsApi;

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
    CivilClaim result = civilClaimsApi.getClaim(CLAIM_ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(CLAIM_ID);
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
