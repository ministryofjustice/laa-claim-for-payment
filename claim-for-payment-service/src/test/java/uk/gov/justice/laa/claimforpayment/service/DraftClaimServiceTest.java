package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.*;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.LineItemRequestBody;

/**
 * Service class for managing draft claims. This class provides methods to create, retrieve, update,
 * and delete draft claims, as well as to manage evidence associated with those claims. It interacts
 * with the CivilDraftClaimsApi to perform operations on draft claims.
 */
@ExtendWith(MockitoExtension.class)
public class DraftClaimServiceTest {

  @Mock private CivilDraftClaimsApi mockDraftCivilClaimsApi;

  @InjectMocks private DraftClaimService draftClaimService;

  private static final UUID DRAFT_ID = UUID.randomUUID();
  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();

  private CivilDraftClaim civilDraftClaim(
      UUID id, Map<String, Object> payload, UUID providerUserId) {

    CivilDraftClaim claim = new CivilDraftClaim();
    claim.setId(id);
    claim.setPayload(payload);
    claim.setProviderUserId(providerUserId);
    return claim;
  }

  @Test
  void shouldGetDraftClaimById() {
    String ufn = "UFN123";
    String client = "John Doe";
    String category = "Category A";
    String feeType = "Fixed";
    Boolean escaped = false;
    String counselPayment = "Paid and Reconciled";
    String claimed = "1000.00";

    Map<String, Object> payload = new HashMap<>();

    payload.put("id", DRAFT_ID);
    payload.put("ufn", ufn);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put("client", client);
    payload.put("category", category);
    payload.put("concluded", "2026-07-15");
    payload.put("feeType", feeType);
    payload.put("escaped", escaped);
    payload.put("counselPayment", counselPayment);
    payload.put("claimed", claimed);

    payload.put(
        "lineItems",
        List.of(
            Map.of(
                "title", "string",
                "category", category,
                "date", "2026-07-15",
                "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                "id", "3fa85f64-5717-4562-b3fc-2c963f66afa6")));

    payload.put(
        "evidence",
        List.of(
            Map.of(
                "fileKey", "string",
                "fileSize", 0,
                "submittedOn", "2026-07-15T10:34:33.079Z",
                "id", "3fa85f64-5717-4562-b3fc-2c963f66afa6")));

    CivilDraftClaim civilDraftClaim = civilDraftClaim(DRAFT_ID, payload, PROVIDER_USER_ID);

    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);

    Claim result = draftClaimService.getClaim(DRAFT_ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(DRAFT_ID);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(new BigDecimal("1000.00"));
    assertThat(result.getEvidence()).hasSize(1);
    assertThat(result.getLineItems()).hasSize(1);
    assertThat(result.getLineItems().get(0).getEvidenceItems()).hasSize(1);
    assertThat(result.getLineItems().get(0).getEvidenceItems().get(0))
        .isEqualTo(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
  }

  @Test
  void shouldGetAllDraftClaimsForProviderUser() {
    String ufn = "UFN123";
    String client = "John Doe";
    String category = "Category A";
    String feeType = "Fixed";
    Boolean escaped = false;
    String counselPayment = "Paid and Reconciled";
    String claimed = "1000.00";

    Map<String, Object> payload = new HashMap<>();

    payload.put("id", DRAFT_ID);
    payload.put("ufn", ufn);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put("client", client);
    payload.put("category", category);
    payload.put("concluded", "2026-07-15");
    payload.put("feeType", feeType);
    payload.put("escaped", escaped);
    payload.put("counselPayment", counselPayment);
    payload.put("claimed", claimed);

    payload.put(
            "lineItems",
            List.of(
                    Map.of(
                            "title", "string",
                            "category", category,
                            "date", "2026-07-15",
                            "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                            "id", "3fa85f64-5717-4562-b3fc-2c963f66afa6")));

    payload.put(
            "evidence",
            List.of(
                    Map.of(
                            "fileKey", "string",
                            "fileSize", 0,
                            "submittedOn", "2026-07-15T10:34:33.079Z",
                            "id", "3fa85f64-5717-4562-b3fc-2c963f66afa6")));

    CivilDraftClaim civilDraftClaim = civilDraftClaim(DRAFT_ID, payload, PROVIDER_USER_ID);


    int page = 0;
    int limit = 10;
    CivilDraftClaimPageResponse pageResponse = new CivilDraftClaimPageResponse();
    pageResponse.setDraftClaims(List.of(civilDraftClaim));
    pageResponse.setPage(page);
    pageResponse.setLimit(limit);
    pageResponse.setTotalPages(1);
    pageResponse.setTotal(1L);
    when(mockDraftCivilClaimsApi.getDraftClaims(any(), any())).thenReturn(pageResponse);

    ClaimPage result = draftClaimService.getClaims(page, limit);

    assertThat(result).isNotNull();
    assertThat(result.claims()).hasSize(1);
    assertThat(result.claims().getFirst().getId()).isEqualTo(DRAFT_ID);
    assertThat(result.claims().getFirst().getClient()).isEqualTo("John Doe");
    assertThat(result.claims().getFirst().getClaimed()).isEqualTo(new BigDecimal("1000.00"));
    assertThat(result.claims().getFirst().getEvidence()).hasSize(1);
    assertThat(result.claims().getFirst().getLineItems()).hasSize(1);
    assertThat(result.claims().getFirst().getLineItems().getFirst().getEvidenceItems()).hasSize(1);
    assertThat(result.claims().getFirst().getLineItems().getFirst().getEvidenceItems().getFirst())
        .isEqualTo(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
  }

  @Test
  void shouldCreateDraftClaim() {
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

    TimeBasedEpochGenerator generator = mock(TimeBasedEpochGenerator.class);

    when(generator.generate()).thenReturn(DRAFT_ID);

    try (MockedStatic<Generators> mocked = mockStatic(Generators.class)) {
      mocked.when(Generators::timeBasedEpochGenerator).thenReturn(generator);

      when(mockDraftCivilClaimsApi.createDraftClaim(any(CivilDraftClaimPost.class)))
          .thenReturn(new CivilCreateDraftClaimResponse().id(DRAFT_ID));

      UUID result = draftClaimService.createClaim(claimRequestBody, PROVIDER_USER_ID);

      assertThat(result).isNotNull().isEqualTo(DRAFT_ID);

      ArgumentCaptor<CivilDraftClaimPost> captor =
          ArgumentCaptor.forClass(CivilDraftClaimPost.class);

      verify(mockDraftCivilClaimsApi).createDraftClaim(captor.capture());

      var body = captor.getValue();

      assertThat(body.getId()).isEqualTo(DRAFT_ID);
      assertThat(body.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
      assertThat(body.getPayload())
          .containsEntry("ufn", "UFN789")
          .containsEntry("client", "Alice Example")
          .containsEntry("category", "Category C")
          .containsEntry("concluded", "2025-07-03")
          .containsEntry("feeType", "Capped")
          .containsEntry("escaped", false)
          .containsEntry("counselPayment", "Paid and Reconciled")
          .containsEntry("claimed", new BigDecimal("1500.00"))
          .containsEntry("id", DRAFT_ID)
          .containsEntry("providerUserId", PROVIDER_USER_ID);
    }
  }

  @Test
  void shouldUpdateDraftClaim() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN999")
            .client("Updated Client")
            .category("Updated Category")
            .concluded(LocalDate.of(2025, 7, 4))
            .feeType("Revised")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("2500.00"))
            .build();

    draftClaimService.updateClaim(DRAFT_ID, claimRequestBody, PROVIDER_USER_ID);

    ArgumentCaptor<CivilDraftClaimPut> captor = ArgumentCaptor.forClass(CivilDraftClaimPut.class);

    verify(mockDraftCivilClaimsApi).updateDraftClaim(eq(DRAFT_ID), captor.capture());

    var body = captor.getValue();

    assertThat(body.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
    assertThat(body.getPayload())
        .containsEntry("ufn", "UFN999")
        .containsEntry("client", "Updated Client")
        .containsEntry("category", "Updated Category")
        .containsEntry("concluded", "2025-07-04")
        .containsEntry("feeType", "Revised")
        .containsEntry("escaped", false)
        .containsEntry("counselPayment", "Paid and Reconciled")
        .containsEntry("claimed", new BigDecimal("2500.00"))
        .containsEntry("id", DRAFT_ID)
        .containsEntry("providerUserId", PROVIDER_USER_ID);
  }

  @Test
  void shouldDeleteDraftClaim() {
    draftClaimService.deleteClaim(DRAFT_ID);

    verify(mockDraftCivilClaimsApi).deleteDraftClaim(DRAFT_ID);
  }

  /** Should not delete a claim when it does not exist. */
  @Test
  void shouldNotDeleteDraftClaim_whenClaimNotFoundThenThrowsException() {
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .when(mockDraftCivilClaimsApi)
        .deleteDraftClaim(DRAFT_ID);

    assertThrows(ResourceNotFoundException.class, () -> draftClaimService.deleteClaim(DRAFT_ID));
  }

  @Test
  void shouldAddLineItemToClaim() {
    Map<String, Object> payload = new HashMap<>();

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put(
        "lineItems",
        List.of());

    UUID claimId = UUID.randomUUID();

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(claimId);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);

    LineItemRequestBody lineItemRequestBody =
        LineItemRequestBody.builder()
            .title("New Line Item")
            .category("Category D")
            .date(LocalDate.of(2025, 7, 5))
            .actualNetValue(new BigDecimal("500.00"))
            .vatApplicable(true)
            .feeEarnerName("John Smith")
            .build();

    when(mockDraftCivilClaimsApi.getDraftClaim(claimId)).thenReturn(civilDraftClaim);

    draftClaimService.addLineItemToClaim(claimId, lineItemRequestBody);

    verify(mockDraftCivilClaimsApi).patchDraftClaim(eq(claimId), any(CivilDraftClaimPatch.class));
  }

  @Test
  void shouldUpdateLineItem() {
    Map<String, Object> payload = new HashMap<>();

    UUID lineItemId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put(
        "lineItems",
        List.of(
            Map.of(
                "title", "Old Title",
                "category", "Old Category",
                "date", "2025-07-05",
                "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                "id", lineItemId)));

    UUID claimId = UUID.randomUUID();

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(claimId);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);

    LineItemRequestBody lineItemRequestBody =
        LineItemRequestBody.builder()
            .title("New Title")
            .category("Category D")
            .date(LocalDate.of(2026, 7, 5))
            .build();

    when(mockDraftCivilClaimsApi.getDraftClaim(claimId)).thenReturn(civilDraftClaim);

    draftClaimService.updateLineItem(claimId, lineItemId, lineItemRequestBody);

    verify(mockDraftCivilClaimsApi).patchDraftClaim(eq(claimId), any(CivilDraftClaimPatch.class));
  }

  @Test
  void shouldDeleteLineItem() {
    Map<String, Object> payload = new HashMap<>();

    UUID lineItemId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put(
        "lineItems",
        List.of(
            Map.of(
                "title", "Old Title",
                "category", "Old Category",
                "date", "2025-07-05",
                "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                "id", lineItemId)));

    UUID claimId = UUID.randomUUID();

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(claimId);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);

    when(mockDraftCivilClaimsApi.getDraftClaim(claimId)).thenReturn(civilDraftClaim);

    draftClaimService.deleteLineItem(claimId, lineItemId);

    verify(mockDraftCivilClaimsApi).patchDraftClaim(eq(claimId), any(CivilDraftClaimPatch.class));
  }
}
