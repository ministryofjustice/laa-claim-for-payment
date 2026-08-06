package uk.gov.justice.laa.claimforpayment.service;

import com.fasterxml.uuid.Generators;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamClientException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamConflictException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamForbiddenException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamRateLimitedException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamServiceException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamTimeoutException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamUnauthorisedException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamValidationException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.LineItemRequestBody;

/** An interface to some method of managing claims. */
public interface ClaimServiceInterface {

  Logger getLogger();

  default void execute() {
    getLogger().info("Execution started");
  }

  /**
   * Gets all claims.
   *
   * @return the list of claims
   */
  ClaimPage getClaims(int page, int limit);

  /**
   * Gets a claim for a given id.
   *
   * @param claimId the claim id
   * @return the requested claim
   */
  Claim getClaim(UUID claimId);

  /**
   * Creates a claim.
   *
   * @param claimRequestBody the claim to be created
   * @param providerUserId the provider user ID
   * @return the id of the created claim
   */
  UUID createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId);

  /**
   * Updates a claim.
   *
   * @param id the id of the claim to be updated
   * @param claimRequestBody the updated claim
   * @param providerUserId the provider user ID
   */
  void updateClaim(UUID id, ClaimRequestBody claimRequestBody, UUID providerUserId);

  /**
   * Deletes a claim.
   *
   * @param id the id of the claim to be deleted
   */
  void deleteClaim(UUID id);

  UUID addEvidenceToClaim(UUID claimId, UploadFile uploadFile);

  void deleteEvidenceFromClaim(UUID claimId, UUID evidenceId);

  void deleteAllEvidenceFromClaim(UUID claimId);

  void linkEvidenceToLineItem(UUID claimId, UUID lineItemId, List<UUID> evidenceIds);

  void unlinkEvidenceFromLineItem(UUID claimId, UUID lineItemId, UUID evidenceId);

  UUID addLineItemToClaim(UUID claimId, LineItemRequestBody lineItemRequestBody);

  void updateLineItem(UUID claimId, UUID lineItemId, LineItemRequestBody lineItemRequestBody);

  void deleteLineItem(UUID claimId, UUID lineItemId);

  default UUID generateUuid7() {
    return Generators.timeBasedEpochGenerator().generate();
  }

  private RuntimeException translateHttpStatusFailure(
      String service, String operation, HttpStatusCodeException ex) {

    int status = ex.getStatusCode().value();

    getLogger().debug("Operation: {}, Status: {}", operation, status);

    return switch (status) {
      case 400, 422 -> new UpstreamValidationException(service, operation, ex);
      case 401 -> new UpstreamUnauthorisedException(service, operation, ex);
      case 403 -> new UpstreamForbiddenException(service, operation, ex);
      case 404 -> new ResourceNotFoundException(service, operation, ex);
      case 409 -> new UpstreamConflictException(service, operation, ex);
      case 429 -> new UpstreamRateLimitedException(service, operation, ex);
      default -> {
        if (status >= 500) {
          yield new UpstreamServiceException(service, String.valueOf(status), ex);
        }
        yield new UpstreamClientException(service, String.valueOf(status), ex);
      }
    };
  }

  /**
   * Calls the civil claims API and handles the result including failures.
   *
   * @param callback the function to call
   * @param operation the RESTful endpoint
   * @param <T> return type of the callback
   * @return the result of the callback
   */
  default <T> T executeCivilClaimsApi(Supplier<T> callback, String operation) {
    try {
      return callback.get();
    } catch (HttpStatusCodeException ex) {
      throw translateHttpStatusFailure("Civil Claims API", operation, ex);
    } catch (ResourceAccessException ex) {
      throw new UpstreamTimeoutException("Civil Claims API", "call", ex);
    } catch (RestClientException ex) {
      throw new UpstreamServiceException("Civil Claims API", "call", ex);
    }
  }
}
