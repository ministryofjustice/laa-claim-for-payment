package uk.gov.justice.laa.claimforpayment.exception;

/**
 * The exception thrown when claim not found.
 */
public class ClaimNotFoundException extends RuntimeException {

  /**
   * Constructor for ClaimNotFoundException.
   *
   * @param message the error message
   */
  public ClaimNotFoundException(String message) {
    super(message);
  }
}
