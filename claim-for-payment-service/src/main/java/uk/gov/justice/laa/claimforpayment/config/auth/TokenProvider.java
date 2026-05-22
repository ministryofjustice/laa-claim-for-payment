package uk.gov.justice.laa.claimforpayment.config.auth;

import org.springframework.security.core.Authentication;

/** A provider for generating tokens for authentication purposes. */
public interface TokenProvider {
  String getToken(Authentication authentication);
}
