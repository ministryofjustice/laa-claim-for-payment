package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;

/**
 * Test configuration for Pact tests. Handles creating beans which otherwise would not be
 * automatically created.
 */

//
//@TestConfiguration
//public class ClaimsApiPactTestConfig {
//
//  @Bean
//  @Primary
//  public uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient civilClaimsApiClient() {
//    var client =
//        new uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient();
//
//    client.setBasePath("http://localhost:9999");
//    return client;
//  }
//}

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

  /**
   * Creates a {@link CivilDraftClaimsApi} using the test {@link ApiClient}.
   */
  @Bean
  public CivilDraftClaimsApi civilDraftClaimsApi(ApiClient apiClient) {
    return new CivilDraftClaimsApi(apiClient);
  }
}
