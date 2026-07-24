package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.claimforpayment.api.DraftClaimPayloadDeserializer;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateDraftClaimResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPatch;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPost;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPut;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamServiceException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.LineItem;
import uk.gov.justice.laa.claimforpayment.model.LineItemRequestBody;

/**
 * Service class for managing draft claims operations. Handles retrieval, creation, update, and deletion
 * of draft claims from the Civil Claims API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DraftClaimService implements ClaimServiceInterface {

  private final CivilDraftClaimsApi civilDraftClaimsApi;

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  public UUID addEvidenceToClaim(UUID claimId, UploadFile uploadFile) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'addEvidenceToClaim'");
  }

  @Override
  public UUID createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    CivilDraftClaimPost body = new CivilDraftClaimPost();
    UUID claimId = generateUuid7();
    body.setId(claimId);
    body.setProviderUserId(providerUserId);
    body.setPayload(DraftClaimPayloadDeserializer.serialise(claimRequestBody, providerUserId, claimId));
    CivilCreateDraftClaimResponse response =
        executeCivilClaimsApi(
            () -> civilDraftClaimsApi.createDraftClaim(body), "POST /api/v1/drafts/");

    return response.getId();
  }

  @Override
  public void deleteClaim(UUID id) {
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.deleteDraftClaim(id);
          return null;
        },
        "DELETE /api/v1/drafts/{claimId}");
  }

  @Override
  public void deleteEvidenceFromClaim(UUID claimId, UUID evidenceId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteEvidenceFromClaim'");
  }

  @Override
  public Claim getClaim(UUID claimId) {
    CivilDraftClaim draftClaim =
        executeCivilClaimsApi(
            () -> civilDraftClaimsApi.getDraftClaim(claimId), "GET /api/v1/drafts/{claimId}");

    try {
      return DraftClaimPayloadDeserializer.deserialise(draftClaim);
    } catch (Exception e) {
      throw new UpstreamServiceException("Draft Claims API", "call", e);
    }
  }

  @Override
  public ClaimPage getClaims(int page, int limit) {
    CivilDraftClaimPageResponse response =
            executeCivilClaimsApi(() -> civilDraftClaimsApi.getDraftClaims(page, limit), "GET /api/v1/drafts");
    if (response == null) {
      return ClaimPage.empty(page, limit);
    }

    if (response.getDraftClaims() == null
            || response.getTotal() == null
            || response.getTotalPages() == null) {
      throw new IllegalStateException("Civil claims API returned an incomplete response");
    }

    List<Claim> claims  = response.getDraftClaims().stream().map(DraftClaimPayloadDeserializer::deserialise).toList();
    return new ClaimPage(claims, page, limit, response.getTotal(), response.getTotalPages());
  }

  @Override
  public void linkEvidenceToLineItem(UUID claimId, UUID lineItemId, List<UUID> evidenceIds) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'linkEvidenceToLineItem'");
  }

  @Override
  public void unlinkEvidenceFromLineItem(UUID claimId, UUID lineItemId, UUID evidenceId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'unlinkEvidenceFromLineItem'");
  }

  @Override
  public void updateClaim(UUID id, ClaimRequestBody claimRequestBody, UUID providerUserId) {
    CivilDraftClaimPut body = new CivilDraftClaimPut();
    body.setProviderUserId(providerUserId);
    body.setPayload(DraftClaimPayloadDeserializer.serialise(claimRequestBody, providerUserId, id));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.updateDraftClaim(id, body);
          return null;
        },
        "PUT /api/v1/drafts/{claimId}");
  }

  /**
   * Patches a draft claim by its id.
   */
  public void patchDraftClaim(UUID id, ClaimRequestBody claimRequestBody) {
    CivilDraftClaimPatch body = new CivilDraftClaimPatch();
    body.setPayload(DraftClaimPayloadDeserializer.serialise(claimRequestBody, null, id));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.patchDraftClaim(id, body);
          return null;
        },
        "PATCH /api/v1/drafts/{claimId}");
  }

  @Override
  public UUID addLineItemToClaim(UUID claimId, LineItemRequestBody lineItemRequestBody) {
    LineItem lineItem = LineItem.builder()
        .id(generateUuid7())
        .title(lineItemRequestBody.getTitle())
        .category(lineItemRequestBody.getCategory())
        .date(lineItemRequestBody.getDate())
        .actualNetValue(lineItemRequestBody.getActualNetValue())
        .vatApplicable(lineItemRequestBody.getVatApplicable())
        .feeEarnerName(lineItemRequestBody.getFeeEarnerName())
        .build();

    Claim claim = getClaim(claimId);
    claim.getLineItems().add(lineItem);

    CivilDraftClaimPatch civilDraftClaimPatch = new CivilDraftClaimPatch();
    civilDraftClaimPatch.setPayload(DraftClaimPayloadDeserializer.serialise(claim, claimId));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.patchDraftClaim(claimId, civilDraftClaimPatch);
          return null;
        },
        "PATCH /api/v1/drafts/{claimId}");

    return lineItem.getId();
  }
}
