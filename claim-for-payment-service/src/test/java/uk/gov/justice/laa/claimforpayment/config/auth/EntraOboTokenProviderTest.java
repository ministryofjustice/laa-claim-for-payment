package uk.gov.justice.laa.claimforpayment.config.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class EntraOboTokenProviderTest {

  @Mock private OAuth2AuthorizedClientManager manager;

  @Mock private TokenCache tokenCache;

  private EntraOboTokenProvider tokenProvider;

  @BeforeEach
  void setUp() {
    tokenProvider = new EntraOboTokenProvider(manager, tokenCache);
  }

  @Test
  void getToken_shouldReturnTokenFromCache() {
    // 1. Arrange the inbound Authentication (the user "Alice")
    String inboundTokenValue = "original-user-token";
    String subject = "alice-123";

    Jwt jwt = Jwt.withTokenValue(inboundTokenValue).header("alg", "none").subject(subject).build();
    JwtAuthenticationToken authentication =
        new JwtAuthenticationToken(jwt, Collections.emptyList());

    OAuth2AccessToken mockExchangedToken = mock(OAuth2AccessToken.class);
    when(mockExchangedToken.getTokenValue()).thenReturn("exchanged-obo-token");

    OAuth2AuthorizedClient mockClient =
        new OAuth2AuthorizedClient(mock(ClientRegistration.class), subject, mockExchangedToken);

    when(tokenCache.get(eq(subject + "::civil-claims"), any()))
        .thenAnswer(
            invocation -> {
              Supplier<OAuth2AuthorizedClient> supplier = invocation.getArgument(1);
              return supplier.get(); // This triggers the private authorize() method
            });

    when(manager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(mockClient);

    String result = tokenProvider.getToken(authentication);

    assertEquals("exchanged-obo-token", result);

    ArgumentCaptor<OAuth2AuthorizeRequest> requestCaptor =
        ArgumentCaptor.forClass(OAuth2AuthorizeRequest.class);

    verify(manager).authorize(requestCaptor.capture());
    OAuth2AuthorizeRequest capturedRequest = requestCaptor.getValue();

    assertEquals("downstream-api-obo", capturedRequest.getClientRegistrationId());
    assertEquals(authentication, capturedRequest.getPrincipal());

    assertEquals(
        inboundTokenValue,
        capturedRequest.getAttribute(OnBehalfOfAuthorizedClientProvider.INCOMING_ACCESS_TOKEN));
  }

  @Test
  void getToken_shouldThrowException_whenAuthenticationIsNull() {
    assertThrows(IllegalStateException.class, () -> tokenProvider.getToken(null));
  }

  @Test
  void getToken_shouldThrowException_whenAuthorizationFails() {
    Jwt jwt = Jwt.withTokenValue("t").header("a", "n").subject("s").build();
    JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

    // Mock cache to return null (simulating exchange failure)
    when(tokenCache.get(anyString(), any())).thenReturn(null);

    assertThrows(IllegalStateException.class, () -> tokenProvider.getToken(auth));
  }
}
