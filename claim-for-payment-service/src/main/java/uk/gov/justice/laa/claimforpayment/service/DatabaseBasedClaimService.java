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
import uk.gov.justice.laa.claimforpayment.repository.ClaimRepository;

/** Service class for handling claims requests. */
@RequiredArgsConstructor
@Service
public class DatabaseBasedClaimService implements ClaimServiceInterface {

  private final ClaimRepository claimRepository;
  private final ClaimMapper claimMapper;

  /**
   * Gets all claims.
   *
   * @return the list of claims
   */
  @Override
  public List<Claim> getClaims() {
    return claimRepository.findAll().stream().map(claimMapper::toClaim).toList();
  }

  /**
   * Gets a claim for a given id.
   *
   * @param claimId the claim id
   * @return the requested claim
   */
  @Override
  public Claim getClaim(Long claimId) {
    ClaimEntity claimEntity = checkIfClaimExist(claimId);
    return claimMapper.toClaim(claimEntity);
  }

  /**
   * Creates a claim.
   *
   * @param claimRequestBody the claim to be created
   * @return the id of the created claim
   */
  @Override
  public Long createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setUfn(claimRequestBody.getUfn());
    claimEntity.setClient(claimRequestBody.getClient());
    claimEntity.setCategory(claimRequestBody.getCategory());
    claimEntity.setConcluded(claimRequestBody.getConcluded());
    claimEntity.setFeeType(claimRequestBody.getFeeType());
    claimEntity.setClaimed(claimRequestBody.getClaimed());
    claimEntity.setProviderUserId(providerUserId);
    claimEntity.setSubmissionId(claimRequestBody.getSubmissionId());

    ClaimEntity createdClaimEntity = claimRepository.save(claimEntity);
    return createdClaimEntity.getId();
  }

  /**
   * Updates a claim.
   *
   * @param id the id of the claim to be updated
   * @param claimRequestBody the updated claim
   */
  @Override
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
  @Override
  public void deleteClaim(Long id) {
    checkIfClaimExist(id);
    claimRepository.deleteById(id);
  }

  private ClaimEntity checkIfClaimExist(Long id) throws ClaimNotFoundException {
    return claimRepository
        .findById(id)
        .orElseThrow(
            () -> new ClaimNotFoundException(String.format("No claim found with id: %s", id)));
  }

  @Override
  public List<Claim> getAllClaimsForProvider(UUID providerUserId) {
    return claimRepository.findByProviderUserId(providerUserId).stream()
        .map(claimMapper::toClaim)
        .toList();
  }
}
