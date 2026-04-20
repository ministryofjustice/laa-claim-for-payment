package uk.gov.justice.laa.claimforpayment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.laa.claimforpayment.ClaimForPaymentApplication;

@SpringBootTest(classes = ClaimForPaymentApplication.class)
@ActiveProfiles("test")
class CivilClaimsOboTest {

  @Autowired private ClaimService claimService; // The service that calls CivilClaimsApi

  @Autowired private RestTemplate civilClaimsOboRestTemplate; // The template from your config

  @MockitoBean private OAuth2AuthorizedClientManager authorizedClientManager;

  private MockRestServiceServer mockServer;

  @BeforeEach
  void setUp() {
    mockServer = MockRestServiceServer.createServer(civilClaimsOboRestTemplate);
  }

  @Test
  void callingServiceProducesRequestWithExchangedToken() {

    Jwt jwt =
        Jwt.withTokenValue("mock-inbound-token")
            .header("alg", "none")
            .subject("alice")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    // 1. Mock the Token Manager to return a "fake" exchanged token
    OAuth2AccessToken fakeExchangedToken =
        new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "new-obo-token-123", null, null);

    OAuth2AuthorizedClient fakeClient =
        new OAuth2AuthorizedClient(mock(ClientRegistration.class), "alice", fakeExchangedToken);

    when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
        .thenReturn(fakeClient);

    try {
      mockServer
          .expect(requestTo("http://localhost:8090/api/v1/claims/1"))
          .andExpect(method(HttpMethod.DELETE))
          .andExpect(header("Authorization", "Bearer new-obo-token-123")) 
          .andRespond(withSuccess("{\"id\":\"789\"}", MediaType.APPLICATION_JSON));

      // 3. Trigger the service
      claimService.deleteClaim(1L);

      // 4. Verify all expectations were met
      mockServer.verify();
    } finally {
      SecurityContextHolder.clearContext();
    }
  }
}
