package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.claimforpayment.config.ScopePropertyConfig;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.security.SecurityConfig;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;
import uk.gov.justice.laa.claimforpayment.service.DraftClaimService;

@WebMvcTest(controllers = ClaimController.class)
@TestPropertySource(properties = "security.enabled=true")
@Import({SecurityConfig.class, ScopePropertyConfig.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class ClaimControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ClaimService mockClaimService;
  @MockitoBean private DraftClaimService mockDraftClaimService;

  private static final UUID CLAIM_1_ID = UUID.randomUUID();
  private static final UUID CLAIM_2_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_1_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_2_ID = UUID.randomUUID();

  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();

  @Test
  void getClaims_returnsForbiddenWithoutReadScope() throws Exception {

    mockMvc.perform(get("/api/v1/claims")).andExpect(status().isForbidden());
  }

  @Test
  void getClaims_returnsForbiddenWithoutProviderId() throws Exception {

    mockMvc
        .perform(get("/api/v1/claims").with(jwt().authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isForbidden());
  }

  @Test
  void getClaims_returnsOkStatusAndAllClaims() throws Exception {
    List<Claim> claims =
        List.of(
            Claim.builder()
                .id(CLAIM_1_ID)
                .category("Category 1")
                .claimed(new BigDecimal(2.2))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 1")
                .escaped(false)
                .counselPayment("Paid and Reconciled")
                .providerUserId(PROVIDER_USER_ID)
                .build(),
            Claim.builder()
                .id(CLAIM_2_ID)
                .category("Category 1")
                .claimed(new BigDecimal(2.5))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 2")
                .escaped(false)
                .counselPayment("Paid and Reconciled")
                .providerUserId(UUID.randomUUID())
                .build());

    List<Claim> claim1 = List.of(claims.getFirst());
    ClaimPage claimPage = new ClaimPage(claim1, 0, 100, 1, 1);
    when(mockClaimService.getClaims(anyInt(), anyInt())).thenReturn(claimPage);

    mockMvc
        .perform(
            get("/api/v1/claims")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.claims[0].id").value(CLAIM_1_ID.toString()))
        .andExpect(jsonPath("$.claims", hasSize(1)))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.limit").value(100))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void getClaimById_returnsOkStatusAndOneClaim() throws Exception {
    when(mockClaimService.getClaim(CLAIM_1_ID))
        .thenReturn(
            Claim.builder()
                .id(CLAIM_1_ID)
                .feeType("Fee type 1")
                .category("Category 1")
                .claimed(new BigDecimal(2.2))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 1")
                .escaped(true)
                .counselPayment("Paid and Reconciled")
                .build());

    mockMvc
        .perform(
            get("/api/v1/claims/{id}", CLAIM_1_ID)
                    .param("status", "SUBMITTED")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CLAIM_1_ID.toString()))
        .andExpect(jsonPath("$.feeType").value("Fee type 1"))
        .andExpect(jsonPath("$.escaped").value(true))
        .andExpect(jsonPath("$.counselPayment").value("Paid and Reconciled"))
        .andExpect(jsonPath("$.client").value("Smith"));

    verifyNoInteractions(mockDraftClaimService);
  }

  @Test
  void getClaimById_returnsOkStatusAndOneDraftClaim() throws Exception {
    when(mockDraftClaimService.getClaim(CLAIM_1_ID))
        .thenReturn(
            Claim.builder()
                .id(CLAIM_1_ID)
                .feeType("Fee type 1")
                .category("Category 1")
                .claimed(new BigDecimal(2.2))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 1")
                .escaped(true)
                .counselPayment("Paid and Reconciled")
                .build());

    mockMvc
        .perform(
            get("/api/v1/claims/{id}", CLAIM_1_ID)
                    .param("status", "DRAFT")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(CLAIM_1_ID.toString()))
        .andExpect(jsonPath("$.feeType").value("Fee type 1"))
        .andExpect(jsonPath("$.escaped").value(true))
        .andExpect(jsonPath("$.counselPayment").value("Paid and Reconciled"))
        .andExpect(jsonPath("$.client").value("Smith"));

    verifyNoInteractions(mockClaimService);
  }

  @Test
  void getClaimById_returnsBadRequestStatus_whenNoStatusParam() throws Exception {

    mockMvc
        .perform(
            get("/api/v1/claims/{id}", CLAIM_1_ID)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(mockClaimService);
    verifyNoInteractions(mockDraftClaimService);
  }

  @Test
  void getClaimById_returnsBadRequestStatus_whenInvalidStatusParam() throws Exception {

    mockMvc
        .perform(
            get("/api/v1/claims/{id}", CLAIM_1_ID)
                    .param("status", "TOFU")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(mockClaimService);
    verifyNoInteractions(mockDraftClaimService);
  }

  @Test
  void createClaim_returnsCreatedStatusAndLocationHeader() throws Exception {
    when(mockClaimService.createClaim(any(ClaimRequestBody.class), any(UUID.class))).thenReturn(CLAIM_1_ID);

    String requestBody =
        """
        {
          "ufn": "UFN1",
          "category": "Category 1",
          "claimed": 2.2,
          "client": "Smith",
          "concluded": "2025-07-07",
          "feeType": "Fee type 1",
          "escaped": false,
          "counselPayment": "Paid and Reconciled"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/claims")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString(String.format("/api/v1/claims/%s", CLAIM_1_ID))));
  }

  @Test
  void createClaim_returnsBadRequestStatusWithInvalidFields() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Claim Three\"}")
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Request validation failed."));

    verify(mockClaimService, never()).createClaim(any(ClaimRequestBody.class), any(UUID.class));
  }

  @Test
  void updateClaim_returnsNoContentStatus() throws Exception {
    String requestBody =
        """
        {
          "ufn": "UFN2",
          "client": "Updated Client",
          "category": "Updated Category",
          "concluded": "2025-07-08",
          "feeType": "Updated Fee Type",
          "escaped": false,
          "counselPayment": "Paid and Reconciled",
          "claimed": 1234.56
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/claims/{id}", CLAIM_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).updateClaim(eq(CLAIM_1_ID), any(ClaimRequestBody.class));
  }

  @Test
  void updateClaim_returnsBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/claims/{id}", CLAIM_1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"This is an updated claim two.\"}")
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Request validation failed."))
        .andExpect(status().isBadRequest());

    verify(mockClaimService, never()).updateClaim(eq(CLAIM_1_ID), any(ClaimRequestBody.class));
  }

  @Test
  void deleteClaim_returnsNoContentStatus() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/claims/{id}", CLAIM_1_ID)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).deleteClaim(CLAIM_1_ID);
  }

  @Test
  void getClaims_returnsBadRequestForPageParameterOverMax() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/claims")
                .param("page", "500000")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getClaims_returnsBadRequestForPageParameterBelowMin() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/claims")
                .param("page", "-2")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getClaims_returnsBadRequestForLimitParameterOverMax() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/claims")
                .param("limit", "5000001")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getClaims_returnsBadRequestForLimitParameterBelowMin() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/claims")
                .param("limit", "-10")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldAddEvidenceToClaim() throws Exception {
    when(mockClaimService.addEvidenceToClaim(eq(CLAIM_1_ID), any())).thenReturn(EVIDENCE_1_ID);

    MockMultipartFile file =
        new MockMultipartFile(
            "documents", // must match @RequestParam name
            "file1.pdf",
            "application/pdf",
            "Test file 1".getBytes());

    mockMvc
        .perform(
            multipart("/api/v1/claims/{claimId}/upload-evidence", CLAIM_1_ID)
                .file(file)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(String.format("File uploaded with ID: %s", EVIDENCE_1_ID)))
        .andExpect(jsonPath("$.file.filename").value("file1.pdf"))
        .andExpect(jsonPath("$.file.originalname").value("file1.pdf"))
        .andExpect(jsonPath("$.file.filesize").value(11))
        .andExpect(jsonPath("$.evidenceId").value(EVIDENCE_1_ID.toString()));
  }

  @Test
  void shouldReturnBadRequestWithErrorMessageWhenAddEvidenceToClaimFails() throws Exception {
    when(mockClaimService.addEvidenceToClaim(eq(CLAIM_1_ID), any()))
        .thenThrow(new RuntimeException("Upload failed"));

    MockMultipartFile file =
        new MockMultipartFile(
            "documents", // must match @RequestParam name
            "file1.pdf",
            "application/pdf",
            "Test file 1".getBytes());

    mockMvc
        .perform(
            multipart("/api/v1/claims/{id}/upload-evidence", CLAIM_1_ID)
                .file(file)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Failed to upload file: Upload failed"))
        .andExpect(jsonPath("$.file.filename").value("file1.pdf"))
        .andExpect(jsonPath("$.file.originalname").value("file1.pdf"))
        .andExpect(jsonPath("$.file.filesize").value(11));
  }

  @Test
  void shouldLinkEvidenceToLineItem() throws Exception {
    String requestBody = String.format("""
        [
          "%s"
        ]
        """, EVIDENCE_1_ID);

    mockMvc
        .perform(
            post("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence", CLAIM_1_ID, LINE_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).linkEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID));
  }

  @Test
  void shouldLinkMultipleEvidenceToLineItem() throws Exception {
    String requestBody = String.format("""
        [
          "%s",
          "%s"
        ]
        """, EVIDENCE_1_ID, EVIDENCE_2_ID);

    mockMvc
        .perform(
            post("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence", CLAIM_1_ID, LINE_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).linkEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID, EVIDENCE_2_ID));
  }

  @Test
  void shouldAddNewEvidenceToLineItem() throws Exception {
    when(mockClaimService.addEvidenceToClaim(eq(CLAIM_1_ID), any())).thenReturn(EVIDENCE_1_ID);

    MockMultipartFile file =
        new MockMultipartFile(
            "documents", // must match @RequestParam name
            "file1.pdf",
            "application/pdf",
            "Test file 1".getBytes());

    mockMvc
        .perform(
            multipart("/api/v1/claims/{claimId}/line-items/{lineItemId}/upload-evidence", CLAIM_1_ID, LINE_ITEM_ID)
                .file(file)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.message")
                .value(String.format("File uploaded with ID: %s and linked to line item: %s", EVIDENCE_1_ID, LINE_ITEM_ID)))
        .andExpect(jsonPath("$.file.filename").value("file1.pdf"))
        .andExpect(jsonPath("$.file.originalname").value("file1.pdf"))
        .andExpect(jsonPath("$.file.filesize").value(11))
        .andExpect(jsonPath("$.evidenceId").value(EVIDENCE_1_ID.toString()));

    verify(mockClaimService).linkEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID));
  }

  @Test
  void shouldDeleteEvidenceFromClaim() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/claims/{claimId}/evidence/{evidenceId}", CLAIM_1_ID, EVIDENCE_1_ID)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).deleteEvidenceFromClaim(CLAIM_1_ID, EVIDENCE_1_ID);
  }

  @Test
  void shouldUnlinkEvidenceFromLineItem() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence/{evidenceId}", CLAIM_1_ID, LINE_ITEM_ID, EVIDENCE_1_ID)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).unlinkEvidenceFromLineItem(CLAIM_1_ID, LINE_ITEM_ID, EVIDENCE_1_ID);
  }
}
