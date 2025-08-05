package uk.gov.justice.laa.claimforpayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.claimforpayment.exception.ClaimNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.SubmissionNotFoundException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.service.ClaimServiceInterface;

/** REST controller for managing claims. */
@Slf4j
@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Tag(name = "Claims", description = "Operations related to provider claims")
public class ClaimController {

  private final ClaimServiceInterface claimService;

  /**
   * Creates a new claim.
   *
   * @param requestBody the claim input data
   * @return a response entity with the location of the created claim
   */
  @Operation(summary = "Create a new claim")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Claim created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
      })
  @PostMapping("/{submissionId}/claims")
  public ResponseEntity<Void> createClaim(
      @Parameter(description = "ID of the parent submission", required = true) @PathVariable
          UUID submissionId,
      @Parameter(description = "Claim input data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody) {

    log.debug("Creating new claim with submission ID: {}", submissionId);
    Long claimId = claimService.createClaim(submissionId, requestBody);
    URI location =
        URI.create(String.format("/api/v1/submissions/%s/claims/", submissionId) + claimId);
    return ResponseEntity.created(location).build();
  }

  /**
   * Retrieves all claims for a submission.
   *
   * @return a list of all claims for a submission
   */
  @Operation(summary = "Get all claims for the given submission")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of claims attached to a submission",
            content = @Content(schema = @Schema(implementation = Claim.class)))
      })
  @GetMapping("/{submissionId}/claims")
  public ResponseEntity<List<Claim>> getClaims(
      @Parameter(description = "ID of the submission", required = true) @PathVariable
          UUID submissionId) {
    log.debug("Fetching all claims");
    List<Claim> claims = claimService.getClaims(submissionId);
    return ResponseEntity.ok(claims);
  }

  /**
   * Retrieves a claim by its ID.
   *
   * @param submissionId the ID of the parent submission
   * @param claimId the ID of the claim to retrieve
   * @return the claim with the specified ID
   */
  @Operation(summary = "Get a claim by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Claim found",
            content = @Content(schema = @Schema(implementation = Claim.class))),
        @ApiResponse(responseCode = "404", description = "Claim not found", content = @Content)
      })
  @GetMapping("/{submissionId}/claims/{claimId}")
  public ResponseEntity<Claim> getClaim(
      @Parameter(description = "ID of the parent submission", required = true) @PathVariable
          UUID submissionId,
      @Parameter(description = "ID of the claim to retrieve", required = true) @PathVariable
          Long claimId) {

    log.debug("Fetching claim with ID: {}", claimId);
    Claim claim = claimService.getClaim(submissionId, claimId);
    return ResponseEntity.ok(claim);
  }

  /**
   * Updates an existing claim by its ID.
   *
   * @param id the ID of the claim to update
   * @param requestBody the updated claim data
   * @return a response entity with no content if update is successful
   */
  @Operation(summary = "Update a claim")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Claim updated successfully"),
        @ApiResponse(responseCode = "404", description = "Claim not found", content = @Content)
      })
  @PutMapping("/{submissionId}/claims/{id}")
  public ResponseEntity<Void> updateClaim(
      @Parameter(description = "ID of the parent submission", required = true) @PathVariable
          UUID submissionId,
      @Parameter(description = "ID of the claim to update", required = true) @PathVariable Long id,
      @Parameter(description = "Updated claim data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody) {

    log.debug("Updating claim with ID: {}", id);
    try {
      claimService.updateClaim(submissionId, id, requestBody);
    } catch (ClaimNotFoundException e) {
      log.debug("Claim not found for ID {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (SubmissionNotFoundException e) {
      log.debug("Submission not found for ID {}: {}", submissionId, e.getMessage());
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes a claim by its ID.
   *
   * @param claimId the ID of the claim to delete
   * @return a response entity with no content if deletion is successful
   */
  @Operation(summary = "Delete a claim")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Claim deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Claim not found", content = @Content)
      })
  @DeleteMapping("/{submissionId}/claims/{claimId}")
  public ResponseEntity<Void> deleteClaim(
      @Parameter(description = "ID of the parent submission", required = true) @PathVariable
          UUID submissionId,
      @Parameter(description = "ID of the claim to delete", required = true) @PathVariable
          Long claimId) {

    log.debug("Deleting claim with ID: {}", claimId);
    System.out.println(
        "Deleting claim with submission id "
            + submissionId.toString()
            + " and claim id "
            + claimId);
    try {
      claimService.deleteClaim(submissionId, claimId);
    } catch (ClaimNotFoundException e) {
      log.debug("Claim not found for ID {}: {}", claimId, e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (SubmissionNotFoundException e) {
      log.debug("Submission not found for ID {}: {}", submissionId, e.getMessage());
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }
}
