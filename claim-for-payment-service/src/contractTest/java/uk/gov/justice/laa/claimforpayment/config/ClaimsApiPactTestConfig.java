package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;

/**
 * Test configuration for Pact tests. Handles creating beans which otherwise would not be
 * automatically created.
 */

@TestConfiguration
public class ClaimsApiPactTestConfig {

  /**
  * Creates a minimal ApiClient configured to point to the Pact mock server.
  */
  @Bean
  public ApiClient apiClient() {
    ApiClient client = new ApiClient();
    client.setBasePath("http://localhost:9999");
    return client;
  }

  /**
  * Creates a {@link CivilClaimsApi} using the test {@link ApiClient}.
  */
  @Bean
  public CivilClaimsApi civilClaimsApi(ApiClient apiClient) {
    return new CivilClaimsApi(apiClient);
  }
}
