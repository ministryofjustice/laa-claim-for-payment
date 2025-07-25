package uk.gov.justice.laa.claimforpayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

/** REST controller for managing claims. */
@Slf4j
@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

  private final ClaimService claimService;

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
  @PostMapping
  public ResponseEntity<Void> createClaim(
      @Parameter(description = "Claim input data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody) {

    log.debug("Creating new claim with submission ID: {}", requestBody.getSubmissionId());
    Long claimId = claimService.createClaim(requestBody);
    URI location = URI.create("/api/v1/claims/" + claimId);
    return ResponseEntity.created(location).build();
  }

  /**
   * Retrieves all claims.
   *
   * @return a list of all claims
   */
  @Operation(summary = "Get all claims")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of claims",
            content = @Content(schema = @Schema(implementation = Claim.class)))
      })
  @GetMapping
  public ResponseEntity<List<Claim>> getAllClaims() {
    log.debug("Fetching all claims");
    List<Claim> claims = claimService.getAllClaims();
    return ResponseEntity.ok(claims);
  }

  /**
   * Retrieves a claim by its ID.
   *
   * @param id the ID of the claim to retrieve
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
  @GetMapping("/{id}")
  public ResponseEntity<Claim> getClaim(
      @Parameter(description = "ID of the claim to retrieve", required = true) @PathVariable
          Long id) {

    log.debug("Fetching claim with ID: {}", id);
    Claim claim = claimService.getClaim(id);
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
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateClaim(
      @Parameter(description = "ID of the claim to update", required = true) @PathVariable Long id,
      @Parameter(description = "Updated claim data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody) {

    log.debug("Updating claim with ID: {}", id);
    claimService.updateClaim(id, requestBody);
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes a claim by its ID.
   *
   * @param id the ID of the claim to delete
   * @return a response entity with no content if deletion is successful
   */
  @Operation(summary = "Delete a claim")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Claim deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Claim not found", content = @Content)
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClaim(
      @Parameter(description = "ID of the claim to delete", required = true) @PathVariable
          Long id) {

    log.debug("Deleting claim with ID: {}", id);
    claimService.deleteClaim(id);
    return ResponseEntity.noContent().build();
  }
}
