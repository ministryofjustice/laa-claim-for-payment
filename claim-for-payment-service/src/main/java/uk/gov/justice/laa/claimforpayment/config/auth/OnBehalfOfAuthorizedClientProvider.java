package uk.gov.justice.laa.claimforpayment.config.auth;

import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** A custom OAuth2AuthorizedClientProvider for handling on-behalf-of token exchanges. */
public class OnBehalfOfAuthorizedClientProvider implements OAuth2AuthorizedClientProvider {

  public static final String INCOMING_ACCESS_TOKEN = "INCOMING_ACCESS_TOKEN";

  private static final Logger log =
      LoggerFactory.getLogger(OnBehalfOfAuthorizedClientProvider.class);

  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {

    String incomingToken = context.getAttribute(INCOMING_ACCESS_TOKEN);

    if (incomingToken == null) {
      log.debug("###No incoming token found for OBO exchange");
      return null;
    }

    ClientRegistration clientRegistration = context.getClientRegistration();

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

    form.add("client_id", clientRegistration.getClientId());
    form.add("client_secret", clientRegistration.getClientSecret());
    form.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
    form.add("requested_token_use", "on_behalf_of");
    form.add("assertion", incomingToken);
    form.add("scope", String.join(" ", clientRegistration.getScopes()));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(
            clientRegistration.getProviderDetails().getTokenUri(),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {});

    Map<String, Object> body = response.getBody();

    if (body == null || !body.containsKey("access_token")) {
      throw new OAuth2AuthorizationException(
          new OAuth2Error("invalid_token_response", "Missing access_token in OBO response", null));
    }

    String accessTokenValue = body.get("access_token").toString();

    Instant issuedAt = Instant.now();

    Instant expiresAt = issuedAt.plusSeconds(Long.parseLong(body.get("expires_in").toString()));

    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER, accessTokenValue, issuedAt, expiresAt);

    return new OAuth2AuthorizedClient(
        clientRegistration, context.getPrincipal().getName(), accessToken);
  }
}
