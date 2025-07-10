package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;

@Configuration
public class ExternalApiClientsConfig {
  @Bean
  public ApiClient civilClaimsApiClient(CivilClaimsProperties props) {
    ApiClient client = new ApiClient();
    client.setBasePath(props.getBaseUrl());
    return client;
  }

  @Bean
  public ClaimsApi civilClaimsApi(ApiClient civilClaimsApiClient) {
    return new ClaimsApi(civilClaimsApiClient);
  }
}