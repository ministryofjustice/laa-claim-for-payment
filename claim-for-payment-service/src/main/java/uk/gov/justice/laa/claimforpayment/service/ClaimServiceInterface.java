package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.UUID;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.model.SubmissionRequestBody;

/**
 * An interface to some method of managing claims.
 */
public interface ClaimServiceInterface {

  /**
   * Gets all claims.
   *
   * @param submissionId the ID of the submission
   * @return the list of claims
   */
  List<Claim> getClaims(UUID submissionId);

  /**
   * Gets a claim for a given id.
   *
   * @param claimId the claim id
   * @return the requested claim
   */
  Claim getClaim(UUID submissionId, Long claimId);

  /**
   * Gets a submission by its ID.
   *
   * @param submissionId the ID of the submission
   * @return the submission
   */
  Submission getSubmission(UUID submissionId, boolean includeTotals);

  /**
   * Creates a new submission.
   *
   * @param submissionRequestBody the request body containing submission details
   * @return the ID of the created submission
   */
  UUID createSubmission(SubmissionRequestBody submissionRequestBody);

  /**
   * Creates a claim.
   *
   * @param submissionId the id of the parent submission
   * @param claimRequestBody the claim to be created
   * @return the id of the created claim
   */
  Long createClaim(UUID submissionId, ClaimRequestBody claimRequestBody);

  /**
   * Updates a claim.
   *
   * @param id the id of the claim to be updated
   * @param claimRequestBody the updated claim
   */
  void updateClaim(UUID submissionId, Long id, ClaimRequestBody claimRequestBody);

  /**
   * Creates a new submission.
   *
   * @param submissionRequestBody the request body containing submission details
   */
  void updateSubmission(UUID id, SubmissionRequestBody submissionRequestBody);

  /**
   * Deletes a submission.
   *
   * @param id the id of the submission to be deleted
   */
  void deleteSubmission(UUID id);

  /**
   * Deletes a claim.
   *
   * @param submissionId the id of the parent submission
   * @param id the id of the claim to be deleted
   */
  void deleteClaim(UUID submissionId, Long id);

  /**
   * Gets all submissions for a given provider user ID.
   *
   * @param providerUserId the ID of the provider user
   * @return a list of submissions for the provider user
   */
  List<Submission> getAllSubmissionsForProvider(UUID providerUserId, boolean includeTotals);
}
