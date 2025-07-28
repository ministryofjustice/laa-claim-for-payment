package uk.gov.justice.laa.claimforpayment.security;

import java.util.UUID;

/**
 * Represents a principal for a provider user with an ID and username.
 */
public record ProviderUserPrincipal(UUID providerUserId, String username) {}

