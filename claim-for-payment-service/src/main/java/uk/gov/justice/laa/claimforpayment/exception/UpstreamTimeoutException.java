package uk.gov.justice.laa.claimforpayment.exception;

/** upstream timeout exception. */
public class UpstreamTimeoutException extends ServiceException {
  public UpstreamTimeoutException(String service, String operation, Throwable cause) {
    super("Upstream timeout", service, operation, cause);
  }
}
