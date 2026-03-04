package uk.gov.justice.laa.claimforpayment.exception;

/** upstream validation exception. */
public class UpstreamValidationException extends ServiceException {
  public UpstreamValidationException(String service, String operation, Throwable cause) {
    super("Upstream rejected request", service, operation, cause);
  }
}
