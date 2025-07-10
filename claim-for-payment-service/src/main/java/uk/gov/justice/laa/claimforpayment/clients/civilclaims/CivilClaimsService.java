package uk.gov.justice.laa.claimforpayment.clients.civilclaims;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.ClaimRequestBody;

/**
 * Service for handling Civil Claims operations.
 */
@Service
@RequiredArgsConstructor
public class CivilClaimsService {

  private final ClaimsApi claimsApi;

  public Mono<Void> submitClaim(ClaimRequestBody request) {
    // You’ll call your generated method and return its result
    return claimsApi.createClaim(request);
  }

}
