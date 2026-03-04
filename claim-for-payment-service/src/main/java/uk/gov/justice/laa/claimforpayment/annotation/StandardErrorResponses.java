package uk.gov.justice.laa.claimforpayment.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.ProblemDetail;

/** Standard error responses for API endpoints. */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthenticated",
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "403",
        description = "Unauthorised",
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "502",
        description = "Upstream service error",
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "504",
        description = "Upstream timeout",
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = @Content(
            mediaType = "application/problem+json",
            schema = @Schema(implementation = ProblemDetail.class)
        )
    )
})
public @interface StandardErrorResponses {}