package uk.gov.justice.laa.claimforpayment.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Claim for Payment service. Configures HTTP security, OAuth2 login,
 * and resource server JWT support.
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@ConditionalOnProperty(name = "security.enabled", havingValue = "true")
public class SecurityConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  @Order(1)
  SecurityFilterChain http(
      HttpSecurity http, @org.springframework.lang.Nullable JwtDecoder jwtDecoder)
      throws Exception {

    log.info("USING REAL SECURITY CONFIG");

    http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/", "/assets/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    // If a decoder exists (Local/Prod), enable JWT.
    // If not (Test), this part is skipped safely.
    if (jwtDecoder != null) {
      http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
    }

    return http.build();
  }
}
