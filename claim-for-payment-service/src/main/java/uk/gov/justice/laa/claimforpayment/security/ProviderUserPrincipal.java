package uk.gov.justice.laa.claimforpayment.security;

import java.util.UUID;

public record ProviderUserPrincipal(UUID providerUserId, String username) {}
