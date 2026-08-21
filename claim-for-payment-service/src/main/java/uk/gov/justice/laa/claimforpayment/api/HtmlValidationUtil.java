package uk.gov.justice.laa.claimforpayment.api;

import org.apache.tomcat.util.http.InvalidParameterException;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Utility class for validating HTML content in strings.
 **/
public final class HtmlValidationUtil {

  private static final PolicyFactory NO_HTML =
      new HtmlPolicyBuilder().toFactory();

  private HtmlValidationUtil() {
  }

  /**
   * Validates that the provided string does not contain any HTML content.
   */
  public static void validateNoHtml(String value) {
    if (value == null) {
      return;
    }

    String sanitised = NO_HTML.sanitize(value);

    if (!value.equals(sanitised)) {
      throw new InvalidParameterException(
          "HTML content is not permitted.");
    }
  }
}