package uk.gov.justice.laa.claimforpayment.exception;

/** upstream unauthorised exception. */
public class UpstreamUnauthorisedException extends ServiceException {
  public UpstreamUnauthorisedException(
      String service,
      String operation,
      Throwable cause
  ) {
    super("Unauthorised upstream call", service, operation, cause);
  }
}
