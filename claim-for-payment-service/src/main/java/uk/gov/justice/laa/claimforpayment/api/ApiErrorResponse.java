package uk.gov.justice.laa.claimforpayment.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Generic API error response model following RFC 7807 Problem Details format, used for apispec.
 */
@Data
public class ApiErrorResponse {
  @Schema(description = "High-level error type", example = "about:blank")
  private String title;

  @Schema(description = "HTTP status code", example = "400")
  private int status;

  @Schema(description = "Human-readable explanation")
  private String detail;

  @Schema(description = "Internal error code for machine consumption", example = "ERROR_CODE")
  private String errorCode;

  @Schema(description = "Unique ID for log correlation", example = "uuid")
  private String correlationId;
}
