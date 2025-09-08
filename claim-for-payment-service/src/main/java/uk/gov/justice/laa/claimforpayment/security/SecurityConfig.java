package uk.gov.justice.laa.claimforpayment.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Claim for Payment service. Configures HTTP security, OAuth2 login,
 * and resource server JWT support.
 */
@Configuration
@EnableMethodSecurity
@ConditionalOnProperty(name = "security.enabled", havingValue = "true")
@Profile("!test")
public class SecurityConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  SecurityFilterChain http(HttpSecurity http, ObjectProvider<JwtDecoder> jwtDecoderProvider)
      throws Exception {
    log.info("USING REAL SECURITY CONFIG");
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/",
                        "/assets/**",
                        "/actuator/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

    // Only enable resource-server JWT if a JwtDecoder exists
    if (jwtDecoderProvider.getIfAvailable() != null) {
      http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
    }

    return http.build();
  }
}
