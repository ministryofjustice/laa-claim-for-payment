package uk.gov.justice.laa.claimforpayment.exception;

/** upstream service exception. */
public class UpstreamServiceException extends ServiceException {
  public UpstreamServiceException(String service, String operation, Throwable cause) {
    super("Upstream service failure", service, operation, cause);
  }
}
