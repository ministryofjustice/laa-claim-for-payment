package uk.gov.justice.laa.claimforpayment.exception;

/** upstream rate limited exception. */
public class UpstreamRateLimitedException extends ServiceException {
  public UpstreamRateLimitedException(String service, String operation, Throwable cause) {
    super("Upstream received too many requests", service, operation, cause);
  }
}
