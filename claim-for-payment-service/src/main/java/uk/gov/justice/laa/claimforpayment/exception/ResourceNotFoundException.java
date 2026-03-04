package uk.gov.justice.laa.claimforpayment.exception;

/** upstream resource not found exception. */
public class ResourceNotFoundException extends ServiceException {
  public ResourceNotFoundException(String service, String operation, Throwable cause) {
    super("Resource not found", service, operation, cause);
  }
}
