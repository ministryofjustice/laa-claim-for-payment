package uk.gov.justice.laa.claimforpayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.claimforpayment.exception.SubmissionNotFoundException;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.model.SubmissionRequestBody;
import uk.gov.justice.laa.claimforpayment.security.ProviderUserPrincipal;
import uk.gov.justice.laa.claimforpayment.service.ClaimServiceInterface;

/** REST controller for managing submissions. */
@Slf4j
@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Operations related to provider submissions")
public class SubmissionController {

  private final ClaimServiceInterface claimService;

  /**
   * Retrieves a submission by its ID.
   *
   * @param id the ID of the submission to retrieve
   * @return the submission with the specified ID
   */
  @Operation(summary = "Get a submission by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Submission found",
            content = @Content(schema = @Schema(implementation = Submission.class))),
        @ApiResponse(responseCode = "404", description = "Submssions not found", content = @Content)
      })
  @GetMapping("/{id}")
  public ResponseEntity<Submission> getSubmission(
      @Parameter(description = "ID of the submission to retrieve", required = true) @PathVariable
          UUID id,
      @RequestParam(name = "includeTotals", defaultValue = "false") boolean includeTotals) {

    log.debug("Fetching submission with ID: {}", id);
    Submission submission = claimService.getSubmission(id, includeTotals);
    return ResponseEntity.ok(submission);
  }

  /**
   * Retrieves all submissions for a logged-in provider user.
   *
   * @return a list of all submissions for that provider user
   */
  @Operation(summary = "Get all submissions for the logged-in provider user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of submissions for the logged-in provider user",
            content =
                @Content(
                    array = @ArraySchema(schema = @Schema(implementation = Submission.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
      })
  @GetMapping
  public ResponseEntity<List<Submission>> getAllSubmissionsForProvider(
      @AuthenticationPrincipal ProviderUserPrincipal principal,
      @RequestParam(name = "includeTotals", defaultValue = "false") boolean includeTotals) {

    UUID providerUserId = principal.providerUserId();
    log.debug("Fetching all submissions for provider user " + providerUserId);

    List<Submission> submissions =
        claimService.getAllSubmissionsForProvider(providerUserId, includeTotals);
    return ResponseEntity.ok(submissions);
  }

  /**
   * Creates a new submission.
   *
   * @param submissionRequestBody the submission data to create
   * @return a response indicating the creation status
   * @throws IllegalArgumentException if the submission data is invalid
   */
  @Operation(summary = "Create a new submission")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Submission created successfully",
            content = @Content(schema = @Schema(implementation = Submission.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
      })
  @PostMapping
  public ResponseEntity<Void> createSubmission(
      @Parameter(description = "Submission input data", required = true) @Valid @RequestBody
          SubmissionRequestBody submissionRequestBody,
      @AuthenticationPrincipal ProviderUserPrincipal principal) {
    UUID providerUserId = principal.providerUserId();
    log.debug("Creating new submission for provider user {}", providerUserId);
    UUID submissionId = claimService.createSubmission(submissionRequestBody);
    URI location = URI.create(String.format("/api/v1/submissions/%s", submissionId));
    return ResponseEntity.created(location).build();
  }

  /**
   * Updates an existing submission.
   *
   * @param id the ID of the submission to update
   * @param submissionRequestBody the updated submission data
   * @return a response indicating the update status
   */
  @Operation(summary = "Update an existing submission")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Submission updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Submission not found",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
      })
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateSubmission(
      @Parameter(description = "ID of the submission to update", required = true) @PathVariable
          UUID id,
      @Parameter(description = "Updated submission data", required = true) @Valid @RequestBody
          SubmissionRequestBody submissionRequestBody) {
    log.debug("Updating submission with ID: {}", id);

    try {
      claimService.updateSubmission(id, submissionRequestBody);
    } catch (SubmissionNotFoundException e) {
      log.debug("Submission not found for ID {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes a submission.
   *
   * @param id the ID of the submission to delete
   * @return a response indicating the deletion status
   */
  @Operation(summary = "Delete a submission")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Submission deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "Submission not found",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSubmission(
      @Parameter(description = "ID of the submission to delete", required = true) @PathVariable
          UUID id) {
    log.debug("Deleting submission with ID: {}", id);
    try {
      claimService.deleteSubmission(id);
    } catch (SubmissionNotFoundException e) {
      log.debug("Submission not found for ID {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }
}
