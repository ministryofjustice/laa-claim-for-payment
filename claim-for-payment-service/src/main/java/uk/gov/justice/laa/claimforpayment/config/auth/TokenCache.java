package uk.gov.justice.laa.claimforpayment.config.auth;

import java.util.function.Supplier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

/** Token cache interface. */
public interface TokenCache {
  OAuth2AuthorizedClient get(String key, Supplier<OAuth2AuthorizedClient> loader);
}
