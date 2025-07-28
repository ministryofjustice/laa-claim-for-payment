package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.exception.ClaimNotFoundException;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimMapper;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.repository.ClaimRepository;

/** Service class for handling claims requests. */
@RequiredArgsConstructor
@Service
public class ClaimService {

  private final ClaimRepository claimRepository;
  private final ClaimMapper claimMapper;

  /**
   * Gets all claims.
 * @param submissionId 
   *
   * @return the list of claims
   */
  public List<Claim> getClaims(UUID submissionId) {
    return claimRepository.findAll().stream().map(claimMapper::toClaim).toList();
  }

  /**
   * Gets a claim for a given id.
   *
   * @param claimId the claim id
   * @return the requested claim
   */
  public Claim getClaim(UUID submissionId, Long claimId) {
    ClaimEntity claimEntity = checkIfClaimExist(claimId);
    return claimMapper.toClaim(claimEntity);
  }

  public Submission getSubmission(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getSubmission'");
  }

  /**
   * Creates a claim.
   *
   * @param submissionId the id of the parent submission
   * @param claimRequestBody the claim to be created
   * @return the id of the created claim
   */
  public Long createClaim(UUID submissionId, ClaimRequestBody claimRequestBody) {
    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setUfn(claimRequestBody.getUfn());
    claimEntity.setClient(claimRequestBody.getClient());
    claimEntity.setCategory(claimRequestBody.getCategory());
    claimEntity.setConcluded(claimRequestBody.getConcluded());
    claimEntity.setFeeType(claimRequestBody.getFeeType());
    claimEntity.setClaimed(claimRequestBody.getClaimed());
    ClaimEntity createdClaimEntity = claimRepository.save(claimEntity);
    return createdClaimEntity.getId();
  }

  /**
   * Updates a claim.
   *
   * @param id the id of the claim to be updated
   * @param claimRequestBody the updated claim
   */
  public void updateClaim(Long id, ClaimRequestBody claimRequestBody) {
    ClaimEntity claimEntity = checkIfClaimExist(id);
    claimEntity.setUfn(claimRequestBody.getUfn());
    claimEntity.setClient(claimRequestBody.getClient());
    claimEntity.setCategory(claimRequestBody.getCategory());
    claimEntity.setConcluded(claimRequestBody.getConcluded());
    claimEntity.setFeeType(claimRequestBody.getFeeType());
    claimEntity.setClaimed(claimRequestBody.getClaimed());
    claimRepository.save(claimEntity);
  }

  /**
   * Deletes a claim.
   *
   * @param id the id of the claim to be deleted
   */
  public void deleteClaim(Long id) {
    checkIfClaimExist(id);
    claimRepository.deleteById(id);
  }

  private ClaimEntity checkIfClaimExist(Long id) {
    return claimRepository
        .findById(id)
        .orElseThrow(
            () -> new ClaimNotFoundException(String.format("No claim found with id: %s", id)));
  }

  public List<Submission> getAllSubmissionsForProvider(UUID uuid) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllSubmissionsForProvider'");
  }
}
