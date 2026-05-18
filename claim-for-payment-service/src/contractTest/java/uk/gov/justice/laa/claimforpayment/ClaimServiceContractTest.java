package uk.gov.justice.laa.claimforpayment;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.justice.laa.claimforpayment.config.ClaimsApiPactTestConfig;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapperImpl;
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
            ClaimPageMapperImpl.class,
            ClaimRequestBodyMapperImpl.class
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


  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getClaimById(PactDslWithProvider builder) {
    return builder
            .given("Claim with ID 1 exists")
            .uponReceiving("A request to get claim with ID 1")
            .path("/api/v1/claims/1")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(claimBody(1L))
            .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getClaimNotFound(PactDslWithProvider builder) {
    return builder
            .given("Claim with ID 999 does not exist")
            .uponReceiving("A request to get claim with ID 999")
            .path("/api/v1/claims/999")
            .method("GET")
            .willRespondWith()
            .status(404)
            .toPact(V4Pact.class);
  }

  @Test
  @PactTestFor(pactMethod = "getClaimById")
  void shouldReturnClaimForGivenId() {
    Claim result = claimService.getClaim(1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getSubmissionId()).isNotNull();
    assertThat(result.getClient()).isNotBlank();
    assertThat(result.getUfn()).isNotBlank();
  }

  @Test
  @PactTestFor(pactMethod = "getClaimNotFound")
  void shouldThrowNotFoundWhenClaimDoesNotExist() {
    assertThatThrownBy(() -> claimService.getClaim(999L))
            .isInstanceOf(ResourceNotFoundException.class);
  }

  private PactDslJsonBody claimBody(Long id) {
    return new PactDslJsonBody()
            .integerType("id", id)
            .stringType("ufn", "123456/001")
            .uuid("providerUserId")
            .stringType("client", "John Doe")
            .stringType("category", "CIVIL")
            .stringType("feeType", "STANDARD")
            .booleanType("escaped", false)
            .stringType("counselPayment", "NONE")
            .decimalType("claimed", 100.50)
            .stringMatcher("concluded", "\\d{4}-\\d{2}-\\d{2}", "2026-01-01")
            .uuid("submissionId");
  }

}



