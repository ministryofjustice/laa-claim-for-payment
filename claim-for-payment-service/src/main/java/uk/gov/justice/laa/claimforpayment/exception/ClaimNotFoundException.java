package uk.gov.justice.laa.claimforpayment.exception;

/**
 * The exception thrown when item not found.
 */
public class ClaimNotFoundException extends RuntimeException {

  /**
   * Constructor for ItemNotFoundException.
   *
   * @param message the error message
   */
  public ClaimNotFoundException(String message) {
    super(message);
  }
}
