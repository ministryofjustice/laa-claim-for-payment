package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.claimforpayment.config.AuthConfig;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.security.SecurityConfig;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

@WebMvcTest(controllers = ClaimController.class)
@TestPropertySource(properties = "security.enabled=true")
@ImportAutoConfiguration(exclude = OAuth2ResourceServerAutoConfiguration.class)
@Import({SecurityConfig.class, AuthConfig.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class ClaimControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ClaimService mockClaimService;

  @Test
  void getClaims_returnsForbiddendWithoutReadScope() throws Exception {

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
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    UUID providerUserId2 = UUID.randomUUID();

    List<Claim> claims =
        List.of(
            Claim.builder()
                .id(1L)
                .category("Category 1")
                .claimed(new BigDecimal(2.2))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 1")
                .providerUserId(providerUserId1)
                .build(),
            Claim.builder()
                .id(2L)
                .category("Category 1")
                .claimed(new BigDecimal(2.5))
                .client("Smith")
                .concluded(LocalDate.now())
                .feeType("Fee type 2")
                .providerUserId(providerUserId2)
                .build());

    List<Claim> claim1 = List.of(claims.getFirst());
    ClaimPage claimPage = new ClaimPage(claim1, 0, 100, 1, 1);
    when(mockClaimService.getClaims(anyInt(), anyInt())).thenReturn(claimPage);

    mockMvc
        .perform(
            get("/api/v1/claims")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.claims[0].id").value("1"))
        .andExpect(jsonPath("$.claims", hasSize(1)))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.limit").value(100))
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  @Test
  void getClaimById_returnsOkStatusAndOneClaim() throws Exception {
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

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
        .perform(
            get("/api/v1/claims/1")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.feeType").value("Fee type 1"))
        .andExpect(jsonPath("$.client").value("Smith"));
  }

  @Test
  void createClaim_returnsCreatedStatusAndLocationHeader() throws Exception {
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    when(mockClaimService.createClaim(any(ClaimRequestBody.class), any(UUID.class))).thenReturn(3L);

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
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/api/v1/claims/3")));
  }

  @Test
  void createClaim_returnsBadRequestStatusWithInvalidFields() throws Exception {
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    mockMvc
        .perform(
            post("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Claim Three\"}")
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Request validation failed."));

    verify(mockClaimService, never()).createClaim(any(ClaimRequestBody.class), any(UUID.class));
  }

  @Test
  void updateClaim_returnsNoContentStatus() throws Exception {
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

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
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).updateClaim(eq(2L), any(ClaimRequestBody.class));
  }

  @Test
  void updateClaim_returnsBadRequestStatus() throws Exception {
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    mockMvc
        .perform(
            put("/api/v1/claims/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"This is an updated claim two.\"}")
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Request validation failed."))
        .andExpect(status().isBadRequest());

    verify(mockClaimService, never()).updateClaim(eq(2L), any(ClaimRequestBody.class));
  }

  @Test
  void deleteClaim_returnsNoContentStatus() throws Exception {
    UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    mockMvc
        .perform(
            delete("/api/v1/claims/3")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());

    verify(mockClaimService).deleteClaim(3L);
  }
}
