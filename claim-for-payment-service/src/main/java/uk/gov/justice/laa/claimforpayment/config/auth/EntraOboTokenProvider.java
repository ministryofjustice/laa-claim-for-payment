package uk.gov.justice.laa.claimforpayment.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Token provider that uses the OBO flow to obtain access tokens for downstream APIs on behalf of
 * the currently authenticated user. Tokens are cached to avoid unnecessary calls to the OIDC server
 * for each request.
 */
@Slf4j
@Component("civilClaimsOboTokenProvider")
public class EntraOboTokenProvider implements TokenProvider {

  private final OAuth2AuthorizedClientManager manager;

  private final TokenCache tokenCache;

  public EntraOboTokenProvider(OAuth2AuthorizedClientManager manager, TokenCache tokenCache) {
    this.manager = manager;
    this.tokenCache = tokenCache;
  }

  @Override
  public String getToken(Authentication authentication) {
    if (authentication == null) {
      throw new IllegalStateException("No authentication in security context");
    }

    // Cache key: per-user + per-client
    String cacheKey = authentication.getName() + "::civil-claims";

    OAuth2AuthorizedClient client = tokenCache.get(cacheKey, () -> authorize(authentication));

    if (client == null || client.getAccessToken() == null) {
      throw new IllegalStateException("Failed to obtain OBO access token");
    }

    return client.getAccessToken().getTokenValue();
  }

  private OAuth2AuthorizedClient authorize(Authentication authentication) {

    OAuth2AuthorizeRequest authorizeRequest =
        OAuth2AuthorizeRequest.withClientRegistrationId("downstream-api-obo")
            .principal(authentication)
            .attribute(
                OnBehalfOfAuthorizedClientProvider.INCOMING_ACCESS_TOKEN,
                ((JwtAuthenticationToken) authentication).getToken().getTokenValue())
            .build();
    OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);

    return client;
  }
}
