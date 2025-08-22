package uk.gov.justice.laa.mockoidc;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/** Configuration class for the mock OIDC server. */
@Configuration
public class OidcServerConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(OidcServerConfig.class);

  @Value("${auth.mock.issuer:http://localhost:8081/mock-issuer}")
  private String issuer;

  // ========= Security chains =========

  /** Authorization Server endpoints (discovery, authorize, token, jwks, userinfo). */
  @Bean
  @Order(1)
  SecurityFilterChain authorizationServer(HttpSecurity http, Map<String, TestUser> profiles)
      throws Exception {
    var as = OAuth2AuthorizationServerConfigurer.authorizationServer();

    http.securityMatcher(as.getEndpointsMatcher())
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    new org.springframework.security.web.authentication
                        .LoginUrlAuthenticationEntryPoint("/login")))
        .with(
            as,
            cfg ->
                cfg.oidc(
                    oidc ->
                        oidc.userInfoEndpoint(
                            ui ->
                                ui.userInfoMapper(
                                    ctx -> {
                                      String sub = ctx.getAuthorization().getPrincipalName();
                                      TestUser u = profiles.get(sub);

                                      Map<String, Object> claims = new HashMap<>();
                                      claims.put("sub", sub);
                                      if (u != null) {
                                        claims.put("name", u.displayName());
                                        claims.put("preferred_username", u.username());
                                        claims.put("email", u.email());
                                        claims.put("providerId", u.providerId());
                                      }
                                      return new OidcUserInfo(claims);
                                    }))))
        .authorizeHttpRequests(a -> a.anyRequest().authenticated())
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

    return http.build();
  }

  /** App web security (serves login page, etc.). */
  @Bean
  @Order(2)
  SecurityFilterChain application(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/login", "/css/**", "/js/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }

  // ========= Clients, issuer, keys =========

  @Bean
  RegisteredClientRepository registeredClientRepository() {
    // SSR client for Node/Express frontend
    RegisteredClient ssr =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("SSR")
            .clientSecret("{noop}super-secret-value") // plain secret for dev
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:3000/callback")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope(OidcScopes.EMAIL)
            .scope("api.read")
            .build();

    // Machine client for service-to-service
    RegisteredClient machine =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("machine")
            .clientSecret("{noop}another-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("api.read")
            .build();

    return new InMemoryRegisteredClientRepository(ssr, machine);
  }

  @Bean
  AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer(issuer).build();
  }

  /** JWK for signing tokens; JWKS exposed at /oauth2/jwks. */
  @Bean
  JWKSource<SecurityContext> jwkSource() {
    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(2048);
      KeyPair kp = kpg.generateKeyPair();
      RSAPublicKey pub = (RSAPublicKey) kp.getPublic();
      RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();

      RSAKey jwk = new RSAKey.Builder(pub).privateKey(priv).keyID("mock-rsa").build();
      JWKSet jwkSet = new JWKSet(jwk);
      return (selector, ctx) -> selector.select(jwkSet);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create RSA JWK", e);
    }
  }

  // ========= Users & claims =========

  @Bean
  UserDetailsService users(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
        User.withUsername("alice").password(encoder.encode("password")).roles("caseworker").build(),
        User.withUsername("bob").password(encoder.encode("password")).roles("admin").build());
  }

  public record TestUser(
      String username, String displayName, String email, String providerId, UUID providerUserId) {}

  @Bean
  Map<String, TestUser> testProfiles() {
    return Map.of(
        "alice",
            new TestUser(
                "alice", "Alice Smith", "alice.smith@example.test", "prov-123", UUID.randomUUID()),
        "bob",
            new TestUser(
                "bob",
                "Bob Jones",
                "bob.jones@example.test",
                "prov-456",
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
  }

  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(Map<String, TestUser> profiles) {
    return ctx -> {
      TestUser u = profiles.get(ctx.getPrincipal().getName());
      if (u == null) return;

      if (OidcParameterNames.ID_TOKEN.equals(ctx.getTokenType().getValue())) {
        ctx.getClaims()
            .claim("name", u.displayName())
            .claim("preferred_username", u.username())
            .claim("email", u.email())
            .claim("providerId", u.providerId())
            .claim("providerUserId", u.providerUserId());
      }

      if (OAuth2TokenType.ACCESS_TOKEN.equals(ctx.getTokenType())) {
        ctx.getClaims()
            .claim("providerId", u.providerId())
            .claim("providerUserId", u.providerUserId());
      }
    };
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
