package uk.gov.justice.laa.claimforpayment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;

import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.config.ClaimsApiPactTestConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ClaimsApiPactTestConfig.class)
@TestPropertySource(properties = "civilclaims.api.base-url=http://localhost:9998")
@PactConsumerTest
@PactTestFor(providerName = "civil-claims-api", port = "9998")
public class DraftClaimServiceContractTest {

  @Autowired CivilDraftClaimsApi civilDraftClaimsApi;

  private static final UUID CLAIM_ID = UUID.randomUUID();
  private static final UUID NON_EXISTENT_CLAIM_ID = UUID.randomUUID();

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getDraftClaimById(PactDslWithProvider builder) {
    return builder
        .given(String.format("Draft claim with ID %s exists", CLAIM_ID))
        .uponReceiving(String.format("A request to get draft claim with ID %s", CLAIM_ID))
        .path(String.format("/api/v1/drafts/%s", CLAIM_ID))
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(draftClaimBody(CLAIM_ID))
        .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getDraftClaimByIdNotFound(PactDslWithProvider builder) {
    return builder
        .given(String.format("Draft claim with ID %s does not exist", NON_EXISTENT_CLAIM_ID))
        .uponReceiving(String.format("A request to get draft claim with ID %s", NON_EXISTENT_CLAIM_ID))
        .path(String.format("/api/v1/drafts/%s", NON_EXISTENT_CLAIM_ID))
        .method("GET")
        .willRespondWith()
        .status(404)
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

  @Test
  @PactTestFor(pactMethod = "getDraftClaimByIdNotFound")
  void shouldReturnNotFoundForNonExistentDraftClaim() {
    assertThatThrownBy(() -> civilDraftClaimsApi.getDraftClaim(NON_EXISTENT_CLAIM_ID))
        .isInstanceOf(RestClientException.class);
  }

  private PactDslJsonBody draftClaimBody(UUID id) {
    return new PactDslJsonBody().uuid("id", id).uuid("providerUserId").object("payload");
  }
}
