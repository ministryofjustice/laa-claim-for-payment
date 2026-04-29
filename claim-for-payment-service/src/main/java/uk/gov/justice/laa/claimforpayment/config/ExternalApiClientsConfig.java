package uk.gov.justice.laa.claimforpayment.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.config.auth.TokenProvider;

/** Spring config for external API clients. */
@Configuration
@Slf4j
@SuppressWarnings({"checkstyle:LocalVariableName", "checkstyle:AbbreviationAsWordInName"})
public class ExternalApiClientsConfig {

  /** API client for Civil Claims. */
  @Bean
  public ApiClient civilClaimsApiClient(
      @Qualifier("civilClaimsOboRestTemplate") RestTemplate civilClaimsOboRestTemplate,
      CivilClaimsProperties props) {

    ApiClient client = new ApiClient(civilClaimsOboRestTemplate);
    client.setBasePath(props.getBaseUrl());
    return client;
  }

  @Bean
  public CivilClaimsApi civilClaimsApi(ApiClient civilClaimsApiClient) {
    return new CivilClaimsApi(civilClaimsApiClient);
  }

  /** RestTemplate for Civil Claims using Entra OBO. */
  @Bean
  public RestTemplate civilClaimsOboRestTemplate(
      @Qualifier("civilClaimsOboTokenProvider") TokenProvider tokenProvider) {

    RestTemplate restTemplate = new RestTemplate();

    restTemplate
        .getInterceptors()
        .addAll(
            Set.of(
                ((request, body, execution) -> {
                  Authentication authentication =
                      SecurityContextHolder.getContext().getAuthentication();

                  log.debug(
                      "Outgoing request using auth: {}",
                      authentication != null ? authentication.getName() : "none");

                  String accessToken = tokenProvider.getToken(authentication);

                  request.getHeaders().setBearerAuth(accessToken);

                  return execution.execute(request, body);
                }),
                ((request, body, execution) -> {
                  String incomingXAuth = null;
                  String incomingAuthorization = null;

                  if (RequestContextHolder.getRequestAttributes()
                      instanceof ServletRequestAttributes attrs) {

                    HttpServletRequest incomingRequest = attrs.getRequest();

                    incomingXAuth = incomingRequest.getHeader("X-Auth");
                    incomingAuthorization = incomingRequest.getHeader(HttpHeaders.AUTHORIZATION);
                  }

                  String xAuthToSend = null;

                  if (incomingXAuth != null && !incomingXAuth.isBlank()) {
                    xAuthToSend = incomingXAuth;

                  } else if (incomingAuthorization != null
                      && incomingAuthorization.startsWith("Bearer ")) {

                    xAuthToSend = incomingAuthorization.substring("Bearer ".length());
                  }

                  if (xAuthToSend != null) {
                    request.getHeaders().set("X-Auth", xAuthToSend);
                  }

                  Authentication authentication =
                      SecurityContextHolder.getContext().getAuthentication();

                  log.debug(
                      "Outgoing request using auth: {}",
                      authentication != null ? authentication.getName() : "none");

                  String accessToken = tokenProvider.getToken(authentication);
                  request.getHeaders().setBearerAuth(accessToken);

                  return execution.execute(request, body);
                })));

    return restTemplate;
  }
}
