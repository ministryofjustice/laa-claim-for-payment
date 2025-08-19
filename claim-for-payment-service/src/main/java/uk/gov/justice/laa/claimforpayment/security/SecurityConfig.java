package uk.gov.justice.laa.claimforpayment.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Claim for Payment service.
 * Configures HTTP security, OAuth2 login, and resource server JWT support.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain http(
      HttpSecurity http,
      ObjectProvider<JwtDecoder> jwtDecoderProvider)
      throws Exception {

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
        .csrf(csrf -> csrf.disable());


    // Only enable resource-server JWT if a JwtDecoder exists
    if (jwtDecoderProvider.getIfAvailable() != null) {
      http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
    }

    return http.build();
  }
}
