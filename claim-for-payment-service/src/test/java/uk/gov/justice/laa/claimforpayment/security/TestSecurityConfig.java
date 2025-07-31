package uk.gov.justice.laa.claimforpayment.security;

import java.util.UUID;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@TestConfiguration
public class TestSecurityConfig {

  @Bean
  public RequestPostProcessor mockPrincipal() {
    UUID fixedUserId = UUID.fromString("999e4567-e89b-12d3-a456-426614174999");
    var principal = new ProviderUserPrincipal(fixedUserId, "Firstname Lastname");
    var auth = new TestingAuthenticationToken(principal, null);
    return SecurityMockMvcRequestPostProcessors.authentication(auth);
  }
}
