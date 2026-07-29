package uk.gov.justice.laa.claimforpayment.controller;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.justice.laa.claimforpayment.annotation.StandardErrorResponses;
import uk.gov.justice.laa.claimforpayment.api.UploadError;
import uk.gov.justice.laa.claimforpayment.api.UploadEvidenceRequest;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.api.UploadResponse;
import uk.gov.justice.laa.claimforpayment.api.UploadSuccess;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.ClaimStatus;
import uk.gov.justice.laa.claimforpayment.model.LineItem;
import uk.gov.justice.laa.claimforpayment.model.LineItemRequestBody;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;
import uk.gov.justice.laa.claimforpayment.service.ClaimServiceInterface;
import uk.gov.justice.laa.claimforpayment.service.DraftClaimService;

/** REST controller for managing claims. */
@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Claims", description = "Operations related to provider claims")
public class ClaimController {

  private final ClaimService claimService;
  private final DraftClaimService draftService;

  /**
   * Creates a new claim.
   *
   * @param requestBody the claim input data
   * @return a response entity with the location of the created claim
   */
  @Operation(summary = "Create a new claim")
  @ApiResponse(
      responseCode = "201",
      description = "Claim created successfully",
      headers = {
        @Header(
            name = "Location",
            description = "URI of the created claim resource",
            schema = @Schema(type = "string", example = "/api/v1/claims/123"))
      })
  @StandardErrorResponses
  @PostMapping
  public ResponseEntity<Void> createClaim(
      @Parameter(description = "Claim input data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody,
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(name = "status") ClaimStatus status) {
    UUID providerUserId = getProviderUserId(jwt);
    UUID claimId = callService(status, service -> service.createClaim(requestBody, providerUserId));
    URI location =
        UriComponentsBuilder.fromPath("/api/v1/claims/{id}")
            .queryParam("status", status.name())
            .buildAndExpand(claimId)
            .toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * Retrieves all claims for the user.
   *
   * @return a list of all claims for the user
   */
  @Operation(summary = "Get all claims for the authenticated user")
  @ApiResponse(
      responseCode = "200",
      description = "List of claims linked to a provider user",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ClaimPage.class)))
  @StandardErrorResponses
  @PreAuthorize("hasAuthority(@authProps.getClaimsWrite())")
  @GetMapping
  public ResponseEntity<ClaimPage> getClaims(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(name = "page", defaultValue = "0") @Min(0) @Max(10000) Integer page,
      @RequestParam(name = "limit", defaultValue = "10000") @Min(0) @Max(100000) Integer limit,
      @RequestParam(name = "status") ClaimStatus status) {
    UUID providerUserId = getProviderUserId(jwt);
    log.debug("Fetching all claims for provider user " + providerUserId);
    ClaimPage claimPage = callService(status, service -> service.getClaims(page, limit));
    return ResponseEntity.ok(claimPage);
  }

