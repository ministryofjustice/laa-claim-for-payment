package uk.gov.justice.laa.claimforpayment.exception;

/**
 * The exception thrown when submission not found.
 */
public class SubmissionNotFoundException extends RuntimeException {

  /**
   * Constructor for SubmissionNotFoundException.
   *
   * @param message the error message
   */
  public SubmissionNotFoundException(String message) {
    super(message);
  }
}
