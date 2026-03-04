package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;

/** Spring config for external API clients. */
@Configuration
public class ExternalApiClientsConfig {
  /**
   * Creates and configures the ApiClient for Civil Claims.
   *
   * @param props the CivilClaimsProperties containing configuration
   * @return the configured ApiClient
   */
  @Bean
  public ApiClient civilClaimsApiClient(CivilClaimsProperties props) {
    ApiClient client = new ApiClient();
    client.setBasePath(props.getBaseUrl());
    return client;
  }

  @Bean
  public CivilClaimsApi civilClaimsApi(ApiClient civilClaimsApiClient) {
    return new CivilClaimsApi(civilClaimsApiClient);
  }
}
