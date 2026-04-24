package uk.gov.justice.laa.claimforpayment.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Central exception -> HTTP mapping for the API.
 *
 * <p>service exceptions bubble to this handler.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String CORRELATION_HEADER = "X-Correlation-Id";
  private static final String MDC_CORRELATION_ID = "correlationId";

  private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

  /**
   * Handle resource not found.
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(
          ResourceNotFoundException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Resource not found. method={} path={} correlationId={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId);

    ProblemDetail body =
            problem(
                    HttpStatus.NOT_FOUND,
                    "Not found",
                    safeMessage(ex),
                    request,
                    correlationId,
                    "NOT_FOUND");

    return respond(HttpStatus.NOT_FOUND, body);
  }

  /**
   * Handle validation failure by API or referred by upstream API.
   */
  @ExceptionHandler(UpstreamValidationException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamValidation(
          UpstreamValidationException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.warn(
            "Upstream validation failure. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex),
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request",
                    "The request could not be processed.",
                    request,
                    correlationId,
                    "VALIDATION_FAILED");

    return respond(HttpStatus.BAD_REQUEST, body);
  }

  /**
   * Handle conflicting state upstream.
   */
  @ExceptionHandler(UpstreamConflictException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamConflict(
          UpstreamConflictException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Upstream conflict. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));

    ProblemDetail body =
            problem(
                    HttpStatus.CONFLICT,
                    "Conflict",
                    "The request could not be completed due to a conflict.",
                    request,
                    correlationId,
                    "CONFLICT");

    return respond(HttpStatus.CONFLICT, body);
  }

  /**
   * Handle upstream authz failure.
   */
  @ExceptionHandler(UpstreamUnauthorisedException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamUnauthorised(
          UpstreamUnauthorisedException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.warn(
            "Upstream unauthorised. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex),
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthenticated",
                    "Authentication is required.",
                    request,
                    correlationId,
                    "UNAUTHENTICATED");

    return respond(HttpStatus.UNAUTHORIZED, body);
  }

  /**
   * Handle upstream authn failure.
   */
  @ExceptionHandler(UpstreamForbiddenException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamForbidden(
          UpstreamForbiddenException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.warn(
            "Upstream forbidden. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex),
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    "You do not have permission to perform this action.",
                    request,
                    correlationId,
                    "FORBIDDEN");

    return respond(HttpStatus.FORBIDDEN, body);
  }

  /**
   * Handle upstream rate limited exception.
   */
  @ExceptionHandler(UpstreamRateLimitedException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamRateLimited(
          UpstreamRateLimitedException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.warn(
            "Upstream rate limited. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));

    ProblemDetail body =
            problem(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Temporarily unavailable",
                    "A dependent service is rate limiting requests. Please try again.",
                    request,
                    correlationId,
                    "TEMPORARILY_UNAVAILABLE");

    return respond(HttpStatus.SERVICE_UNAVAILABLE, body);
  }

  /**
   * Handle upstream timeout exception.
   */
  @ExceptionHandler(UpstreamTimeoutException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamTimeout(
          UpstreamTimeoutException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.warn(
            "Upstream timeout. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex),
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "Upstream timeout",
                    "A dependent service did not respond in time.",
                    request,
                    correlationId,
                    "UPSTREAM_TIMEOUT");

    return respond(HttpStatus.GATEWAY_TIMEOUT, body);
  }

  /**
   * Handle generic upstream failure.
   */
  @ExceptionHandler({UpstreamServiceException.class, UpstreamClientException.class})
  public ResponseEntity<ProblemDetail> handleUpstreamFailure(
          RuntimeException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.warn(
            "Upstream failure. method={} path={} correlationId={} exception={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            ex.getClass().getSimpleName(),
            safeMessage(ex),
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.BAD_GATEWAY,
                    "Upstream service error",
                    "A dependent service returned an error.",
                    request,
                    correlationId,
                    "UPSTREAM_ERROR");

    return respond(HttpStatus.BAD_GATEWAY, body);
  }

  /**
   * Handle generic service exception.
   */
  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<ProblemDetail> handleServiceException(
          ServiceException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    // Known failure type, but not mapped above. WARN by default.
    log.warn(
            "Service exception. method={} path={} correlationId={} exception={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            ex.getClass().getSimpleName(),
            safeMessage(ex),
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error",
                    "The request could not be completed.",
                    request,
                    correlationId,
                    "SERVICE_ERROR");

    return respond(HttpStatus.INTERNAL_SERVER_ERROR, body);
  }

  /**
   * Handle unexpected exceptions. *
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.error(
            "Unhandled exception. method={} path={} correlationId={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            ex);

    ProblemDetail body =
            problem(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error",
                    "An unexpected error occurred.",
                    request,
                    correlationId,
                    "INTERNAL_ERROR");

    return respond(HttpStatus.INTERNAL_SERVER_ERROR, body);
  }

  /**
   * Handle method argument validation errors.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Validation failed. method={} path={} correlationId={} errors={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            ex.getBindingResult().getErrorCount());

    ProblemDetail body =
            problem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request",
                    "Request validation failed.",
                    request,
                    correlationId,
                    "VALIDATION_FAILED");

    // Optional: expose field errors for the UI (safe, stable structure)
    body.setProperty(
            "fieldErrors",
            ex.getBindingResult().getFieldErrors().stream()
                    .map(err -> new FieldErrorView(err.getField(), err.getDefaultMessage()))
                    .toList());

    return respond(HttpStatus.BAD_REQUEST, body);
  }

  /**
   * Handle exceptions thrown by this service.
   */
  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ProblemDetail> handleErrorResponseException(
          ErrorResponseException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);
    HttpStatusCode statusCode = ex.getStatusCode();

    // Spring may already have a ProblemDetail; preserve it if present, else create one.
    ProblemDetail body = ex.getBody();

    // Ensure we still add your standard properties
    body.setInstance(URI.create(request.getRequestURI()));
    body.setProperty("correlationId", correlationId);
    body.setProperty("errorCode", errorCodeForStatus(statusCode.value()));

    logAtLevelForStatus(statusCode.value(), request, correlationId, ex);

    return respond(HttpStatus.valueOf(statusCode.value()), body);
  }

  /**
   * Handle Spring Security authentication failures (Missing/Invalid Token).
   */
  @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthenticationException(
          org.springframework.security.core.AuthenticationException ex,
          HttpServletRequest request) {

    String correlationId = correlationId(request);
    log.info(
            "Authentication failed. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            ex.getMessage());

    ProblemDetail body =
            problem(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthenticated",
                    "Authentication is required.",
                    request,
                    correlationId,
                    "UNAUTHENTICATED");

    return respond(HttpStatus.UNAUTHORIZED, body);
  }

  /**
   * Handle Spring Security authorization failures (Wrong Scopes).
   */
  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDeniedException(
          org.springframework.security.access.AccessDeniedException ex,
          HttpServletRequest request) {

    String correlationId = correlationId(request);
    log.info(
            "Access denied. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            ex.getMessage());

    ProblemDetail body =
            problem(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    "You do not have the required permissions.",
                    request,
                    correlationId,
                    "FORBIDDEN");

    return respond(HttpStatus.FORBIDDEN, body);
  }

  /**
   * Handle constraint violation errors.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolation(
          ConstraintViolationException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Validation failed. method={} path={} correlationId={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId);

    ProblemDetail body =
            problem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request",
                    "Request validation failed.",
                    request,
                    correlationId,
                    "VALIDATION_FAILED");

    body.setProperty(
            "fieldErrors",
            ex.getConstraintViolations().stream()
                    .map(
                            err -> new FieldErrorView(
                                    err.getPropertyPath().toString(), err.getMessage()))
                    .toList());

    return respond(HttpStatus.BAD_REQUEST, body);
  }

  /**
   * Handle method argument type mismatch errors.
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
          MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Validation failed. method={} path={} correlationId={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId);

    ProblemDetail body =
            problem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request",
                    "Request validation failed.",
                    request,
                    correlationId,
                    "VALIDATION_FAILED");

    return respond(HttpStatus.BAD_REQUEST, body);
  }

  /**
   * Handle resource not found exception.
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> handleNoResourceFound(
          NoResourceFoundException ex, HttpServletRequest request) {
    String correlationId = correlationId(request);

    log.info(
            "Resource not found. method={} path={} correlationId={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId);

    ProblemDetail body =
            problem(
                    HttpStatus.NOT_FOUND,
                    "Not found",
                    safeMessage(ex),
                    request,
                    correlationId,
                    "NOT_FOUND");

    return respond(HttpStatus.NOT_FOUND, body);
  }

  /**
   * Handle Http message not readable exception.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(
          HttpMessageNotReadableException ex, HttpServletRequest request) {
    String correlationId = correlationId(request);

    log.info(
            "Validation failed. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));
    ProblemDetail body =
            problem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request",
                    "Request validation failed.",
                    request,
                    correlationId,
                    "VALIDATION_FAILED");

    return respond(HttpStatus.BAD_REQUEST, body);
  }

  /**
   * Handle invalid parameter exception.
   */
  @ExceptionHandler(InvalidParameterException.class)
  public ResponseEntity<ProblemDetail> handleInvalidParameter(InvalidParameterException ex,
                                                              HttpServletRequest request) {
    String correlationId = correlationId(request);

    log.info(
            "Validation failed. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));
    ProblemDetail body =
            problem(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request",
                    "Request validation failed.",
                    request,
                    correlationId,
                    "VALIDATION_FAILED");

    return respond(HttpStatus.BAD_REQUEST, body);
  }

  /**
   * Handle HTTP request method not supported.
   */

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleHttpMethodNotSupported(
          HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Method not allowed. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));

    ProblemDetail body =
            problem(
                    HttpStatus.METHOD_NOT_ALLOWED,
                    "Method not supported",
                    "The HTTP method is not supported for this endpoint.",
                    request,
                    correlationId,
                    "METHOD_NOT_ALLOWED");

    return respond(HttpStatus.METHOD_NOT_ALLOWED, body);
  }

  /**
   * Handle HTTP media type not acceptable.
   */

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMediaTypeNotAcceptable(
          HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Media type not acceptable. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));

    ProblemDetail body =
            problem(
                    HttpStatus.NOT_ACCEPTABLE,
                    "Not acceptable",
                    "The requested media type is not acceptable.",
                    request,
                    correlationId,
                    "NOT_ACCEPTABLE");

    return respond(HttpStatus.NOT_ACCEPTABLE, body);
  }

  /**
   * Handle HTTP media type not supported.
   * */

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleHttpMediaTypeNotSupported(
          HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

    String correlationId = correlationId(request);

    log.info(
            "Media type not supported. method={} path={} correlationId={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            correlationId,
            safeMessage(ex));

    ProblemDetail body =
            problem(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported media type",
                    "The media type of the request is not supported.",
                    request,
                    correlationId,
                    "UNSUPPORTED_MEDIA_TYPE");

    return respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE, body);
  }

  private static String errorCodeForStatus(int status) {
    if (status == 400) {
      return "VALIDATION_FAILED";
    }
    if (status == 401) {
      return "UNAUTHENTICATED";
    }
    if (status == 403) {
      return "FORBIDDEN";
    }
    if (status == 404) {
      return "NOT_FOUND";
    }
    if (status >= 400 && status < 500) {
      return "REQUEST_FAILED";
    }
    return "INTERNAL_ERROR";
  }

  private void logAtLevelForStatus(
          int status, HttpServletRequest request, String correlationId, Exception ex) {
    if (status >= 400 && status < 500) {
      log.info(
              "Request failed. method={} path={} status={} correlationId={}",
              request.getMethod(),
              request.getRequestURI(),
              status,
              correlationId);
    } else {
      log.warn(
              "Request failed. method={} path={} status={} correlationId={}",
              request.getMethod(),
              request.getRequestURI(),
              status,
              correlationId,
              ex);
    }
  }

  private record FieldErrorView(String field, String message) {
  }

  private static ResponseEntity<ProblemDetail> respond(HttpStatus status, ProblemDetail body) {
    return ResponseEntity.status(status).contentType(PROBLEM_JSON).body(body);
  }

  private static ProblemDetail problem(
          HttpStatus status,
          String title,
          String detail,
          HttpServletRequest request,
          String correlationId,
          String errorCode) {

    ProblemDetail pd = ProblemDetail.forStatus(status);
    pd.setTitle(title);
    pd.setDetail(detail);
    pd.setInstance(URI.create(request.getRequestURI()));

    // Useful for client support tickets + log correlation
    pd.setProperty("correlationId", correlationId);

    // Stable code the UI can key off later (keep this set limited/stable)
    pd.setProperty("errorCode", errorCode);

    return pd;
  }

  private static String correlationId(HttpServletRequest request) {
    String headerValue = request.getHeader(CORRELATION_HEADER);
    String correlationId =
            Optional.ofNullable(headerValue)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .orElseGet(() -> UUID.randomUUID().toString());

    MDC.put(MDC_CORRELATION_ID, correlationId);

    return correlationId;
  }

  private static String safeMessage(Throwable t) {
    String message = t.getMessage();
    if (message == null || message.isBlank()) {
      return t.getClass().getSimpleName();
    }
    return message;
  }
}
