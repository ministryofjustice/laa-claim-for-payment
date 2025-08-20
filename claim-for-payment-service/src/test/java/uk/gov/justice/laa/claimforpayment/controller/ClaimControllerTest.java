package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.security.SecurityConfig;
import uk.gov.justice.laa.claimforpayment.service.DatabaseBasedClaimService;

@WebMvcTest(ClaimController.class)
@Import(SecurityConfig.class)
class ClaimControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DatabaseBasedClaimService mockClaimService;

  @Test
  void getClaims_returnsOkStatusAndAllClaims() throws Exception {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    List<Claim> claims =
        List.of(
            Claim.builder()
                .id(1L)
                .category("Category 1")
                .claimed(new BigDecimal(2.2))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 1")
                .providerUserId(userId1)
                .build(),
            Claim.builder()
                .id(2L)
                .category("Category 1")
                .claimed(new BigDecimal(2.5))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 2")
                .providerUserId(userId2)
                .build());

    List<Claim> claim1 = List.of(claims.getFirst());

    when(mockClaimService.getClaims()).thenReturn(claim1);

    mockMvc
        .perform(get("/api/v1/claims"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.[0].id").value("1"))
        .andExpect(jsonPath("$.*", hasSize(1)));
  }

  @Test
  void getClaimById_returnsOkStatusAndOneClaim() throws Exception {
    when(mockClaimService.getClaim(1L))
        .thenReturn(
            Claim.builder()
                .id(1L)
                .feeType("Fee type 1")
                .category("Category 1")
                .claimed(new BigDecimal(2.2))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 1")
                .build());

    mockMvc
        .perform(get("/api/v1/claims/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.feeType").value("Fee type 1"))
        .andExpect(jsonPath("$.client").value("Smith"));
  }

  @Test
  void createClaim_returnsCreatedStatusAndLocationHeader() throws Exception {

    when(mockClaimService.createClaim(any(ClaimRequestBody.class))).thenReturn(3L);

    String requestBody =
        """
        {
          "ufn": "UFN1",
          "category": "Category 1",
          "claimed": 2.2,
          "client": "Smith",
          "concluded": "2025-07-07",
          "feeType": "Fee type 1",
          "submissionId": "123e4567-e89b-12d3-a456-426614174000"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string(
                    "Location",
                    containsString(
                        "/api/v1/claims/3")));
  }

  @Test
  void createClaim_returnsBadRequestStatus() throws Exception {
    UUID submissionId = UUID.randomUUID();

    mockMvc
        .perform(
            post("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Claim Three\"}")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    String.format(
                        "{\"type\":\"about:blank\",\"title\":\"Bad"
                            + " Request\",\"status\":400,\"detail\":\"Invalid request"
                            + " content.\",\"instance\":\"/api/v1/claims\"}",
                        submissionId)));

    verify(mockClaimService, never()).createClaim(any(ClaimRequestBody.class));
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
          "claimed": 1234.56,
          "submissionId": "123e4567-e89b-12d3-a456-426614174001"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/claims/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(mockClaimService).updateClaim(eq(2L), any(ClaimRequestBody.class));
  }

  @Test
  void updateClaim_returnsBadRequestStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/claims/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"This is an updated claim two.\"}")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .string(
                    "{\"type\":\"about:blank\",\"title\":\"Bad"
                        + " Request\",\"status\":400,\"detail\":\"Invalid request"
                        + " content.\",\"instance\":"
                        + "\"/api/v1/claims/2\"}"));

    verify(mockClaimService, never())
        .updateClaim(eq(2L), any(ClaimRequestBody.class));
  }

  @Test
  void deleteClaim_returnsNoContentStatus() throws Exception {
    mockMvc
        .perform(delete("/api/v1/claims/3"))
        .andExpect(status().isNoContent());

    verify(mockClaimService)
        .deleteClaim(3L);
  }
}
