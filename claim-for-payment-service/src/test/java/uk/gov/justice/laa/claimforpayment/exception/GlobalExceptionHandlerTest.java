package uk.gov.justice.laa.claimforpayment.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

  private static final String CORRELATION_HEADER = "X-Correlation-Id";
  private static final String MDC_CORRELATION_ID = "correlationId";
  private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

  private static final String SERVICE = "civil-claims";
  private static final String OPERATION = "GET /api/v1/claims";

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void handleResourceNotFound_shouldReturn404_andUseHeaderCorrelationId() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims/123", "corr-123");
    ResourceNotFoundException ex =
        new ResourceNotFoundException(SERVICE, OPERATION, new RuntimeException("nope"));

    ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, request);

    assertProblem(
        response,
        HttpStatus.NOT_FOUND,
        "Not found",
        // handler uses safeMessage(ex) here; safest assertion is "not blank"
        null,
        "/api/v1/claims/123",
        "corr-123",
        "NOT_FOUND",
        true);

    assertThat(MDC.get(MDC_CORRELATION_ID)).isEqualTo("corr-123");
  }

  @Test
  void handleResourceNotFound_shouldGenerateCorrelationId_whenHeaderMissing() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims/123", null);
    ResourceNotFoundException ex =
        new ResourceNotFoundException(SERVICE, OPERATION, new RuntimeException("nope"));

    ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getHeaders().getContentType()).isEqualTo(PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getProperties()).containsKey("correlationId");
    assertThat(body.getProperties()).containsKey("errorCode");
    assertThat(body.getProperties().get("errorCode")).isEqualTo("NOT_FOUND");

    String correlationId = (String) body.getProperties().get("correlationId");
    assertThat(correlationId).isNotBlank();
    assertThat(MDC.get(MDC_CORRELATION_ID)).isEqualTo(correlationId);
  }

  @Test
  void handleUpstreamValidation_shouldReturn400() {
    MockHttpServletRequest request = request("POST", "/api/v1/claims", "corr-400");
    UpstreamValidationException ex =
        new UpstreamValidationException(
            SERVICE, "POST /api/v1/claims", new RuntimeException("bad"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamValidation(ex, request);

    assertProblem(
        response,
        HttpStatus.BAD_REQUEST,
        "Invalid request",
        "The request could not be processed.",
        "/api/v1/claims",
        "corr-400",
        "VALIDATION_FAILED",
        false);
  }

  @Test
  void handleUpstreamConflict_shouldReturn409() {
    MockHttpServletRequest request = request("PUT", "/api/v1/claims/1", "corr-409");
    UpstreamConflictException ex =
        new UpstreamConflictException(
            SERVICE, "PUT /api/v1/claims/1", new RuntimeException("conflict"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamConflict(ex, request);

    assertProblem(
        response,
        HttpStatus.CONFLICT,
        "Conflict",
        "The request could not be completed due to a conflict.",
        "/api/v1/claims/1",
        "corr-409",
        "CONFLICT",
        false);
  }

  @Test
  void handleUpstreamUnauthorised_shouldReturn401() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-401");
    UpstreamUnauthorisedException ex =
        new UpstreamUnauthorisedException(SERVICE, OPERATION, new RuntimeException("unauth"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamUnauthorised(ex, request);

    assertProblem(
        response,
        HttpStatus.UNAUTHORIZED,
        "Unauthenticated",
        "Authentication is required.",
        "/api/v1/claims",
        "corr-401",
        "UNAUTHENTICATED",
        false);
  }

  @Test
  void handleUpstreamForbidden_shouldReturn403() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-403");
    UpstreamForbiddenException ex =
        new UpstreamForbiddenException(SERVICE, OPERATION, new RuntimeException("forbidden"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamForbidden(ex, request);

    assertProblem(
        response,
        HttpStatus.FORBIDDEN,
        "Forbidden",
        "You do not have permission to perform this action.",
        "/api/v1/claims",
        "corr-403",
        "FORBIDDEN",
        false);
  }

  @Test
  void handleUpstreamRateLimited_shouldReturn503() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-503");
    UpstreamRateLimitedException ex =
        new UpstreamRateLimitedException(SERVICE, OPERATION, new RuntimeException("rate"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamRateLimited(ex, request);

    assertProblem(
        response,
        HttpStatus.SERVICE_UNAVAILABLE,
        "Temporarily unavailable",
        "A dependent service is rate limiting requests. Please try again.",
        "/api/v1/claims",
        "corr-503",
        "TEMPORARILY_UNAVAILABLE",
        false);
  }

  @Test
  void handleUpstreamTimeout_shouldReturn504() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-504");
    UpstreamTimeoutException ex =
        new UpstreamTimeoutException(SERVICE, OPERATION, new RuntimeException("timeout"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamTimeout(ex, request);

    assertProblem(
        response,
        HttpStatus.GATEWAY_TIMEOUT,
        "Upstream timeout",
        "A dependent service did not respond in time.",
        "/api/v1/claims",
        "corr-504",
        "UPSTREAM_TIMEOUT",
        false);
  }

  @Test
  void handleUpstreamFailure_shouldReturn502_forUpstreamServiceException() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-502");
    UpstreamServiceException ex =
        new UpstreamServiceException(SERVICE, OPERATION, new RuntimeException("boom"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamFailure(ex, request);

    assertProblem(
        response,
        HttpStatus.BAD_GATEWAY,
        "Upstream service error",
        "A dependent service returned an error.",
        "/api/v1/claims",
        "corr-502",
        "UPSTREAM_ERROR",
        false);
  }

  @Test
  void handleUpstreamFailure_shouldReturn502_forUpstreamClientException() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-502");
    UpstreamClientException ex =
        new UpstreamClientException(SERVICE, OPERATION, new RuntimeException("boom"));

    ResponseEntity<ProblemDetail> response = handler.handleUpstreamFailure(ex, request);

    assertProblem(
        response,
        HttpStatus.BAD_GATEWAY,
        "Upstream service error",
        "A dependent service returned an error.",
        "/api/v1/claims",
        "corr-502",
        "UPSTREAM_ERROR",
        false);
  }

  @Test
  void handleServiceException_shouldReturn500() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-500");

    ServiceException ex =
        new ServiceException("Message", SERVICE, OPERATION, new RuntimeException("service")) {};

    ResponseEntity<ProblemDetail> response = handler.handleServiceException(ex, request);

    assertProblem(
        response,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Error",
        "The request could not be completed.",
        "/api/v1/claims",
        "corr-500",
        "SERVICE_ERROR",
        false);
  }

  @Test
  void handleUnexpected_shouldReturn500() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-500");
    Exception ex = new Exception("kaboom");

    ResponseEntity<ProblemDetail> response = handler.handleUnexpected(ex, request);

    assertProblem(
        response,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Unexpected error",
        "An unexpected error occurred.",
        "/api/v1/claims",
        "corr-500",
        "INTERNAL_ERROR",
        false);
  }

  @Test
  void handleNotAuthenticated_shouldReturn401() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-401");
    AuthenticationException ex =
        new AuthenticationException("Unauthenticated") {};
    ResponseEntity<ProblemDetail> response = handler.handleAuthenticationException(ex, request);

    assertProblem(
        response,
        HttpStatus.UNAUTHORIZED,
        "Unauthenticated",
        "Authentication is required.",
        "/api/v1/claims",
        "corr-401",
        "UNAUTHENTICATED",
        false);
  }

  @Test
  void handleAccessDenied_shouldReturn403() {
    MockHttpServletRequest request = request("GET", "/api/v1/claims", "corr-403");
    org.springframework.security.access.AccessDeniedException ex =
        new org.springframework.security.access.AccessDeniedException("Forbidden");
    ResponseEntity<ProblemDetail> response = handler.handleAccessDeniedException(ex, request);    
    assertProblem(
        response,
        HttpStatus.FORBIDDEN,
        "Forbidden",
        "You do not have the required permissions.",
        "/api/v1/claims",
        "corr-403",
        "FORBIDDEN",
        false);
  }

  @Test
  void handleConstraintViolation_shouldReturn400() {
    MockHttpServletRequest request = request("GET", "api/v1/claims", "corr-400");
    ConstraintViolationException ex = mock(ConstraintViolationException.class);
    ResponseEntity<ProblemDetail> response = handler.handleConstraintViolation(ex, request);
    assertProblem(
            response,
            HttpStatus.BAD_REQUEST,
            "Invalid request",
            "Request validation failed.",
            "api/v1/claims",
            "corr-400",
            "VALIDATION_FAILED",
            false);
  }

  @Test
  void handleMethodArgumentTypeMismatch_shouldReturn400() {
    MockHttpServletRequest request = request("GET", "api/v1/claims", "corr-400");
    MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
    ResponseEntity<ProblemDetail> response = handler.handleMethodArgumentTypeMismatch(ex, request);
    assertProblem(
            response,
            HttpStatus.BAD_REQUEST,
            "Invalid request",
            "Request validation failed.",
            "api/v1/claims",
            "corr-400",
            "VALIDATION_FAILED",
            false);
  }

  @Test
  void handleNoResourceFound_shouldReturn404() {
    MockHttpServletRequest request = request("GET", "api/v1/claims", "corr-404");
    NoResourceFoundException ex = mock(NoResourceFoundException.class);
    ResponseEntity<ProblemDetail> response = handler.handleNoResourceFound(ex, request);
    assertProblem(
            response,
            HttpStatus.NOT_FOUND,
            "Not found",
            "NoResourceFoundException",
            "api/v1/claims",
            "corr-404",
            "NOT_FOUND",
            false);
  }

  @Test
  void handleHttpMessageNotReadable_shouldReturn400() {
    MockHttpServletRequest request = request("POST", "api/v1/claims", "corr-400");
    org.springframework.http.converter.HttpMessageNotReadableException ex =
        mock(org.springframework.http.converter.HttpMessageNotReadableException.class);
    ResponseEntity<ProblemDetail> response = handler.handleHttpMessageNotReadable(ex, request);
    assertProblem(
            response,
            HttpStatus.BAD_REQUEST,
            "Invalid request",
            "Request validation failed.",
            "api/v1/claims",
            "corr-400",
            "VALIDATION_FAILED",
            false);
  }

  @Test
  void handleInvalidParameter_shouldReturn400() {
    MockHttpServletRequest request = request("POST", "api/v1/claims", "corr-400");
    InvalidParameterException ex =
            mock(InvalidParameterException.class);
    ResponseEntity<ProblemDetail> response = handler.handleInvalidParameter(ex, request);
    assertProblem(
            response,
            HttpStatus.BAD_REQUEST,
            "Invalid request",
            "Request validation failed.",
            "api/v1/claims",
            "corr-400",
            "VALIDATION_FAILED",
            false);
  }

  @Test
  void handleHttpRequestMethodNotSupported_shouldReturn405() {
    MockHttpServletRequest request = request("POST", "api/v1/claims", "corr-405");
    HttpRequestMethodNotSupportedException ex =
            mock(HttpRequestMethodNotSupportedException.class);
    ResponseEntity<ProblemDetail> response = handler.handleHttpMethodNotSupported(ex, request);
    assertProblem(
            response,
            HttpStatus.METHOD_NOT_ALLOWED,
            "Method not supported",
            "The HTTP method is not supported for this endpoint.",
            "api/v1/claims",
            "corr-405",
            "METHOD_NOT_ALLOWED",
            false);
  }

  @Test
  void handleHttpMediaTypeNotAcceptable_shouldReturn406() {
    MockHttpServletRequest request = request("GET", "api/v1/claims", "corr-406");
    org.springframework.web.HttpMediaTypeNotAcceptableException ex =
            mock(org.springframework.web.HttpMediaTypeNotAcceptableException.class);
    ResponseEntity<ProblemDetail> response = handler.handleHttpMediaTypeNotAcceptable(ex, request);
    assertProblem(
            response,
            HttpStatus.NOT_ACCEPTABLE,
            "Not acceptable",
            "The requested media type is not acceptable.",
            "api/v1/claims",
            "corr-406",
            "NOT_ACCEPTABLE",
            false);
  }

  @Test
  void handleHttpMediaTypeNotSupported_shouldReturn415() {
    MockHttpServletRequest request = request("POST", "api/v1/claims", "corr-415");
    HttpMediaTypeNotSupportedException ex =
            mock(HttpMediaTypeNotSupportedException.class);
    ResponseEntity<ProblemDetail> response = handler.handleHttpMediaTypeNotSupported(ex, request);
    assertProblem(
            response,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "Unsupported media type",
            "The media type of the request is not supported.",
            "api/v1/claims",
            "corr-415",
            "UNSUPPORTED_MEDIA_TYPE",
            false);
  }

  // ---------- helpers ----------

  private static MockHttpServletRequest request(String method, String uri, String correlationId) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(method);
    request.setRequestURI(uri);
    if (correlationId != null) {
      request.addHeader(CORRELATION_HEADER, correlationId);
    }
    return request;
  }

  private static void assertProblem(
      ResponseEntity<ProblemDetail> response,
      HttpStatus expectedStatus,
      String expectedTitle,
      String expectedDetail,
      String expectedInstance,
      String expectedCorrelationId,
      String expectedErrorCode,
      boolean expectNonBlankDetail) {

    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    assertThat(response.getHeaders().getContentType()).isEqualTo(PROBLEM_JSON);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getStatus()).isEqualTo(expectedStatus.value());
    assertThat(body.getTitle()).isEqualTo(expectedTitle);

    if (expectedDetail != null) {
      assertThat(body.getDetail()).isEqualTo(expectedDetail);
    } else if (expectNonBlankDetail) {
      assertThat(body.getDetail()).isNotBlank();
    }

    assertThat(body.getInstance()).isEqualTo(URI.create(expectedInstance));
    assertThat(body.getProperties().get("correlationId")).isEqualTo(expectedCorrelationId);
    assertThat(body.getProperties().get("errorCode")).isEqualTo(expectedErrorCode);

    assertThat(MDC.get(MDC_CORRELATION_ID)).isEqualTo(expectedCorrelationId);
  }
}
