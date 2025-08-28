package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.claimforpayment.ClaimForPaymentApplication;

@SpringBootTest(classes = ClaimForPaymentApplication.class, properties = "security.enabled=false")
@AutoConfigureMockMvc
@Transactional
class ClaimControllerIntegrationNoAuthTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldGetAllClaimsForUser() throws Exception {
    mockMvc
        .perform(get("/api/v1/claims"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(11)));
  }

  @Test
  void shouldGetClaimFor() throws Exception {
    mockMvc
        .perform(get("/api/v1/claims/{claimId}", 1))
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
  void shouldCreateClaim() throws Exception {
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
            post("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateClaim() throws Exception {
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
            put("/api/v1/claims/{claimId}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDeleteClaim() throws Exception {
    mockMvc.perform(delete("/api/v1/claims/{claimId}", 3)).andExpect(status().isNoContent());
  }
}
