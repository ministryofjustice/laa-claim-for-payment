package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.model.Claim;

@ExtendWith(MockitoExtension.class)
public class DraftClaimServiceTest {

  @Mock private CivilDraftClaimsApi mockDraftCivilClaimsApi;

  @InjectMocks private DraftClaimService draftClaimService;

  private CivilDraftClaim civilDraftClaim(UUID id, String payload, UUID providerUserId) {

    CivilDraftClaim claim = new CivilDraftClaim();
    claim.setId(id);
    claim.setPayload(payload);
    claim.setProviderUserId(providerUserId);
    return claim;
  }

  @Test
  void shouldGetClaimById() {
    UUID draftId = UUID.randomUUID();
    UUID providerUserId = UUID.randomUUID();
    Long claimId = 1L;

    CivilDraftClaim civilDraftClaim =
        civilDraftClaim(
            draftId,
            "payload",
            providerUserId);

    // Claim claim =
    //     Claim.builder()
    //         .id(claimId)
    //         .draftId(draftId)
    //         .ufn("UFN123")
    //         .client("John Doe")
    //         .category("Category A")
    //         .concluded(LocalDate.of(2025, 7, 1))
    //         .feeType("Fixed")
    //         .escaped(false)
    //         .counselPayment("Paid and Reconciled")
    //         .claimed(new BigDecimal("1000.00"))
    //         .build();

    when(mockDraftCivilClaimsApi.getDraftClaim(draftId)).thenReturn(civilDraftClaim);

    Claim result = draftClaimService.getClaim(claimId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(claimId);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(new BigDecimal("1000.00"));
  }

  
}
