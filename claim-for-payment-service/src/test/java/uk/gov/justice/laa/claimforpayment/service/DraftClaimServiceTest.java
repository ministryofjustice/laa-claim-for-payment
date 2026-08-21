package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.justice.laa.claimforpayment.api.HtmlValidationUtil;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.*;
import uk.gov.justice.laa.claimforpayment.exception.DraftResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.model.*;

/**
 * Service class for managing draft claims. This class provides methods to create, retrieve, update,
 * and delete draft claims, as well as to manage evidence associated with those claims. It interacts
 * with the CivilDraftClaimsApi to perform operations on draft claims.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DraftClaimServiceTest.TestConfig.class, DraftClaimService.class})
public class DraftClaimServiceTest {

  @Configuration
  @EnableRetry(proxyTargetClass = true)
  static class TestConfig {}

  @MockitoBean // Use @MockBean for Spring Boot <= 3.3
  private CivilDraftClaimsApi mockDraftCivilClaimsApi;

  @Autowired private DraftClaimService draftClaimService;

  private static final UUID DRAFT_ID = UUID.randomUUID();
  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_ID = UUID.randomUUID();

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

    Map<String, Object> payload = new HashMap<>();
    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);
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

    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);

    draftClaimService.updateClaim(DRAFT_ID, claimRequestBody, PROVIDER_USER_ID);

    ArgumentCaptor<CivilDraftClaimPatch> captor =
        ArgumentCaptor.forClass(CivilDraftClaimPatch.class);

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(DRAFT_ID), eq(String.valueOf(civilDraftClaim.getVersion())), captor.capture());

    var body = captor.getValue();
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
  void shouldUpdateDraftClaimAndKeepLineItems() {
    Map<String, Object> payload = new HashMap<>();
    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    List<Map<String, Object>> lineItemData =  List.of(
            Map.of(
                    "title", "LineItem Title",
                    "category", "LineItem Category",
                    "date", "2025-07-05",
                    "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                    "id", "3fa85f64-5717-4562-b3fc-2c963f66afa8"));

    payload.put("lineItems", lineItemData);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);

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

    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);

    draftClaimService.updateClaim(DRAFT_ID, claimRequestBody, PROVIDER_USER_ID);

    ArgumentCaptor<CivilDraftClaimPatch> captor =
            ArgumentCaptor.forClass(CivilDraftClaimPatch.class);

    verify(mockDraftCivilClaimsApi)
            .patchDraftClaim(
                    eq(DRAFT_ID), eq(String.valueOf(civilDraftClaim.getVersion())), captor.capture());

    var body = captor.getValue();

    @SuppressWarnings("unchecked")
    List<LineItem> lineItems = (List<LineItem>) body.getPayload().get("lineItems");

    assertThat(lineItems).hasSize(1);
    assertEquals(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa8"), lineItems.getFirst().getId());
    assertEquals("LineItem Title", lineItems.getFirst().getTitle());
    assertEquals("LineItem Category", lineItems.getFirst().getCategory());
    assertEquals(LocalDate.parse("2025-07-05"), lineItems.getFirst().getDate());
    assertEquals( List.of(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")), lineItems.getFirst().getEvidenceItems());

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

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);

    LineItemRequestBody lineItemRequestBody =
        LineItemRequestBody.builder()
            .title("New Line Item")
            .category("Category D")
            .date(LocalDate.of(2025, 7, 5))
            .actualNetValue(new BigDecimal("500.00"))
            .netProfitCostAmount(new BigDecimal("600.00"))
            .netAdvocacyCostAmount(new BigDecimal("700.00"))
            .vatApplicable(true)
            .feeEarnerName("John Smith")
            .build();

    TimeBasedEpochGenerator generator = mock(TimeBasedEpochGenerator.class);

    when(generator.generate()).thenReturn(LINE_ITEM_ID);

    try (MockedStatic<Generators> mocked = mockStatic(Generators.class)) {
      mocked.when(Generators::timeBasedEpochGenerator).thenReturn(generator);
      when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);

      draftClaimService.addLineItemToClaim(DRAFT_ID, lineItemRequestBody);

      ArgumentCaptor<CivilDraftClaimPatch> captor =
          ArgumentCaptor.forClass(CivilDraftClaimPatch.class);

      verify(mockDraftCivilClaimsApi)
          .patchDraftClaim(
              eq(DRAFT_ID), eq(String.valueOf(civilDraftClaim.getVersion())), captor.capture());

      assertThat(captor.getValue().getPayload())
          .containsEntry("id", DRAFT_ID)
          .containsEntry("providerUserId", PROVIDER_USER_ID.toString());

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> lineItems =
          (List<Map<String, Object>>) captor.getValue().getPayload().get("lineItems");

      assertThat(lineItems).hasSize(1);
      assertThat(lineItems.getFirst())
          .containsEntry("id", LINE_ITEM_ID.toString())
          .containsEntry("title", "New Line Item")
          .containsEntry("category", "Category D")
          .containsEntry("date", "2025-07-05")
          .containsEntry("actualNetValue", new BigDecimal("500.00"))
          .containsEntry("netProfitCostAmount", new BigDecimal("600.00"))
          .containsEntry("netAdvocacyCostAmount", new BigDecimal("700.00"))
          .containsEntry("vatApplicable", true)
          .containsEntry("feeEarnerName", "John Smith");
    }
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

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(claimId),
            eq(String.valueOf(civilDraftClaim.getVersion())),
            any(CivilDraftClaimPatch.class));
  }

  @Test
  void shouldNotUpdateLineItem_whenLineItemNotInPayloadThenThrowsException() {
    Map<String, Object> payload = new HashMap<>();

    UUID lineItemId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put("lineItems", List.of());

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
    assertThrows(
        DraftResourceNotFoundException.class,
        () -> draftClaimService.updateLineItem(claimId, lineItemId, lineItemRequestBody));
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
    civilDraftClaim.setVersion(0L);

    when(mockDraftCivilClaimsApi.getDraftClaim(claimId)).thenReturn(civilDraftClaim);

    draftClaimService.deleteLineItem(claimId, lineItemId);

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(claimId),
            eq(String.valueOf(civilDraftClaim.getVersion())),
            any(CivilDraftClaimPatch.class));
  }

  @Test
  @DisplayName("Should add evidence to draft claim and return the evidence ID")
  void shouldAddEvidenceToClaim() {
    UploadFile uploadFile = new UploadFile("test.pdf", 100L);
    Map<String, Object> payload = new HashMap<>();

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);

    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);
    TimeBasedEpochGenerator generator = mock(TimeBasedEpochGenerator.class);

    when(generator.generate()).thenReturn(EVIDENCE_ID);

    try (MockedStatic<Generators> mocked = mockStatic(Generators.class)) {
      mocked.when(Generators::timeBasedEpochGenerator).thenReturn(generator);
      draftClaimService.addEvidenceToClaim(DRAFT_ID, uploadFile);

      ArgumentCaptor<CivilDraftClaimPatch> captor =
          ArgumentCaptor.forClass(CivilDraftClaimPatch.class);

      verify(mockDraftCivilClaimsApi)
          .patchDraftClaim(
              eq(DRAFT_ID), eq(String.valueOf(civilDraftClaim.getVersion())), captor.capture());

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> evidence =
          (List<Map<String, Object>>) captor.getValue().getPayload().get("evidence");

      assertThat(evidence).hasSize(1);
      assertThat(evidence.getFirst())
          .containsEntry("id", EVIDENCE_ID.toString())
          .containsEntry("fileKey", "test.pdf")
          .containsEntry("fileSize", 100L);

      assertThat(evidence.getFirst().get("submittedOn")).isNotNull();
    }
  }

  @Test
  @DisplayName("Should retry when patchDraftClaim encounters 409 conflict and succeed")
  void shouldRetryWhenPatchDraftClaimFailsWithConflict() {
    UploadFile uploadFile = new UploadFile("test.pdf", 100L);

    // Prepare initial claim (version 0)
    Map<String, Object> initialPayload = new HashMap<>();
    initialPayload.put("id", DRAFT_ID);
    initialPayload.put("providerUserId", PROVIDER_USER_ID);

    CivilDraftClaim claimV0 = new CivilDraftClaim();
    claimV0.setId(DRAFT_ID);
    claimV0.setPayload(initialPayload);
    claimV0.setProviderUserId(PROVIDER_USER_ID);
    claimV0.setVersion(0L);

    // Prepare updated claim for second attempt (version 1)
    Map<String, Object> updatedPayload = new HashMap<>(initialPayload);
    CivilDraftClaim claimV1 = new CivilDraftClaim();
    claimV1.setId(DRAFT_ID);
    claimV1.setPayload(updatedPayload);
    claimV1.setProviderUserId(PROVIDER_USER_ID);
    claimV1.setVersion(1L);

    // Return version 0 on first call, version 1 on second call
    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(claimV0).thenReturn(claimV1);

    doThrow(
            HttpClientErrorException.Conflict.create(
                HttpStatus.CONFLICT, "Conflict", HttpHeaders.EMPTY, new byte[0], null))
        .doReturn(new CivilDraftClaim()) // or doReturn(null)
        .when(mockDraftCivilClaimsApi)
        .patchDraftClaim(eq(DRAFT_ID), anyString(), any(CivilDraftClaimPatch.class));

    TimeBasedEpochGenerator generator = mock(TimeBasedEpochGenerator.class);
    when(generator.generate()).thenReturn(EVIDENCE_ID);

    try (MockedStatic<Generators> mocked = mockStatic(Generators.class)) {
      mocked.when(Generators::timeBasedEpochGenerator).thenReturn(generator);

      // Call method under test
      draftClaimService.addEvidenceToClaim(DRAFT_ID, uploadFile);

      // Verify getDraftClaim was called twice
      verify(mockDraftCivilClaimsApi, times(2)).getDraftClaim(DRAFT_ID);

      // Verify patchDraftClaim was called twice: first with version "0", then with version "1"
      verify(mockDraftCivilClaimsApi)
          .patchDraftClaim(eq(DRAFT_ID), eq("0"), any(CivilDraftClaimPatch.class));
      verify(mockDraftCivilClaimsApi)
          .patchDraftClaim(eq(DRAFT_ID), eq("1"), any(CivilDraftClaimPatch.class));
    }
  }

  @Test
  @DisplayName("Should delete evidence from draft claim")
  void shouldDeleteEvidenceFromDraftClaim() {

    Map<String, Object> payload = new HashMap<>();

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put(
        "evidence",
        List.of(Map.of("fileKey", "filekey", "fileSize", 100L, "id", EVIDENCE_ID.toString())));

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);
    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);
    draftClaimService.deleteEvidenceFromClaim(DRAFT_ID, EVIDENCE_ID);

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(DRAFT_ID),
            eq(String.valueOf(civilDraftClaim.getVersion())),
            any(CivilDraftClaimPatch.class));
  }

  @Test
  @DisplayName("Should delete all evidence from draft claim")
  void shouldDeleteAllEvidenceFromDraftClaim() {
    Map<String, Object> payload = new HashMap<>();

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put(
        "evidence",
        List.of(
            Map.of("fileKey", "filekey1", "fileSize", 100L, "id", UUID.randomUUID().toString()),
            Map.of("fileKey", "filekey2", "fileSize", 200L, "id", UUID.randomUUID().toString())));

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);

    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);
    draftClaimService.deleteAllEvidenceFromClaim(DRAFT_ID);

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(DRAFT_ID),
            eq(String.valueOf(civilDraftClaim.getVersion())),
            any(CivilDraftClaimPatch.class));

    ArgumentCaptor<CivilDraftClaimPatch> captor =
        ArgumentCaptor.forClass(CivilDraftClaimPatch.class);

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(DRAFT_ID), eq(String.valueOf(civilDraftClaim.getVersion())), captor.capture());

    assertThat(captor.getValue().getPayload().get("evidence")).isNotNull();
    assertThat(captor.getValue().getPayload().get("evidence")).isEqualTo(Collections.emptyList());
  }

  @Test
  void shouldDeleteAllLineItemsFromDraftClaim() {
    Map<String, Object> payload = new HashMap<>();
    LocalDate date = LocalDate.of(2025, 7, 5);

    payload.put("id", DRAFT_ID);
    payload.put("providerUserId", PROVIDER_USER_ID);
    payload.put(
        "lineItems",
        List.of(
            Map.of(
                "title", "LineItem Title",
                "category", "LineItem Category",
                "date", date.toString(),
                "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                "id", UUID.randomUUID().toString()),
            Map.of(
                "title", "LineItem Title 2",
                "category", "LineItem Category 2",
                "date", date.toString(),
                "evidenceItems", List.of("3fa85f64-5717-4562-b3fc-2c963f66afa7"),
                "id", UUID.randomUUID().toString())));

    CivilDraftClaim civilDraftClaim = new CivilDraftClaim();
    civilDraftClaim.setId(DRAFT_ID);
    civilDraftClaim.setPayload(payload);
    civilDraftClaim.setProviderUserId(PROVIDER_USER_ID);
    civilDraftClaim.setVersion(0L);

    when(mockDraftCivilClaimsApi.getDraftClaim(DRAFT_ID)).thenReturn(civilDraftClaim);
    draftClaimService.deleteAllLineItemsFromClaim(DRAFT_ID);

    ArgumentCaptor<CivilDraftClaimPatch> captor =
        ArgumentCaptor.forClass(CivilDraftClaimPatch.class);

    verify(mockDraftCivilClaimsApi)
        .patchDraftClaim(
            eq(DRAFT_ID), eq(String.valueOf(civilDraftClaim.getVersion())), captor.capture());

    assertThat(captor.getValue().getPayload().get("lineItems")).isNotNull();
    assertThat(captor.getValue().getPayload().get("lineItems")).isEqualTo(Collections.emptyList());
  }

  @Test
  void shouldRejectDraftClaimContainingScriptTag() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN789")
            .client("Alice")
            .category("<script>alert(1)</script>")
            .build();

    InvalidParameterException exception =
        assertThrows(
            InvalidParameterException.class,
            () -> draftClaimService.createClaim(
                claimRequestBody,
                PROVIDER_USER_ID));

    assertThat(exception.getMessage())
        .contains("HTML content is not permitted");

    verifyNoInteractions(mockDraftCivilClaimsApi);
  }

  @Test
  void shouldRejectDraftClaimContainingHtmlTag() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN789")
            .client("Alice Example")
            .category("<b>Category C</b>")
            .build();

    assertThrows(
        InvalidParameterException.class,
        () ->
            draftClaimService.createClaim(
                claimRequestBody,
                PROVIDER_USER_ID));

    verifyNoInteractions(mockDraftCivilClaimsApi);
  }
}
