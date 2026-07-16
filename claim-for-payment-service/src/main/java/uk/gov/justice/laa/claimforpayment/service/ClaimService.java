package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidenceRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateClaimResponse;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimEvidenceRequestBodyMapper;
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
  private final ClaimEvidenceRequestBodyMapper claimEvidenceRequestBodyMapper;

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  public ClaimPage getClaims(int page, int limit) {
    CivilClaimPageResponse response =
        executeCivilClaimsApi(() -> civilClaimsApi.getClaims(page, limit), "GET /api/v1/claims");

    return response == null ? ClaimPage.empty(page, limit) : claimPageMapper.toDomain(response);
  }

  @Override
  public Claim getClaim(UUID claimId) {
    CivilClaim response =
        executeCivilClaimsApi(
            () -> civilClaimsApi.getClaim(claimId), "GET /api/v1/claims/{claimId}");

    return civilClaimMapper.toClaim(response);
  }

  @Override
  public UUID createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    var body = claimRequestBodyMapper.toCivilClaimRequestBody(claimRequestBody);
    body.setId(generateUuid7());
    CivilCreateClaimResponse response =
        executeCivilClaimsApi(() -> civilClaimsApi.createClaim(body), "POST /api/v1/claims/");

    return response.getId();
  }

  @Override
  public void updateClaim(UUID id, ClaimRequestBody claimRequestBody, UUID providerUserId) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.updateClaim(
              id, claimRequestBodyMapper.toCivilClaimRequestBody(claimRequestBody));
          return null;
        },
        "PUT /api/v1/claims/{claimId}");
  }

  @Override
  public void deleteClaim(UUID id) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.deleteClaim(id);
          return null;
        },
        "DELETE /api/v1/claims/{claimId}");
  }

  @Override
  public UUID addEvidenceToClaim(UUID claimId, UploadFile uploadFile) {
    CivilClaimEvidenceRequestBody civilClaimEvidenceRequestBody =
        claimEvidenceRequestBodyMapper.toCivilClaimEvidenceRequestBody(uploadFile);
    var response =
        executeCivilClaimsApi(
            () -> civilClaimsApi.addEvidenceToClaim(claimId, civilClaimEvidenceRequestBody),
            "POST /api/v1/claims/{claimId}/evidence");

    return response.getId();
  }

  @Override
  public void deleteEvidenceFromClaim(UUID claimId, UUID evidenceId) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.deleteEvidenceFromClaim(claimId, evidenceId);
          return null;
        },
        "DELETE /api/v1/claims/{claimId}/evidence");
  }

  @Override
  public void linkEvidenceToLineItem(UUID claimId, UUID lineItemId, List<UUID> evidenceIds) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.addEvidenceToLineItem(claimId, lineItemId, evidenceIds);
          return null;
        },
        "PUT /api/v1/claims/{claimId}/evidence/{evidenceId}");
  }

  @Override
  public void unlinkEvidenceFromLineItem(UUID claimId, UUID lineItemId, UUID evidenceId) {
    executeCivilClaimsApi(
        () -> {
          civilClaimsApi.unlinkEvidenceFromLineItem(claimId, lineItemId, evidenceId);
          return null;
        },
        "DELETE /api/v1/claims/{claimId}/line-items/{lineItemId}/evidence/{evidenceId}");
  }
}
