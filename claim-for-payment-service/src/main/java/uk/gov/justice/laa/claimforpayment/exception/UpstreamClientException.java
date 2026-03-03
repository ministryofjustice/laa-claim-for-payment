package uk.gov.justice.laa.claimforpayment.exception;

/** upstream client error exception. */
public class UpstreamClientException extends ServiceException {
  public UpstreamClientException(String service, String operation, Throwable cause) {
    super("Upstream unknown error", service, operation, cause);
  }
}
