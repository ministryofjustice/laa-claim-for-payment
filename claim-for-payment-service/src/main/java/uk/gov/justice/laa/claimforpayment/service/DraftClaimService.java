package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import uk.gov.justice.laa.claimforpayment.api.DraftClaimPayloadDeserializer;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateDraftClaimResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPost;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamServiceException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

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

    ObjectMapper mapper = new ObjectMapper();
    body.setId(generateUuid7());
    body.setProviderUserId(providerUserId);

    body.setPayload(mapper.writeValueAsString(claimRequestBody));
    CivilCreateDraftClaimResponse response =
        executeCivilClaimsApi(
            () -> civilDraftClaimsApi.createDraftClaim(body), "POST /api/v1/drafts/");

    return response.getId();
  }

  @Override
  public void deleteClaim(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteClaim'");
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getClaims'");
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
  public void updateClaim(UUID id, ClaimRequestBody claimRequestBody) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateClaim'");
  }
}
