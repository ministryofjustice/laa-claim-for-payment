package uk.gov.justice.laa.claimforpayment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateDraftClaimResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPost;
import uk.gov.justice.laa.claimforpayment.config.ClaimsApiPactTestConfig;

/**
 * Contract tests for the DraftClaimService, which interacts with the Civil Draft Claims API. These
 * tests use Pact to verify that the service behaves correctly when interacting with the API.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ClaimsApiPactTestConfig.class)
@TestPropertySource(properties = "civilclaims.api.base-url=http://localhost:9998")
@PactConsumerTest
@PactTestFor(providerName = "civil-claims-api", port = "9998")
public class DraftClaimServiceContractTest {

  @Autowired CivilDraftClaimsApi civilDraftClaimsApi;

  private static final String UUID_REGEX =
      "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

  private static final UUID CLAIM_ID = UUID.randomUUID();

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getDraftClaimById(PactDslWithProvider builder) {
    return builder
        .given("Draft claim exists")
        .uponReceiving("A request to get draft claim ")
        .matchPath(String.format("/api/v1/drafts/%s", UUID_REGEX))
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(draftClaimBody())
        .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact getDraftClaimByIdNotFound(PactDslWithProvider builder) {
    return builder
        .given("Draft claim does not exist")
        .uponReceiving("A request to get draft claim with non existent ID ")
        .matchPath(String.format("/api/v1/drafts/%s", UUID_REGEX))
        .method("GET")
        .willRespondWith()
        .status(404)
        .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact deleteDraftClaimById(PactDslWithProvider builder) {
    return builder
        .given("Draft claim exists")
        .uponReceiving("A request to delete draft claim")
        .matchPath(String.format("/api/v1/drafts/%s", UUID_REGEX))
        .method("DELETE")
        .willRespondWith()
        .status(204)
        .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact deleteDraftClaimByIdNotFound(PactDslWithProvider builder) {
    return builder
        .given(String.format("Draft claim with ID %s does not exist", CLAIM_ID))
        .uponReceiving(String.format("A request to delete draft claim with ID %s", CLAIM_ID))
        .matchPath(String.format("/api/v1/drafts/%s", UUID_REGEX))
        .method("DELETE")
        .willRespondWith()
        .status(404)
        .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact createDraftClaimWithValidRequestBody(PactDslWithProvider builder) {
    return builder
        .given("Draft claim can be created")
        .uponReceiving("A request to create draft claim with valid request body")
        .path("/api/v1/drafts")
        .method("POST")
        .headers(Map.of(
            "Content-Type", "application/json"
        ))
        .body(draftClaimBody())
        .willRespondWith()
        .status(201)
        .body(
            new PactDslJsonBody()
                .uuid("id")
        )
        .toPact(V4Pact.class);
  }

  @Pact(consumer = "laa-claim-for-payment")
  public V4Pact createDraftClaimWithInvalidRequestBody(PactDslWithProvider builder) {
    return builder
        .given("Draft claim cannot be created due to validation errors")
        .uponReceiving("A request to create draft claim with null fields")
        .path("/api/v1/drafts")
        .method("POST")
        .headers(Map.of(
            "Content-Type", "application/json"
        ))
        .body(new PactDslJsonBody()
            .nullValue("id")
            .nullValue("providerUserId")
            .object("payload")
            .closeObject()
        )
        .willRespondWith()
        .status(400)
        .toPact(V4Pact.class);
  }

  @Test
  @PactTestFor(pactMethod = "deleteDraftClaimByIdNotFound")
  void shouldReturnNotFoundWhenDeletingNonExistentDraftClaim() {
    assertThatThrownBy(() -> civilDraftClaimsApi.deleteDraftClaim(CLAIM_ID))
        .isInstanceOf(RestClientException.class);
  }

  @Test
  @PactTestFor(pactMethod = "deleteDraftClaimById")
  void shouldDeleteDraftClaimForGivenId() {
    civilDraftClaimsApi.deleteDraftClaim(CLAIM_ID);
  }

  @Test
  @PactTestFor(pactMethod = "getDraftClaimById")
  void shouldReturnDraftClaimForGivenId() {
    CivilDraftClaim result = civilDraftClaimsApi.getDraftClaim(CLAIM_ID);

    assertThat(result).isNotNull();
    assertThat(result.getId().toString()).matches(UUID_REGEX);
    assertThat(result.getPayload()).isNotNull();
  }

  @Test
  @PactTestFor(pactMethod = "getDraftClaimByIdNotFound")
  void shouldReturnNotFoundForNonExistentDraftClaim() {
    assertThatThrownBy(() -> civilDraftClaimsApi.getDraftClaim(CLAIM_ID))
        .isInstanceOf(RestClientException.class);
  }

  @Test
  @PactTestFor(pactMethod = "createDraftClaimWithValidRequestBody")
  void shouldCreateDraftClaim() {
    CivilDraftClaimPost request = new CivilDraftClaimPost()
        .id(UUID.randomUUID())
        .providerUserId(UUID.randomUUID())
        .payload(new HashMap<>());

    CivilCreateDraftClaimResponse response =
        civilDraftClaimsApi.createDraftClaim(request);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isNotNull();
  }

  @Test
  @PactTestFor(pactMethod = "createDraftClaimWithInvalidRequestBody")
  void shouldThrowExceptionWhenDraftClaimRequestIsInvalid() {
    CivilDraftClaimPost request = new CivilDraftClaimPost();

    assertThatThrownBy(() -> civilDraftClaimsApi.createDraftClaim(request))
        .isInstanceOf(RestClientException.class);
  }

  private PactDslJsonBody draftClaimBody() {
    return new PactDslJsonBody().uuid("id").uuid("providerUserId").object("payload");
  }
}
