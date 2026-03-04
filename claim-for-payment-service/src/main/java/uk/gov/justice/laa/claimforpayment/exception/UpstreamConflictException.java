package uk.gov.justice.laa.claimforpayment.exception;

/** upstream conflict exception. */
public class UpstreamConflictException extends ServiceException {
  public UpstreamConflictException(String service, String operation, Throwable cause) {
    super("Upstream state is conflicting", service, operation, cause);
  }
}
