package uk.gov.justice.laa.claimforpayment.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
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
    try {
      CivilClaimPageResponse response = civilClaimsApi.getClaims(page, limit);

      if (response == null) {
        return ClaimPage.empty(page, limit);
      }

      return claimPageMapper.toDomain(response);
    } catch (HttpStatusCodeException ex) {
      throw translateHttpStatusFailure("Civil Claims API", "GET /api/v1/claims", ex);

    } catch (ResourceAccessException ex) {
      throw new UpstreamTimeoutException("Civil Claims API", "call", ex);

    } catch (RestClientException ex) {
      throw new UpstreamServiceException("Civil Claims API", "call", ex);
    }
  }

  @Override
  public Claim getClaim(Long claimId) {
    try {
      CivilClaim response = civilClaimsApi.getClaim(claimId);
      return civilClaimMapper.toClaim(response);
    } catch (HttpStatusCodeException ex) {
      throw translateHttpStatusFailure("Civil Claims API", "GET /api/v1/claims/{claimId}", ex);

    } catch (ResourceAccessException ex) {
      throw new UpstreamTimeoutException("Civil Claims API", "call", ex);

    } catch (RestClientException ex) {
      throw new UpstreamServiceException("Civil Claims API", "call", ex);
    }
  }

  @Override
  public Long createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    try {
      CivilCreateClaimResponse response =
          civilClaimsApi.createClaim(
              claimRequestBodyMapper.toCivilClaimRequestBody(claimRequestBody));
      return response.getId();
    } catch (HttpStatusCodeException ex) {
      throw translateHttpStatusFailure("Civil Claims API", "POST /api/v1/claims/", ex);

    } catch (ResourceAccessException ex) {
      throw new UpstreamTimeoutException("Civil Claims API", "call", ex);

    } catch (RestClientException ex) {
      throw new UpstreamServiceException("Civil Claims API", "call", ex);
    }
  }

  @Override
  public void updateClaim(Long id, ClaimRequestBody claimRequestBody) {
    try {
      civilClaimsApi.updateClaim(
          id, claimRequestBodyMapper.toCivilClaimRequestBody(claimRequestBody));

    } catch (HttpStatusCodeException ex) {
      throw translateHttpStatusFailure("Civil Claims API", "PUT /api/v1/claims/{id}", ex);

    } catch (ResourceAccessException ex) {
      throw new UpstreamTimeoutException("Civil Claims API", "call", ex);

    } catch (RestClientException ex) {
      throw new UpstreamServiceException("Civil Claims API", "call", ex);
    }
  }

  @Override
  public void deleteClaim(Long id) {
    try {

      civilClaimsApi.deleteClaim(id);
    } catch (HttpStatusCodeException ex) {
      throw translateHttpStatusFailure("Civil Claims API", "DELETE /api/v1/claims/", ex);

    } catch (ResourceAccessException ex) {
      throw new UpstreamTimeoutException("Civil Claims API", "call", ex);

    } catch (RestClientException ex) {
      throw new UpstreamServiceException("Civil Claims API", "call", ex);
    }
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
}
