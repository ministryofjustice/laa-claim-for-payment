package uk.gov.justice.laa.claimforpayment.config.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

/** Caffeine token cache implementation. */
@Component
public class CaffeineTokenCache implements TokenCache {

  private final Cache<String, OAuth2AuthorizedClient> cache =
      Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(5, TimeUnit.MINUTES).build();

  @Override
  public OAuth2AuthorizedClient get(String key, Supplier<OAuth2AuthorizedClient> loader) {

    return cache.get(key, k -> loader.get());
  }
}
