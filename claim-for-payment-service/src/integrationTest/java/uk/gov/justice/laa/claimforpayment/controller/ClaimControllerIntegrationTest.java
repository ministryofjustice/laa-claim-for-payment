package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.claimforpayment.ClaimForPaymentApplication;
import uk.gov.justice.laa.claimforpayment.security.TestSecurityConfig;

@SpringBootTest(classes = ClaimForPaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
@Import(TestSecurityConfig.class)
class ClaimControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  private static final UUID SUBMISSION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

  @Test
  void shouldGetAllClaimsForSubmission() throws Exception {
    mockMvc
        .perform(get("/api/v1/submissions/{submissionId}/claims", SUBMISSION_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(11)));
  }

  @Test
  void shouldGetClaimForSubmission() throws Exception {
    mockMvc
        .perform(get("/api/v1/submissions/{submissionId}/claims/{claimId}", SUBMISSION_ID, 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.ufn").value("121120/467"))
        .andExpect(jsonPath("$.client").value("Giordano"))
        .andExpect(jsonPath("$.category").value("Family"))
        .andExpect(jsonPath("$.concluded").value("2025-03-18"))
        .andExpect(jsonPath("$.feeType").value("Escape"))
        .andExpect(jsonPath("$.claimed").value(234.56));
  }

  @Test
  void shouldCreateClaimForSubmission() throws Exception {
    String requestBody =
        """
        {
          "ufn": "NEW/999",
          "client": "New Client",
          "category": "Family",
          "concluded": "2025-07-09",
          "feeType": "Hourly",
          "claimed": 123.45
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/submissions/{submissionId}/claims", SUBMISSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateClaimForSubmission() throws Exception {
    String requestBody =
        """
        {
          "ufn": "UPDATED/123",
          "client": "Updated Client",
          "category": "Immigration and Asylum",
          "concluded": "2025-07-10",
          "feeType": "Fixed",
          "claimed": 999.99
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/submissions/{submissionId}/claims/{claimId}", SUBMISSION_ID, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDeleteClaimForSubmission() throws Exception {
    mockMvc
        .perform(delete("/api/v1/submissions/{submissionId}/claims/{claimId}", SUBMISSION_ID, 3))
        .andExpect(status().isNoContent());
  }
}