  /**
   * Retrieves a claim by its ID.
   *
   * @param claimId the ID of the claim to retrieve
   * @return the claim with the specified ID
   */
  @Operation(summary = "Get a claim by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Claim found",
      content = @Content(schema = @Schema(implementation = Claim.class)))
  @StandardErrorResponses
  @GetMapping("/{claimId}")
  public ResponseEntity<Claim> getClaim(
      @Parameter(description = "ID of the claim to retrieve", required = true)
          @PathVariable("claimId")
          UUID claimId,
      @RequestParam(name = "status") ClaimStatus status) {
    log.debug("Fetching {} claim with ID: {}", status.name(), claimId);
    Claim claim = callService(status, service -> service.getClaim(claimId));
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
  @ApiResponse(responseCode = "204", description = "Claim updated successfully")
  @StandardErrorResponses
  @ApiResponse(responseCode = "204", description = "Claim updated successfully")
  @PreAuthorize("hasAuthority(@authProps.getClaimsWrite())")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateClaim(
      @AuthenticationPrincipal Jwt jwt,
      @Parameter(description = "ID of the claim to update", required = true) @PathVariable("id")
          UUID id,
      @Parameter(description = "Updated claim data", required = true) @Valid @RequestBody
          ClaimRequestBody requestBody,
      @RequestParam(name = "status") ClaimStatus status) {
    UUID providerUserId = getProviderUserId(jwt);
    log.debug("Updating {} claim with ID: {}", status.name(), id);
    callService(
        status,
        service -> {
          service.updateClaim(id, requestBody, providerUserId);
          return null;
        });
    return ResponseEntity.noContent().build();
  }

  /**
   * Deletes a claim by its ID.
   *
   * @param claimId the ID of the claim to delete
   * @return a response entity with no content if deletion is successful
   */
  @Operation(summary = "Delete a claim")
  @ApiResponse(responseCode = "204", description = "Claim deleted successfully")
  @StandardErrorResponses
  @DeleteMapping("/{claimId}")
  public ResponseEntity<Void> deleteClaim(
      @Parameter(description = "ID of the claim to delete", required = true)
          @PathVariable("claimId")
          UUID claimId,
      @RequestParam(name = "status") ClaimStatus status) {
    log.debug("Deleting claim with ID: {}", claimId);
    callService(
        status,
        service -> {
          service.deleteClaim(claimId);
          return null;
        });
    return ResponseEntity.noContent().build();
  }

  /** Uploads evidence files for a specific claim. */
  @Operation(summary = "Upload evidence files for a claim")
  @ApiResponse(responseCode = "204", description = "Evidence files uploaded successfully")
  @StandardErrorResponses
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Multipart form data containing the evidence file",
      required = true,
      content =
          @Content(
              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(implementation = UploadEvidenceRequest.class)))
  @PostMapping(value = "/{claimId}/upload-evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UploadResponse> uploadClaimEvidence(
      @Parameter(description = "ID of the claim to add evidence to", required = true)
          @PathVariable("claimId")
          UUID claimId,
      @RequestPart("documents") MultipartFile multipartFile,
      @RequestParam(name = "status") ClaimStatus status) {
    UploadFile uploadFile = new UploadFile(multipartFile);
    try {
      UUID evidenceId =
          callService(status, service -> service.addEvidenceToClaim(claimId, uploadFile));
      String message = String.format("File uploaded with ID: %s", evidenceId);
      UploadSuccess response = new UploadSuccess(evidenceId, uploadFile, message);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception ex) {
      String message = String.format("Failed to upload file: %s", ex.getMessage());
      UploadError response = new UploadError(uploadFile, message);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  /** Links evidence to a line item. */
  @Operation(summary = "Link evidence to line item")
  @ApiResponse(responseCode = "204", description = "Evidence linked to line item")
  @StandardErrorResponses
  @PostMapping("/{claimId}/line-items/{lineItemId}/evidence")
  public ResponseEntity<Void> linkEvidenceToLineItem(
      @Parameter(description = "ID of the claim", required = true) @PathVariable("claimId")
          UUID claimId,
      @Parameter(description = "ID of the line item to link to", required = true)
          @PathVariable("lineItemId")
          UUID lineItemId,
      @Parameter(description = "IDs of the evidence to link", required = true) @Valid @RequestBody
          List<UUID> evidenceIds) {

    claimService.linkEvidenceToLineItem(claimId, lineItemId, evidenceIds);
    return ResponseEntity.noContent().build();
  }

  /** Uploads evidence files for a specific line item. */
  @Operation(summary = "Upload evidence files for a specific line item.")
  @ApiResponse(responseCode = "204", description = "Evidence files uploaded successfully")
  @StandardErrorResponses
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Multipart form data containing the evidence file",
      required = true,
      content =
          @Content(
              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(implementation = UploadEvidenceRequest.class)))
  @PostMapping(
      value = "/{claimId}/line-items/{lineItemId}/upload-evidence",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UploadResponse> uploadLineItemEvidence(
      @Parameter(description = "ID of the claim to add evidence to", required = true)
          @PathVariable("claimId")
          UUID claimId,
      @Parameter(description = "ID of the line item to add evidence to", required = true)
          @PathVariable("lineItemId")
          UUID lineItemId,
      @RequestPart("documents") MultipartFile multipartFile) {
    UploadFile uploadFile = new UploadFile(multipartFile);
    try {
      UUID evidenceId = claimService.addEvidenceToClaim(claimId, uploadFile);
      claimService.linkEvidenceToLineItem(claimId, lineItemId, List.of(evidenceId));
      String message =
          String.format(
              "File uploaded with ID: %s and linked to line item: %s", evidenceId, lineItemId);
      UploadSuccess response = new UploadSuccess(evidenceId, uploadFile, message);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception ex) {
      String message = String.format("Failed to upload file: %s", ex.getMessage());
      UploadError response = new UploadError(uploadFile, message);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }

  /** Deletes evidence from a claim. */
  @Operation(summary = "Delete evidence from a claim")
  @ApiResponse(responseCode = "204", description = "Evidence deleted from claim")
  @StandardErrorResponses
  @DeleteMapping("/{claimId}/evidence/{evidenceId}")
  public ResponseEntity<Void> deleteEvidenceFromClaim(
      @Parameter(description = "ID of the claim", required = true) @PathVariable("claimId")
          UUID claimId,
      @Parameter(description = "ID of the evidence to delete", required = true)
          @PathVariable("evidenceId")
          UUID evidenceId,
      @RequestParam("status") ClaimStatus status) {

    callService(
        status,
        service -> {
          service.deleteEvidenceFromClaim(claimId, evidenceId);
          return null;
        });
    return ResponseEntity.noContent().build();
  }

  /** Unlinks evidence from a line item. */
  @Operation(summary = "Unlink evidence from line item")
  @ApiResponse(responseCode = "204", description = "Evidence unlinked from line item")
  @StandardErrorResponses
  @DeleteMapping("/{claimId}/line-items/{lineItemId}/evidence/{evidenceId}")
  public ResponseEntity<Void> unlinkEvidenceFromLineItem(
      @Parameter(description = "ID of the claim", required = true) @PathVariable("claimId")
          UUID claimId,
      @Parameter(description = "ID of the line item to unlink from", required = true)
          @PathVariable("lineItemId")
          UUID lineItemId,
      @Parameter(description = "ID of the evidence to unlink", required = true)
          @PathVariable("evidenceId")
          UUID evidenceId) {

    claimService.unlinkEvidenceFromLineItem(claimId, lineItemId, evidenceId);
    return ResponseEntity.noContent().build();
  }

  /** Gets line item by claim id and line item id. */
  @Operation(summary = "Get a line item by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Line item found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = LineItem.class)))
  @GetMapping("/{claimId}/line-items/{lineItemId}")
  public ResponseEntity<LineItem> getLineItem(
      @PathVariable("claimId") UUID claimId,
      @PathVariable("lineItemId") UUID lineItemId,
      @RequestParam(name = "status") ClaimStatus status) {

    Claim claim = callService(status, service -> service.getClaim(claimId));
    LineItem lineItem =
        claim.getLineItems().stream()
            .filter(li -> li.getId().equals(lineItemId))
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Line item not found."));
    return ResponseEntity.ok(lineItem);
  }

  /** Adds a line item to a claim by id. */
  @Operation(summary = "Add a line item to a claim")
  @ApiResponse(
      responseCode = "201",
      description = "Line item created successfully",
      headers = {
        @Header(
            name = "Location",
            description = "URI of the created line item resource",
            schema = @Schema(type = "string", example = "/api/v1/claims/123/line-items/456"))
      })
  @PostMapping("/{claimId}/line-items")
  public ResponseEntity<Void> addLineItemToClaim(
      @PathVariable("claimId") UUID claimId,
      @RequestParam("status") ClaimStatus status,
      @Parameter(description = "lineItem", required = true) @RequestBody
          LineItemRequestBody requestBody) {
    UUID lineItemId =
        callService(status, service -> service.addLineItemToClaim(claimId, requestBody));
    URI location =
        UriComponentsBuilder.fromPath("/api/v1/claims/{claimId}/line-items/{lineItemId}")
            .buildAndExpand(claimId, lineItemId)
            .toUri();
    return ResponseEntity.created(location).build();
  }

  /** Updates a line item on a claim by claimId and lineItemId. */
  @Operation(summary = "Update a line item on a claim")
  @ApiResponse(responseCode = "204", description = "Line item updated successfully")
  @StandardErrorResponses
  @PutMapping("/{claimId}/line-items/{lineItemId}")
  public ResponseEntity<Void> updateLineItem(
      @PathVariable("claimId") UUID claimId,
      @RequestParam("status") ClaimStatus status,
      @PathVariable("lineItemId") UUID lineItemId,
      @Parameter(description = "lineItem", required = true) @RequestBody
          LineItemRequestBody requestBody) {
    callService(
        status,
        service -> {
          service.updateLineItem(claimId, lineItemId, requestBody);
          return null;
        });
    return ResponseEntity.noContent().build();
  }

  /** Deletes a line item from a claim. */
  @Operation(summary = "Delete a line item from a claim")
  @ApiResponse(responseCode = "204", description = "Line item deleted successfully")
  @StandardErrorResponses
  @DeleteMapping("/{claimId}/line-items/{lineItemId}")
  public ResponseEntity<Void> deleteLineItem(
      @PathVariable("claimId") UUID claimId,
      @RequestParam("status") ClaimStatus status,
      @PathVariable("lineItemId") UUID lineItemId) {
    callService(
        status,
        service -> {
          service.deleteLineItem(claimId, lineItemId);
          return null;
        });
    return ResponseEntity.noContent().build();
  }

  private <T> T callService(ClaimStatus status, Function<ClaimServiceInterface, T> action) {
    return switch (status) {
      case DRAFT -> action.apply(draftService);
      case SUBMITTED -> action.apply(claimService);
    };
  }

  private UUID getProviderUserId(Jwt jwt) {
    String id = jwt.getClaimAsString("USER_NAME");
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(FORBIDDEN, "providerUserId missing in token");
    }
    return UUID.fromString(id);
  }
}
