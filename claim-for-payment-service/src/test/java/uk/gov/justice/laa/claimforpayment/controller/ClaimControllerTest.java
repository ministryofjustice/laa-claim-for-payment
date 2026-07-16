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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ClaimService mockClaimService;
  @MockitoBean
  private DraftClaimService mockDraftClaimService;

  private static final UUID CLAIM_1_ID = UUID.randomUUID();
  private static final UUID CLAIM_2_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_1_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_2_ID = UUID.randomUUID();

  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();

  private static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt =
      jwt()
          .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
          .authorities(() -> "SCOPE_Claims.Write");

  @Nested
  class GetClaims {

    @Test
    void returnsForbiddenWithoutReadScope() throws Exception {

      mockMvc.perform(get("/api/v1/claims")).andExpect(status().isForbidden());
    }

    @Test
    void returnsForbiddenWithoutProviderId() throws Exception {

      mockMvc
          .perform(get("/api/v1/claims").with(jwt().authorities(() -> "SCOPE_Claims.Write")))
          .andExpect(status().isForbidden());
    }

    @Test
    void returnsOkStatusAndAllClaims() throws Exception {
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
                  .with(validJwt))
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
    void returnsBadRequestForPageParameterOverMax() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/claims")
                  .param("page", "500000")
                  .with(validJwt))
          .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestForPageParameterBelowMin() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/claims")
                  .param("page", "-2")
                  .with(validJwt))
          .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestForLimitParameterOverMax() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/claims")
                  .param("limit", "5000001")
                  .with(validJwt))
          .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestForLimitParameterBelowMin() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/claims")
                  .param("limit", "-10")
                  .with(validJwt))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class GetClaim {

    @Test
    void returnsOkStatusAndOneClaim() throws Exception {
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
                  .with(validJwt))
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
    void returnsOkStatusAndOneDraftClaim() throws Exception {
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
                  .with(validJwt))
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
    void returnsBadRequestStatus_whenNoStatusParam() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/claims/{id}", CLAIM_1_ID)
                  .with(validJwt))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(mockClaimService);
      verifyNoInteractions(mockDraftClaimService);
    }

    @Test
    void returnsBadRequestStatus_whenInvalidStatusParam() throws Exception {
      mockMvc
          .perform(
              get("/api/v1/claims/{id}", CLAIM_1_ID)
                  .param("status", "TOFU")
                  .with(validJwt))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(mockClaimService);
      verifyNoInteractions(mockDraftClaimService);
    }
  }

  @Nested
  class CreateClaim {

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

    @Test
    void returnsCreatedStatusAndLocationHeader_whenCreatingClaim() throws Exception {
      String status = "SUBMITTED";

      when(mockClaimService.createClaim(any(ClaimRequestBody.class), any(UUID.class))).thenReturn(CLAIM_1_ID);

      mockMvc
          .perform(
              post("/api/v1/claims")
                  .param("status", status)
                  .with(validJwt)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", containsString(String.format("/api/v1/claims/%s?status=%s", CLAIM_1_ID, status))));

      verify(mockClaimService).createClaim(any(ClaimRequestBody.class), eq(PROVIDER_USER_ID));
      verifyNoInteractions(mockDraftClaimService);
    }

    @Test
    void returnsCreatedStatusAndLocationHeader_whenCreatingDraftClaim() throws Exception {
      String status = "DRAFT";

      when(mockDraftClaimService.createClaim(any(ClaimRequestBody.class), any(UUID.class))).thenReturn(CLAIM_1_ID);

      mockMvc
          .perform(
              post("/api/v1/claims")
                  .param("status", status)
                  .with(validJwt)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", containsString(String.format("/api/v1/claims/%s?status=%s", CLAIM_1_ID, status))));

      verifyNoInteractions(mockClaimService);
      verify(mockDraftClaimService).createClaim(any(ClaimRequestBody.class), eq(PROVIDER_USER_ID));
    }

    @Test
    void returnsBadRequestStatusWithInvalidFields() throws Exception {
      mockMvc
          .perform(
              post("/api/v1/claims")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\": \"Claim Three\"}")
                  .accept(MediaType.APPLICATION_JSON)
                  .with(validJwt))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.detail").value("Request validation failed."));

      verify(mockClaimService, never()).createClaim(any(ClaimRequestBody.class), any(UUID.class));
    }
  }

  @Nested
  class UpdateClaim {

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

    @Test
    void returnsNoContentStatus_whenSubmittedClaim() throws Exception {
      doNothing().when(mockClaimService).updateClaim(any(UUID.class), any(ClaimRequestBody.class));

      mockMvc
          .perform(
              put("/api/v1/claims/{id}", CLAIM_1_ID)
                  .param("status", "SUBMITTED")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
                  .accept(MediaType.APPLICATION_JSON)
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verify(mockClaimService).updateClaim(eq(CLAIM_1_ID), any(ClaimRequestBody.class));
      verifyNoInteractions(mockDraftClaimService);
    }

    @Test
    void returnsNoContentStatus_whenDraftClaim() throws Exception {
      doNothing().when(mockDraftClaimService).updateClaim(any(UUID.class), any(ClaimRequestBody.class));

      mockMvc
          .perform(
              put("/api/v1/claims/{id}", CLAIM_1_ID)
                  .param("status", "DRAFT")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
                  .accept(MediaType.APPLICATION_JSON)
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verifyNoInteractions(mockClaimService);
      verify(mockDraftClaimService).updateClaim(eq(CLAIM_1_ID), any(ClaimRequestBody.class));
    }

    @Test
    void returnsBadRequestStatus_whenInvalidBody() throws Exception {
      mockMvc
          .perform(
              put("/api/v1/claims/{id}", CLAIM_1_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"description\": \"This is an updated claim two.\"}")
                  .accept(MediaType.APPLICATION_JSON)
                  .with(validJwt))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.detail").value("Request validation failed."))
          .andExpect(status().isBadRequest());

      verify(mockClaimService, never()).updateClaim(eq(CLAIM_1_ID), any(ClaimRequestBody.class));
    }

    @Test
    void returnsBadRequestStatus_whenNoStatusParam() throws Exception {
      mockMvc
          .perform(
              put("/api/v1/claims/{id}", CLAIM_1_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
                  .accept(MediaType.APPLICATION_JSON)
                  .with(validJwt))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(mockClaimService);
      verifyNoInteractions(mockDraftClaimService);
    }

    @Test
    void returnsBadRequestStatus_whenInvalidStatusParam() throws Exception {
      mockMvc
          .perform(
              put("/api/v1/claims/{id}", CLAIM_1_ID)
                  .param("status", "cheese")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody)
                  .accept(MediaType.APPLICATION_JSON)
                  .with(validJwt))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(mockClaimService);
      verifyNoInteractions(mockDraftClaimService);
    }
  }

  @Nested
  class DeleteClaim {

    @Test
    void returnsNoContentStatus_whenDeletingClaim() throws Exception {
      mockMvc
          .perform(
              delete("/api/v1/claims/{id}", CLAIM_1_ID)
                  .param("status", "SUBMITTED")
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verify(mockClaimService).deleteClaim(CLAIM_1_ID);
      verifyNoInteractions(mockDraftClaimService);
    }

    @Test
    void returnsNoContentStatus_whenDeletingDraftClaim() throws Exception {
      mockMvc
          .perform(
              delete("/api/v1/claims/{id}", CLAIM_1_ID)
                  .param("status", "DRAFT")
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verifyNoInteractions(mockClaimService);
      verify(mockDraftClaimService).deleteClaim(CLAIM_1_ID);
    }
  }

  @Nested
  class AddEvidenceToClaim {

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
                  .with(validJwt))
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
                  .with(validJwt))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("Failed to upload file: Upload failed"))
          .andExpect(jsonPath("$.file.filename").value("file1.pdf"))
          .andExpect(jsonPath("$.file.originalname").value("file1.pdf"))
          .andExpect(jsonPath("$.file.filesize").value(11));
    }
  }

  @Nested
  class LinkEvidenceToLineItem {

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
                  .with(validJwt))
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
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verify(mockClaimService).linkEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID, EVIDENCE_2_ID));
    }
  }

  @Nested
  class AddEvidenceToLineItem {

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
                  .with(validJwt))
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
  }

  @Nested
  class DeleteEvidenceFromClaim {

    @Test
    void shouldDeleteEvidenceFromClaim() throws Exception {
      mockMvc
          .perform(
              delete("/api/v1/claims/{claimId}/evidence/{evidenceId}", CLAIM_1_ID, EVIDENCE_1_ID)
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verify(mockClaimService).deleteEvidenceFromClaim(CLAIM_1_ID, EVIDENCE_1_ID);
    }
  }

  @Nested
  class UnlinkEvidenceFromLineItem {

    @Test
    void shouldUnlinkEvidenceFromLineItem() throws Exception {
      mockMvc
          .perform(
              delete("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence/{evidenceId}", CLAIM_1_ID, LINE_ITEM_ID, EVIDENCE_1_ID)
                  .with(validJwt))
          .andExpect(status().isNoContent());

      verify(mockClaimService).unlinkEvidenceFromLineItem(CLAIM_1_ID, LINE_ITEM_ID, EVIDENCE_1_ID);
    }
  }
}
