package uk.gov.justice.laa.claimforpayment.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.entity.SubmissionEntity;
import uk.gov.justice.laa.claimforpayment.exception.ClaimNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.SubmissionNotFoundException;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimMapper;
import uk.gov.justice.laa.claimforpayment.mapper.SubmissionMapper;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.model.SubmissionRequestBody;
import uk.gov.justice.laa.claimforpayment.repository.ClaimRepository;
import uk.gov.justice.laa.claimforpayment.repository.SubmissionRepository;

/** Service class for handling claims requests. */
@RequiredArgsConstructor
@Service
public class DatabaseBasedClaimService implements ClaimServiceInterface {

  private final ClaimRepository claimRepository;
  private final SubmissionRepository submissionRepository;
  private final ClaimMapper claimMapper;
  private final SubmissionMapper submissionMapper;

  /**
   * Gets all claims.
   *
   * @param submissionId the ID of the submission
   * @return the list of claims
   */
  @Override
  public List<Claim> getClaims(UUID submissionId) {
    return claimRepository.findBySubmissionId(submissionId).stream()
        .map(claimMapper::toClaim)
        .toList();
  }

  /**
   * Gets a claim for a given id.
   *
   * @param claimId the claim id
   * @return the requested claim
   */
  @Override
  public Claim getClaim(UUID submissionId, Long claimId) {
    ClaimEntity claimEntity = checkIfClaimExist(claimId);
    return claimMapper.toClaim(claimEntity);
  }

  /**
   * Gets a submission by its ID.
   *
   * @param submissionId the ID of the submission
   * @return the submission
   */
  @Override
  public Submission getSubmission(UUID submissionId, boolean includeTotals) {

    Optional<SubmissionEntity> foundSubmission = submissionRepository.findById(submissionId);

    return foundSubmission
        .map(
            submissionEntity -> {
              Submission submission = submissionMapper.toSubmission(submissionEntity);
              if (includeTotals) {
                return submission.toBuilder()
                    .totalClaimed(submissionRepository.findSubmissionTotalById(submissionId))
                    .build();
              } else {
                return submission;
              }
            })
        .orElseThrow(
            () -> new ClaimNotFoundException("Submission not found with id: " + submissionId));
  }

  /**
   * Creates a new submission.
   *
   * @param submissionRequestBody the request body containing submission details
   * @return the ID of the created submission
   */
  @Override
  public UUID createSubmission(SubmissionRequestBody submissionRequestBody) {
    SubmissionEntity submissionEntity = new SubmissionEntity();
    submissionEntity.setProviderUserId(submissionRequestBody.getProviderUserId());
    submissionEntity.setScheduleId(submissionRequestBody.getScheduleId());
    submissionEntity.setProviderOfficeId(submissionRequestBody.getProviderOfficeId());
    submissionEntity.setSubmissionTypeCode(submissionRequestBody.getSubmissionTypeCode());
    submissionEntity.setSubmissionDate(submissionRequestBody.getSubmissionDate());
    submissionEntity.setSubmissionPeriodStartDate(
        submissionRequestBody.getSubmissionPeriodStartDate());
    submissionEntity.setSubmissionPeriodEndDate(submissionRequestBody.getSubmissionPeriodEndDate());

    SubmissionEntity createdSubmission = submissionRepository.save(submissionEntity);

    return createdSubmission.getId();
  }

  /**
   * Creates a claim.
   *
   * @param submissionId the id of the parent submission
   * @param claimRequestBody the claim to be created
   * @return the id of the created claim
   */
  @Override
  public Long createClaim(UUID submissionId, ClaimRequestBody claimRequestBody) {
    SubmissionEntity submissionEntity = checkIfSubmissionExist(submissionId);
    ClaimEntity claimEntity = new ClaimEntity();
    claimEntity.setUfn(claimRequestBody.getUfn());
    claimEntity.setClient(claimRequestBody.getClient());
    claimEntity.setCategory(claimRequestBody.getCategory());
    claimEntity.setConcluded(claimRequestBody.getConcluded());
    claimEntity.setFeeType(claimRequestBody.getFeeType());
    claimEntity.setClaimed(claimRequestBody.getClaimed());
    claimEntity.setSubmission(submissionEntity);

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
  public void updateClaim(UUID submissionId, Long id, ClaimRequestBody claimRequestBody) {
    checkIfSubmissionExist(submissionId);
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
   * Creates a new submission.
   *
   * @param submissionRequestBody the request body containing submission details
   */
  @Override
  public void updateSubmission(UUID id, SubmissionRequestBody submissionRequestBody) {
    SubmissionEntity submissionEntity = checkIfSubmissionExist(id);
    submissionEntity.setProviderUserId(submissionRequestBody.getProviderUserId());
    submissionEntity.setScheduleId(submissionRequestBody.getScheduleId());
    submissionEntity.setProviderOfficeId(submissionRequestBody.getProviderOfficeId());
    submissionEntity.setSubmissionTypeCode(submissionRequestBody.getSubmissionTypeCode());
    submissionEntity.setSubmissionDate(submissionRequestBody.getSubmissionDate());
    submissionEntity.setSubmissionPeriodStartDate(
        submissionRequestBody.getSubmissionPeriodStartDate());
    submissionEntity.setSubmissionPeriodEndDate(submissionRequestBody.getSubmissionPeriodEndDate());
    submissionRepository.save(submissionEntity);
  }

  /**
   * Deletes a submission.
   *
   * @param id the id of the submission to be deleted
   */
  @Override
  public void deleteSubmission(UUID id) {
    checkIfSubmissionExist(id);
    submissionRepository.deleteById(id);
  }

  /**
   * Deletes a claim.
   *
   * @param submissionId the id of the parent submission
   * @param id the id of the claim to be deleted
   */
  @Override
  public void deleteClaim(UUID submissionId, Long id) {
    checkIfSubmissionExist(submissionId);
    checkIfClaimExist(id);
    claimRepository.deleteById(id);
  }

  private ClaimEntity checkIfClaimExist(Long id) {
    return claimRepository
        .findById(id)
        .orElseThrow(
            () -> new ClaimNotFoundException(String.format("No claim found with id: %s", id)));
  }

  private SubmissionEntity checkIfSubmissionExist(UUID id) {
    return submissionRepository
        .findById(id)
        .orElseThrow(
            () ->
                new SubmissionNotFoundException(
                    String.format("No submission found with id: %s", id)));
  }

  /**
   * Gets all submissions for a given provider user ID.
   *
   * @param providerUserId the ID of the provider user
   * @return a list of submissions for the provider user
   */
  @Override
  public List<Submission> getAllSubmissionsForProvider(UUID providerUserId, boolean includeTotals) {

    List<Submission> foundSubmissions =
        submissionRepository.findByProviderUserId(providerUserId).stream()
            .map(
                submissionEntity -> {
                  Submission submission = submissionMapper.toSubmission(submissionEntity);
                  if (includeTotals) {
                    return submission.toBuilder()
                        .totalClaimed(
                            submissionRepository.findSubmissionTotalById(submission.getId()))
                        .build();
                  } else {
                    return submission;
                  }
                })
            .toList();
    return foundSubmissions;
  }
}
