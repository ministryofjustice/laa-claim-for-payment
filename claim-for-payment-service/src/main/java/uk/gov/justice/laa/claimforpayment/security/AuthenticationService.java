package uk.gov.justice.laa.claimforpayment.security;

import java.util.UUID;

/**
 * Service interface for authentication-related operations.
 */
public interface AuthenticationService {
  UUID getCurrentProviderUserId();
}
