package uk.gov.justice.laa.claimforpayment.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.gov.justice.laa.claimforpayment.api.ApiErrorResponse;

/**
 * Standard error responses aligned with GlobalExceptionHandler.
 *
 * <p>This annotation provides a centralized way to document the RFC 7807 Problem Details
 * returned by the API, including LAA-specific error codes and correlation IDs.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = """
            **Invalid Request**
            * **Codes**: `VALIDATION_FAILED`, `BAD_REQUEST`
            * Occurs when the request payload fails validation or is malformed.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "401",
        description = """
            **Unauthenticated**
            * **Codes**: `UNAUTHENTICATED`
            * Authentication is missing or the provided token is invalid.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "403",
        description = """
            **Forbidden**
            * **Codes**: `FORBIDDEN`
            * The user is authenticated but lacks the required OAuth scopes or permissions.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "404",
        description = """
            **Not Found**
            * **Codes**: `NOT_FOUND`
            * The requested resource (e.g., Claim ID) does not exist.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "409",
        description = """
            **Conflict**
            * **Codes**: `CONFLICT`
            * The request conflicts with the current state of the resource in an upstream system.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "429",
        description = """
            **Too Many Requests**
            * **Codes**: `TEMPORARILY_UNAVAILABLE`
            * An upstream service is currently rate-limiting requests.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = """
            **Internal Server Error**
            * **Codes**: `INTERNAL_ERROR`, `SERVICE_ERROR`
            * An unexpected error occurred within this service.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "502",
        description = """
            **Upstream Service Error**
            * **Codes**: `UPSTREAM_ERROR`
            * A dependent upstream service returned an invalid or error response.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "504",
        description = """
            **Upstream Timeout**
            * **Codes**: `UPSTREAM_TIMEOUT`
            * A dependent upstream service failed to respond within the allowed time.
            """,
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ApiErrorResponse.class)
        )
    )
})
public @interface StandardErrorResponses {}