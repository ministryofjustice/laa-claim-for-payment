package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;

@TestConfiguration
public class ClaimsApiPactTestConfig {

  /** Creates a minimal ApiClient configured to point to the Pact mock server. */
  @Bean
  public ApiClient apiClient(
      @Value("${civilclaims.api.base-url:http://localhost:9999}") String baseUrl) {
    ApiClient client = new ApiClient();
    client.setBasePath(baseUrl);
    return client;
  }

  /** Creates a {@link CivilClaimsApi} using the test {@link ApiClient}. */
  @Bean
  @Primary
  public CivilClaimsApi civilClaimsApi(ApiClient apiClient) {
    return new CivilClaimsApi(apiClient);
  }

  /** Creates a {@link CivilDraftClaimsApi} using the test {@link ApiClient}. */
  @Bean
  public CivilDraftClaimsApi draftClaimsApi(ApiClient apiClient) {
    return new CivilDraftClaimsApi(apiClient);
  }
}
