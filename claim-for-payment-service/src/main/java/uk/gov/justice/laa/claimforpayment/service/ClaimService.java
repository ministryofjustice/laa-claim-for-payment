package uk.gov.justice.laa.claimforpayment.service;

import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidenceRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateClaimResponse;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamClientException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamConflictException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamForbiddenException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamRateLimitedException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamServiceException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamTimeoutException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamUnauthorisedException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamValidationException;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimPageMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimRequestBodyMapper;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

/**
 * Service class for managing claims operations. Handles retrieval, creation, update, and deletion
 * of claims from the Civil Claims API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService implements ClaimServiceInterface {

  private final CivilClaimsApi civilClaimsApi;
  private final CivilClaimMapper civilClaimMapper;
  private final ClaimPageMapper claimPageMapper;
  private final ClaimRequestBodyMapper claimRequestBodyMapper;

  @Override
  public ClaimPage getClaims(int page, int limit) {
    CivilClaimPageResponse response =
        executeCivilClaimsApi(() -> civilClaimsApi.getClaims(page, limit), "GET /api/v1/claims");

    return response == null ? ClaimPage.empty(page, limit) : claimPageMapper.toDomain(response);
  }

  @Override
  public Claim getClaim(Long claimId) {
    CivilClaim response =
        executeCivilClaimsApi(
            () -> civilClaimsApi.getClaim(claimId), "GET /api/v1/claims/{claimId}");

    return civilClaimMapper.toClaim(response);
  }

  @Override
  public Long createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    CivilCreateClaimResponse response =
        executeCivilClaimsApi(
            () ->
                civilClaimsApi.createClaim(
                    claimRequestBodyMapper.toCivilClaimRequestBody(claimRequestBody)),
            "POST /api/v1/claims/");

    return response.getId();
  }

  @Override
  public void updateClaim(Long id, ClaimRequestBody claimRequestBody) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.updateClaim(
              id, claimRequestBodyMapper.toCivilClaimRequestBody(claimRequestBody));
          return null;
        },
        "PUT /api/v1/claims/{id}");
  }

  @Override
  public void deleteClaim(Long id) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.deleteClaim(id);
          return null;
        },
        "DELETE /api/v1/claims/");
  }

  @Override
  public Long addEvidenceToClaim(
      Long claimId, CivilClaimEvidenceRequestBody civilClaimEvidenceRequestBody) {
    var response =
        executeCivilClaimsApi(
            () -> civilClaimsApi.addEvidenceToClaim(claimId, civilClaimEvidenceRequestBody),
            "POST /api/v1/claims/{claimId}/evidence");

    return response.getId();
  }

  private RuntimeException translateHttpStatusFailure(
      String service, String operation, HttpStatusCodeException ex) {

    int status = ex.getStatusCode().value();

    log.debug("Operation: {}, Status: {}", operation, status);

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

  private <T> T executeCivilClaimsApi(Supplier<T> callback, String operation) {
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

  @Override
  public void linkEvidenceToLineItem(Long claimId, Long lineItemId, Long evidenceId) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.addEvidenceToLineItem(claimId, lineItemId, evidenceId);
          return null;
        },
        "PUT /api/v1/claims/{claimId}/evidence/{evidenceId}");
  }
}
