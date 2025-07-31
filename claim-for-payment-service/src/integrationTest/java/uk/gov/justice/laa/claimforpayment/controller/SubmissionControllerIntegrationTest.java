package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.claimforpayment.ClaimForPaymentApplication;
import uk.gov.justice.laa.claimforpayment.security.TestSecurityConfig;

@SpringBootTest(classes = ClaimForPaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
@Import(TestSecurityConfig.class)
class SubmissionControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private RequestPostProcessor mockPrincipal;

  private static final UUID SEEDED_SUBMISSION_ID =
      UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

  @Test
  void shouldGetAllSubmissions() throws Exception {
    mockMvc
        .perform(get("/api/v1/submissions"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1))) // you only seeded 1 submission
        .andExpect(jsonPath("$[0].id").value(SEEDED_SUBMISSION_ID.toString()));
  }

  @Test
  void shouldGetSubmissionById() throws Exception {
    mockMvc
        .perform(get("/api/v1/submissions/{submissionId}", SEEDED_SUBMISSION_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(SEEDED_SUBMISSION_ID.toString()))
        .andExpect(jsonPath("$.submissionTypeCode").value("TEST"))
        .andExpect(jsonPath("$.scheduleId").value("SCH-001"));
  }

  @Test
  void shouldCreateSubmission() throws Exception {
    String requestBody =
        """
        {
          "providerUserId": "999e4567-e89b-12d3-a456-426614174999",
          "providerOfficeId": "33333333-3333-3333-3333-333333333333",
          "submissionTypeCode": "NEW",
          "submissionDate": "2025-07-30T10:15:30",
          "submissionPeriodStartDate": "2025-07-01T00:00:00",
          "submissionPeriodEndDate": "2025-07-31T23:59:59",
          "scheduleId": "SCH-002"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/submissions")
                .with(mockPrincipal) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location")) // ✅ assert Location header is set
        .andExpect(content().string("")); // ✅ assert empty body
  }

  @Test
  void shouldUpdateSubmission() throws Exception {
    String requestBody =
        """
        {
          "providerUserId": "999e4567-e89b-12d3-a456-426614174999",
          "providerOfficeId": "123e4567-e89b-12d3-a456-426614174000",
          "submissionTypeCode": "UPDATED",
          "submissionDate": "2025-08-01T10:00:00",
          "submissionPeriodStartDate": "2025-08-01T00:00:00",
          "submissionPeriodEndDate": "2025-08-31T23:59:59",
          "scheduleId": "SCH-003"
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/submissions/{submissionId}", SEEDED_SUBMISSION_ID)
                .with(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldDeleteSubmission() throws Exception {
    mockMvc
        .perform(delete("/api/v1/submissions/{submissionId}", SEEDED_SUBMISSION_ID))
        .andExpect(status().isNoContent());
  }
}
