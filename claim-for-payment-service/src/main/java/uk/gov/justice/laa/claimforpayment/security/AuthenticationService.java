package uk.gov.justice.laa.claimforpayment.security;

import java.util.UUID;

public interface AuthenticationService {
    UUID getCurrentProviderUserId();
}

