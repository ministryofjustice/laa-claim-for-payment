package uk.gov.justice.laa.claimforpayment.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.claimforpayment.annotation.StandardErrorResponses;
import uk.gov.justice.laa.claimforpayment.model.CaseDto;
import uk.gov.justice.laa.claimforpayment.model.ClaimStatus;
import uk.gov.justice.laa.claimforpayment.service.AccessDataService;

/** REST controller for managing cases. */
@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Tag(name = "Cases", description = "Operations related to Civil Cases")
public class CaseController {

  private final AccessDataService accessDataService;

  /**
   * Retrieves a case by its ID.
   *
   * @param caseId the ID of the case to retrieve
   * @return the case with the specified ID
   */
  @Operation(summary = "Get a case by ID")
  @ApiResponse(
      responseCode = "200",
      description = "Case found",
      content = @Content(schema = @Schema(implementation = CaseDto.class)))
  @StandardErrorResponses
  @GetMapping("/{caseId}")
  public ResponseEntity<CaseDto> getCase(
      @Parameter(description = "ID of the case to retrieve", required = true)
          @PathVariable("caseId")
          UUID caseId,
      @RequestParam(name = "status") ClaimStatus status) {
    log.debug("Fetching {} case with ID: {}", status.name(), caseId);
    CaseDto caseDto = accessDataService.getCase(caseId);
    return ResponseEntity.ok(caseDto);
  }
}
