package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidenceRequestBody;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class DraftClaimService implements ClaimServiceInterface{

  private final CivilDraftClaimsApi civilDraftClaimsApi;

  @Override
  public ClaimPage getClaims(int page, int limit) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getClaims'");
  }

  @Override
  public Claim getClaim(Long claimId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getClaim'");
  }

  @Override
  public Long createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createClaim'");
  }

  @Override
  public void updateClaim(Long id, ClaimRequestBody claimRequestBody) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateClaim'");
  }

  @Override
  public void deleteClaim(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteClaim'");
  }

  @Override
  public Long addEvidenceToClaim(Long claimId, CivilClaimEvidenceRequestBody civilClaimEvidenceRequestBody) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'addEvidenceToClaim'");
  }

  @Override
  public void deleteEvidenceFromClaim(Long claimId, Long evidenceId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteEvidenceFromClaim'");
  }

  @Override
  public void linkEvidenceToLineItem(Long claimId, Long lineItemId, List<Long> evidenceIds) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'linkEvidenceToLineItem'");
  }

  @Override
  public void unlinkEvidenceFromLineItem(Long claimId, Long lineItemId, Long evidenceId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'unlinkEvidenceFromLineItem'");
  }

}
