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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * Configuration for the mock OIDC server, including security chains, registered clients, JWK
 * source, test users, and token customizer.
 */
@Configuration
public class OidcServerConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(OidcServerConfig.class);

  // ----- configurable bits (override in application.yml if you like) -----
  @Value("${auth.mock.issuer:http://localhost:8081}")
  private String issuer;

  @Value("${auth.mock.redirect-ssr:http://localhost:3000/callback}")
  private String ssrRedirect;

  @Value("${auth.mock.logout-ssr:http://localhost:3000}")
  private String ssrLogout;

  @Bean
  RequestCache requestCache() {
    return new HttpSessionRequestCache();
  }

  /** Authorisation Server endpoints (discovery, authorize, token, jwks, userinfo). */
  @Bean
  @Order(1)
  SecurityFilterChain authorizationServer(HttpSecurity http, Map<String, TestUser> profiles)
      throws Exception {
    var as = OAuth2AuthorizationServerConfigurer.authorizationServer();

    http.securityMatcher(as.getEndpointsMatcher())
        .securityMatcher(as.getEndpointsMatcher())
        // tell SAS to redirect unauthenticated users to /login instead of 401
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                    new org.springframework.security.web.authentication
                        .LoginUrlAuthenticationEntryPoint("/login")))
        .requestCache(c -> c.requestCache(requestCache()))
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
                                        claims.put("FIRM_CODE", u.firmId());
                                      }
                                      return new OidcUserInfo(claims);
                                    }))))
         .authorizeHttpRequests(authorize -> authorize

          .requestMatchers("/.well-known/**").permitAll()
          .requestMatchers("/oauth2/jwks").permitAll()
          // everything else on the auth server endpoints still needs auth
          .anyRequest().authenticated()
      )
        .oauth2ResourceServer(
            oauth -> oauth.jwt(Customizer.withDefaults())); // lets /userinfo accept bearer tokens

    return http.build();
  }

  /** App web security (serves login page, etc.). */
  @Bean
  @Order(2)
  SecurityFilterChain application(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/login",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/actuator/**",
                        "/favicon.ico"
                        )
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .requestCache(c -> c.requestCache(requestCache()))
        .formLogin(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  RegisteredClientRepository registeredClientRepository(PasswordEncoder encoder) {

    RegisteredClient ssr =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("caa-client")
            .clientSecret(encoder.encode("super-secret-value"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri(ssrRedirect)
            .postLogoutRedirectUri(ssrLogout)
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope(OidcScopes.EMAIL)
            .scope("Claims.Write")
            .build();

    // New: machine client for service-to-service tokens
    RegisteredClient machine =
        RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("machine")
            .clientSecret(encoder.encode("secret"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("Claims.Write") // match what your API checks (e.g. SCOPE_Claims.Write')
            .build();

    return new InMemoryRegisteredClientRepository(ssr, machine);
  }

  @Bean
  AuthorizationServerSettings authorizationServerSettings() {
    // here you can set issuer to include /mock-issuer
    return AuthorizationServerSettings.builder().issuer(issuer).build();
  }

  /** JWK for signing tokens; JWKS exposed automatically at /oauth2/jwks. */
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
      // The encoder will propagate the JWK's kid into the JOSE header automatically.
      return (selector, ctx) -> selector.select(jwkSet);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create RSA JWK", e);
    }
  }

  /** In-memory users for login form (alice/bob : password). */
  @Bean
  UserDetailsService users(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
        User.withUsername("alice").password(encoder.encode("password")).roles("caseworker").build(),
        User.withUsername("bob").password(encoder.encode("password")).roles("admin").build());
  }

  /** Represents a test user profile with username, display name, email, and provider ID. */
  public record TestUser(
      String username, String displayName, String email, String firmId, UUID providerUserId) {}

  /** Extra profile data surfaced in tokens and /userinfo. */
  @Bean
  Map<String, TestUser> testProfiles() {
    return Map.of(
        "alice",
            new TestUser(
                "alice",
                "Alice Smith",
                "alice.smith@example.test",
                "prov-123",
                UUID.fromString("d9c4b277-941c-451c-81c4-6b46b7f7ab59")),
        "bob",
            new TestUser(
                "bob",
                "Bob Jones",
                "bob.jones@example.test",
                "prov-456",
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
  }

  /** Add Entra-style + custom claims. */
  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(Map<String, TestUser> profiles) {
    return ctx -> {
      TestUser u = profiles.get(ctx.getPrincipal().getName());
      if (u == null) {
        return;
      }

      var roles =
          ((Authentication) ctx.getPrincipal())
              .getAuthorities().stream()
                  .map(GrantedAuthority::getAuthority) // e.g. "ROLE_admin"
                  .filter(a -> a.startsWith("ROLE_"))
                  .map(a -> a.substring(5)) // -> "admin"
                  .toList();

      // ID token: rich identity claims
      if (OidcParameterNames.ID_TOKEN.equals(ctx.getTokenType().getValue())) {
        ctx.getClaims()
            .claim("name", u.displayName())
            .claim("preferred_username", u.username())
            .claim("email", u.email())
            .claim("FIRM_CODE", u.firmId())
            .claim("USER_NAME", u.providerUserId())
            .claim("roles", roles);
      }

      // Access token: include providerId so API can authorise with it
      if (OAuth2TokenType.ACCESS_TOKEN.equals(ctx.getTokenType())) {
        ctx.getClaims()
            .audience(java.util.List.of("api-audience"))
            .claim("FIRM_CODE", u.firmId())
            .claim("USER_NAME", u.providerUserId())
            .claim("roles", roles);
      }
    };
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
