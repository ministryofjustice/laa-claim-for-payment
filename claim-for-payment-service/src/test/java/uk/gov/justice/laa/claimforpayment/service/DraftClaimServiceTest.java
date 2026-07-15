package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateDraftClaimResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPost;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

/**
 * Service class for managing draft claims. This class provides methods to create, retrieve, update,
 * and delete draft claims, as well as to manage evidence associated with those claims. It interacts
 * with the CivilDraftClaimsApi to perform operations on draft claims.
 */
@ExtendWith(MockitoExtension.class)
public class DraftClaimServiceTest {

  @Mock private CivilDraftClaimsApi mockDraftCivilClaimsApi;

  @InjectMocks private DraftClaimService draftClaimService;

  private static final UUID CLAIM_1_ID = UUID.randomUUID();
  private static final UUID CLAIM_2_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_1_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_2_ID = UUID.randomUUID();

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

    String ufn = "UFN123";
    String client = "John Doe";
    String category = "Category A";
    String feeType = "Fixed";
    Boolean escaped = false;
    String counselPayment = "Paid and Reconciled";
    String claimed = "1000.00";

    CivilDraftClaim civilDraftClaim =
        civilDraftClaim(
            draftId,
            String.format(
                """
                {
                  "id": "%s",
                  "lineItems": [
                    {
                      "title": "string",
                      "category": "%s",
                      "date": "2026-07-15",
                      "evidenceItems": [
                        "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                      ],
                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                    }
                  ],
                  "evidence": [
                    {
                      "fileKey": "string",
                      "fileSize": 0,
                      "submittedOn": "2026-07-15T10:34:33.079Z",
                      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                    }
                  ],
                  "ufn": "%s",
                  "providerUserId": "%s",
                  "client": "%s",
                  "category": "%s",
                  "concluded": "2026-07-15",
                  "feeType": "%s",
                  "escaped": %b,
                  "counselPayment": "%s",
                  "claimed": %s
                }
                """,
                draftId.toString(),
                category,
                ufn,
                providerUserId.toString(),
                client,
                category,
                feeType,
                escaped,
                counselPayment,
                claimed),
            providerUserId);

    when(mockDraftCivilClaimsApi.getDraftClaim(draftId)).thenReturn(civilDraftClaim);

    Claim result = draftClaimService.getClaim(draftId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(draftId);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(new BigDecimal("1000.00"));
    assertThat(result.getEvidence()).hasSize(1);
    assertThat(result.getLineItems()).hasSize(1);
    assertThat(result.getLineItems().get(0).getEvidenceItems()).hasSize(1);
    assertThat(result.getLineItems().get(0).getEvidenceItems().get(0))
        .isEqualTo(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
  }

  @Test
  void shouldCreateClaim() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN789")
            .client("Alice Example")
            .category("Category C")
            .concluded(LocalDate.of(2025, 7, 3))
            .feeType("Capped")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("1500.00"))
            .build();

    UUID providerUserId = UUID.randomUUID();

    when(mockDraftCivilClaimsApi.createDraftClaim(any(CivilDraftClaimPost.class)))
        .thenReturn(new CivilCreateDraftClaimResponse().id(CLAIM_1_ID));

    UUID result = draftClaimService.createClaim(claimRequestBody, providerUserId);

    assertThat(result).isNotNull().isEqualTo(CLAIM_1_ID);

    ArgumentCaptor<CivilDraftClaimPost> captor = ArgumentCaptor.forClass(CivilDraftClaimPost.class);

    verify(mockDraftCivilClaimsApi).createDraftClaim(captor.capture());

    var body = captor.getValue();

    assertThat(body.getId()).isNotNull();
    assertThat(body.getProviderUserId()).isEqualTo(providerUserId);
  }
}
