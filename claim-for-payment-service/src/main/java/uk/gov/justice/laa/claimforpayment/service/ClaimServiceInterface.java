package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

/** An interface to some method of managing claims. */
public interface ClaimServiceInterface {

  /**
   * Gets all claims.
   *
   * @return the list of claims
   */
  List<Claim> getClaims();

  /**
   * Gets a claim for a given id.
   *
   * @param claimId the claim id
   * @return the requested claim
   */
  Claim getClaim(Long claimId);

  /**
   * Creates a claim.
   *
   * @param claimRequestBody the claim to be created
   * @return the id of the created claim
   */
  Long createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId);

  /**
   * Updates a claim.
   *
   * @param id the id of the claim to be updated
   * @param claimRequestBody the updated claim
   */
  void updateClaim(Long id, ClaimRequestBody claimRequestBody);

  /**
   * Deletes a claim.
   *
   * @param id the id of the claim to be deleted
   */
  void deleteClaim(Long id);

  /**
   * Gets all claims for a given provider user ID.
   *
   * @param providerUserId the ID of the provider user
   * @return a list of submissions for the provider user
   */
  List<Claim> getAllClaimsForProvider(UUID providerUserId);
}
