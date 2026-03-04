package uk.gov.justice.laa.claimforpayment.exception;

/**
 * Abstract base class for service-related exceptions.
 */
public abstract class ServiceException extends RuntimeException {

  private final String service;
  private final String operation;

  protected ServiceException(
      String message,
      String service,
      String operation,
      Throwable cause
  ) {
    super(message, cause);
    this.service = service;
    this.operation = operation;
  }

  public String getService() {
    return service;
  }

  public String getOperation() {
    return operation;
  }
}
