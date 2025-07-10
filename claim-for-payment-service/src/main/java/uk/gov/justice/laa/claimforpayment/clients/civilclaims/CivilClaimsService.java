package uk.gov.justice.laa.claimforpayment.clients.civilclaims;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;


@Service
@RequiredArgsConstructor
public class CivilClaimsService {

    private final ClaimsApi claimsApi;

    public Mono<Void> submitClaim(ClaimRequestBody request) {
        // You’ll call your generated method and return its result
        return claimsApi.createClaim(request);
    }

}
