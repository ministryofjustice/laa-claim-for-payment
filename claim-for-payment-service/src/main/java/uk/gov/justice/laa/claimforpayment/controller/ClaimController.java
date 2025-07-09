package uk.gov.justice.laa.claimforpayment.controller;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.claimforpayment.api.ClaimsApi;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

/**
 * Controller for handling claims requests.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClaimController implements ClaimsApi {
  private final ClaimService service;

  @Override
  public ResponseEntity<List<Claim>> getClaims() {
    log.info("Getting all claims");

    return ResponseEntity.ok(service.getAllClaims());
  }

  @Override
  public ResponseEntity<Claim> getClaimById(Long id) {
    log.info("Getting claim {}", id);

    return ResponseEntity.ok(service.getClaim(id));
  }

  @Override
  public ResponseEntity<Void> createClaim(@RequestBody ClaimRequestBody claimRequestBody) {
    log.info("Creating claim {}", claimRequestBody);

    Long id = service.createClaim(claimRequestBody);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(id).toUri();
    return ResponseEntity.created(uri).build();
  }

  @Override
  public ResponseEntity<Void> updateClaim(Long id, ClaimRequestBody claimRequestBody) {
    log.info("Updating claim {}", id);

    service.updateClaim(id, claimRequestBody);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteClaim(Long id) {
    log.info("Deleting claim {}", id);

    service.deleteClaim(id);
    return ResponseEntity.noContent().build();
  }
}
