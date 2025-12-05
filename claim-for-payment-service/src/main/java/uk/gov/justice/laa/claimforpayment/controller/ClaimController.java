package uk.gov.justice.laa.claimforpayment.controller;

import static org.springframework.http.HttpStatus.FORBIDDEN;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.claimforpayment.exception.ClaimNotFoundException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.service.ClaimServiceInterface;

/** REST controller for managing claims. */
@Slf4j
@RestController
@RequestMapping("/api/v1/claims")
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
  @PostMapping
  public ResponseEntity<Void> createClaim(
      @Parameter(description = "Claim input data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody,
      @AuthenticationPrincipal Jwt jwt) {

    String id = jwt.getClaimAsString("USER_NAME");
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(FORBIDDEN, "providerUserId missing in token");
    }
    UUID providerUserId = UUID.fromString(id);

    Long claimId = claimService.createClaim(requestBody, providerUserId);
    URI location = URI.create("/api/v1/claims/" + claimId);
    return ResponseEntity.created(location).build();
  }

  /**
   * Retrieves all claims for the user.
   *
   * @return a list of all claims for the user
   */
  @Operation(summary = "Get all claims for the authenticated user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of claims linked to a provider user",
            content = @Content(schema = @Schema(implementation = Claim.class)))
      })
  @PreAuthorize("hasAuthority('SCOPE_Claims.Write')")
  @GetMapping
  public ResponseEntity<List<Claim>> getClaims(@AuthenticationPrincipal Jwt jwt) {

    String id = jwt.getClaimAsString("USER_NAME");
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(FORBIDDEN, "providerUserId missing in token");
    }
    UUID providerUserId = UUID.fromString(id);
    log.debug("Fetching all claims for provider user " + providerUserId);

    List<Claim> claims = claimService.getAllClaimsForProvider(providerUserId);

    return ResponseEntity.ok(claims);
  }

  /**
   * Retrieves a claim by its ID.
   *
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
  @GetMapping("/{claimId}")
  public ResponseEntity<Claim> getClaim(
      @Parameter(description = "ID of the claim to retrieve", required = true) @PathVariable
          Long claimId) {

    log.debug("Fetching claim with ID: {}", claimId);
    Claim claim = claimService.getClaim(claimId);
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
    try {
      claimService.updateClaim(id, requestBody);
    } catch (ClaimNotFoundException e) {
      log.debug("Claim not found for ID {}: {}", id, e.getMessage());
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
  @DeleteMapping("/{claimId}")
  public ResponseEntity<Void> deleteClaim(
      @Parameter(description = "ID of the claim to delete", required = true) @PathVariable
          Long claimId) {

    log.debug("Deleting claim with ID: {}", claimId);
    System.out.println("Deleting claim with claim id " + claimId);
    try {
      claimService.deleteClaim(claimId);
    } catch (ClaimNotFoundException e) {
      log.debug("Claim not found for ID {}: {}", claimId, e.getMessage());
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }
}
