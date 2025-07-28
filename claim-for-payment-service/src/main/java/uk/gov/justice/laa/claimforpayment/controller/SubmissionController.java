package uk.gov.justice.laa.claimforpayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;
import uk.gov.justice.laa.claimforpayment.security.ProviderUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

  private final ClaimService claimService;

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
          UUID id) {

    log.debug("Fetching submission with ID: {}", id);
    Submission submission = claimService.getSubmission(id);
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
      @AuthenticationPrincipal ProviderUserPrincipal principal) {

    UUID providerUserId = principal.providerUserId();
    log.debug("Fetching all submissions for provider user " + providerUserId);
    
    List<Submission> submissions = claimService.getAllSubmissionsForProvider(providerUserId);
    return ResponseEntity.ok(submissions);
  }
}
