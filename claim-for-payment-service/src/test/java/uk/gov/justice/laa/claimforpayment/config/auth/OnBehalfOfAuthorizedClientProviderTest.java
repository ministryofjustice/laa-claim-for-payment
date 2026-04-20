package uk.gov.justice.laa.claimforpayment.config.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class OnBehalfOfAuthorizedClientProviderTest {

  @Mock private RestTemplate restTemplate;

  @Mock private OAuth2AuthorizationContext context;

  @InjectMocks // This will attempt to inject the 'restTemplate' mock into your provider
  private OnBehalfOfAuthorizedClientProvider provider;

  @Mock private Authentication authentication;

  @BeforeEach
  void setUp() {
    provider = new OnBehalfOfAuthorizedClientProvider();
    ReflectionTestUtils.setField(provider, "restTemplate", restTemplate);
  }

  @Test
  void authorize_shouldReturnNull_whenIncomingTokenIsMissing() {
    when(context.getAttribute(OnBehalfOfAuthorizedClientProvider.INCOMING_ACCESS_TOKEN))
        .thenReturn(null);

    OAuth2AuthorizedClient result = provider.authorize(context);

    assertNull(result);
    verifyNoInteractions(restTemplate);
  }

  @Test
  @SuppressWarnings("unchecked")
  void authorize_shouldPerformExchange_whenTokenIsPresent() {
    // 1. ARRANGE - Mock the context and registration
    String inboundToken = "raw-inbound-jwt-token";
    ClientRegistration registration = createMockRegistration();

    when(context.getAttribute(OnBehalfOfAuthorizedClientProvider.INCOMING_ACCESS_TOKEN))
        .thenReturn(inboundToken);
    when(context.getClientRegistration()).thenReturn(registration);
    when(authentication.getName()).thenReturn("alice-123");
    when(context.getPrincipal()).thenReturn(authentication);

    Map<String, Object> responseBody =
        Map.of(
            "access_token", "new-obo-token",
            "expires_in", "3600",
            "token_type", "Bearer");
    ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);

    when(restTemplate.exchange(
            eq("http://localhost/token"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)))
        .thenReturn(responseEntity);

    OAuth2AuthorizedClient result = provider.authorize(context);

    assertNotNull(result);
    assertEquals("new-obo-token", result.getAccessToken().getTokenValue());
    assertEquals("alice-123", result.getPrincipalName());

    // Verify the Form Data sent to the OIDC server
    ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor =
        ArgumentCaptor.forClass(HttpEntity.class);

    verify(restTemplate)
        .exchange(
            anyString(), any(), requestCaptor.capture(), any(ParameterizedTypeReference.class));

    MultiValueMap<String, String> form = requestCaptor.getValue().getBody();
    assertEquals("urn:ietf:params:oauth:grant-type:jwt-bearer", form.getFirst("grant_type"));
    assertEquals("on_behalf_of", form.getFirst("requested_token_use"));
    assertEquals(inboundToken, form.getFirst("assertion"));
    assertTrue(form.getFirst("scope").contains("openid"));
  }

  @Test
  void authorize_shouldThrowException_whenResponseIsMalformed() {
    when(context.getAttribute(anyString())).thenReturn("valid-token");
    when(context.getClientRegistration()).thenReturn(createMockRegistration());

    ResponseEntity<Map<String, Object>> badResponse =
        ResponseEntity.ok(Map.of("error", "invalid_request"));
    when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .thenReturn(badResponse);

    assertThrows(OAuth2AuthorizationException.class, () -> provider.authorize(context));
  }

  private ClientRegistration createMockRegistration() {
    return ClientRegistration.withRegistrationId("test-id")
        .clientId("my-client-id")
        .clientSecret("my-secret")
        .tokenUri("http://localhost/token")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // Simplified for test
        .scope("openid", "profile")
        .build();
  }
}
