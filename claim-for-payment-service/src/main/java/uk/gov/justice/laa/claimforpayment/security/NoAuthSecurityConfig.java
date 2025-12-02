package uk.gov.justice.laa.claimforpayment.security;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the Claim for Payment service. Configures HTTP security, OAuth2 login,
 * and resource server JWT support.
 */
@Configuration
@EnableMethodSecurity
@ConditionalOnProperty(name = "security.enabled", havingValue = "false")
public class NoAuthSecurityConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(NoAuthSecurityConfig.class);

  private static final UUID DEFAULT_PROVIDER_ID =
      UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @Bean
  SecurityFilterChain openAll(HttpSecurity http) throws Exception {
    log.info("USING NO AUTH SECURITY CONFIG");
    return http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .authorizeHttpRequests(
            auth -> auth.anyRequest().permitAll()) // no auth while client catches up
        .addFilterBefore(
            (request, response, chain) -> {
              // build a fake Jwt with default providerUserId
              Map<String, Object> claims =
                  Map.of(
                      "USER_NAME",
                      DEFAULT_PROVIDER_ID.toString(),
                      "scope",
                      "Claims.Write" // to satisfy
                      // @PreAuthorize("hasAuthority('SCOPE_Claims.Write')")
                      );

              Jwt jwt =
                  new Jwt(
                      "fake-token",
                      Instant.now(),
                      Instant.now().plusSeconds(3600),
                      Map.of("alg", "none"),
                      claims);

              AbstractAuthenticationToken authToken =
                  new JwtAuthenticationToken(
                      jwt, List.of(new SimpleGrantedAuthority("SCOPE_Claims.Write")));

              SecurityContextHolder.getContext().setAuthentication(authToken);
              chain.doFilter(request, response);
            },
            UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
