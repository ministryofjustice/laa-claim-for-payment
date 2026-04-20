package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.config.auth.TokenProvider;

/** Spring config for external API clients. */
@Configuration
public class ExternalApiClientsConfig {

  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(ExternalApiClientsConfig.class);

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
        .add(
            (request, body, execution) -> {
              Authentication authentication =
                  SecurityContextHolder.getContext().getAuthentication();

              log.debug(
                  "Outgoing request using auth: {}",
                  authentication != null ? authentication.getName() : "none");

              String accessToken = tokenProvider.getToken(authentication);

              request.getHeaders().setBearerAuth(accessToken);

              return execution.execute(request, body);
            });

    return restTemplate;
  }
}
