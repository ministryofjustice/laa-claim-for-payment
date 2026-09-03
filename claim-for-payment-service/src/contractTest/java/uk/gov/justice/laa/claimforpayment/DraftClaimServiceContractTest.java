package uk.gov.justice.laa.claimforpayment;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.config.ClaimsApiPactTestConfig;
import uk.gov.justice.laa.claimforpayment.service.DraftClaimService;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {
        ClaimsApiPactTestConfig.class,
    },
    properties = {
        "civilclaims.api.base-url=http://localhost:9998"
    }
)
@PactConsumerTest
@PactTestFor(providerName = "civil-claims-api", port = "9998")
public class DraftClaimServiceContractTest {

  @Autowired
  CivilDraftClaimsApi civilDraftClaimsApi;

  private static final UUID CLAIM_ID = UUID.randomUUID();

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getDraftClaimById(PactDslWithProvider builder) {
    return builder
        .given(String.format("Draft claim with ID %s exists", CLAIM_ID))
        .uponReceiving(String.format("A request to get draft claim with ID %s", CLAIM_ID))
        .path(String.format("/api/v1/drafts/%s", CLAIM_ID))
        .method("GET")
        .willRespondWith()
        .status(200)
        .toPact(V4Pact.class);
  }

  @Test
  @PactTestFor(pactMethod = "getDraftClaimById")
  void shouldReturnDraftClaimForGivenId() {
    CivilDraftClaim result = civilDraftClaimsApi.getDraftClaim(CLAIM_ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(CLAIM_ID);
    assertThat(result.getPayload()).isNotNull();
  }

  private PactDslJsonBody draftClaimBody(UUID id) {
    return new PactDslJsonBody()
        .uuid("id", id)
        .uuid("providerUserId")
        .object("payload");
  }
}
