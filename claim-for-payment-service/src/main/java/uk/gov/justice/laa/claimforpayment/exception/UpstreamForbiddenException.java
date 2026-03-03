package uk.gov.justice.laa.claimforpayment.exception;

/** upstream forbidden exception. */
public class UpstreamForbiddenException extends ServiceException {
  public UpstreamForbiddenException(String service, String operation, Throwable cause) {
    super("Forbidden upstream call", service, operation, cause);
  }
}
