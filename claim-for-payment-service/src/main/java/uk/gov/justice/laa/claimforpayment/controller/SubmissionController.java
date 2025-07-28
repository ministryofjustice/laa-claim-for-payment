package uk.gov.justice.laa.claimforpayment.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

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

    log.debug("Fetching claim with ID: {}", id);
    Submission submission = claimService.getSubmission(id);
    return ResponseEntity.ok(submission);
  }

}
