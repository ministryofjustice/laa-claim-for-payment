package uk.gov.justice.laa.claimforpayment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 */
@Configuration
public class SecurityConfig {

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * @param http the HttpSecurity to modify
   * @return the configured SecurityFilterChain
   * @throws Exception in case of configuration errors
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // no auth enforcement yet
    return http.build();
  }
}
