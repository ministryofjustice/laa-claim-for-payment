package uk.gov.justice.laa.claimforpayment.config;

import java.time.Instant;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
// import org.springframework.security.oauth2.client.*;
// import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

//@TestConfiguration
public class TestOauthConfig {

  // @Bean
  // ClientRegistrationRepository clientRegistrationRepository() {
  //   ClientRegistration ssr =
  //       ClientRegistration.withRegistrationId("ssr")
  //           .clientId("test-client")
  //           .clientSecret("secret")
  //           .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
  //           .redirectUri("{baseUrl}/login/oauth2/code/ssr")
  //           .scope("openid", "profile", "email", "api.read")
  //           .authorizationUri("http://localhost/authorize")
  //           .tokenUri("http://localhost/token")
  //           .userInfoUri("http://localhost/userinfo")
  //           .userNameAttributeName(IdTokenClaimNames.SUB)
  //           .issuerUri("http://localhost")
  //           .build();
  //   return new InMemoryClientRegistrationRepository(ssr);
  // }

  // @Bean
  // OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository repo) {
  //   return new InMemoryOAuth2AuthorizedClientService(repo);
  // }

  // Satisfy oauth2ResourceServer().jwt() if your SecurityConfig enables it
  @Bean
  JwtDecoder jwtDecoder() {
    return token -> Jwt.withTokenValue(token)
      .header("alg", "none")
      .claim("sub", "test-user")
      .claim("scope", "api.read")
      .issuedAt(Instant.now())
      .expiresAt(Instant.now().plusSeconds(3600))
      .build();
  }
}
