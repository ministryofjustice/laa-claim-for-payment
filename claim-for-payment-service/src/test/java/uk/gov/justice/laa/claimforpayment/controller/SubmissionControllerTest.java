package uk.gov.justice.laa.claimforpayment.controller;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.model.SubmissionRequestBody;
import uk.gov.justice.laa.claimforpayment.security.SecurityConfig;
import uk.gov.justice.laa.claimforpayment.service.DatabaseBasedClaimService;

@WebMvcTest(SubmissionController.class)
@Import(SecurityConfig.class)
class SubmissionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DatabaseBasedClaimService mockClaimService;

  @Test
  void getSubmissionsForProvider_returnsOkStatusAndAllSubmissionsForProvider() throws Exception {

    // TBC does provider user id represent a single user from a provider?
    List<Submission> submissions =
        List.of(
            Submission.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .providerOfficeId(UUID.randomUUID())
                .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
                .scheduleId("Schedule ID")
                .submissionDate(LocalDateTime.now())
                .submissionTypeCode("Type Code")
                .submissionPeriodStartDate(LocalDateTime.now())
                .submissionPeriodEndDate(LocalDateTime.now())
                .build(),
            Submission.builder()
                .id(UUID.fromString("423a6abf-f5dc-4908-9b7a-fe2607ae9c3d"))
                .providerOfficeId(UUID.randomUUID())
                .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
                .scheduleId("Schedule ID")
                .submissionDate(LocalDateTime.now())
                .submissionTypeCode("Type Code")
                .submissionPeriodStartDate(LocalDateTime.now())
                .submissionPeriodEndDate(LocalDateTime.now())
                .build());
    when(mockClaimService.getAllSubmissionsForProvider(any(UUID.class), eq(false)))
        .thenReturn(submissions);

    mockMvc
        .perform(get("/api/v1/submissions?providerUseId=fcb6e669-a17e-4894-8bed-572d7357ba91"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(2)));
  }

  @Test
  void getSubmissionsForProviderWithClTotals_returnsOkStatusAndAllSubmissionsForProviderWithTotals()
      throws Exception {

    // TBC does provider user id represent a single user from a provider?
    List<Submission> submissions =
        List.of(
            Submission.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .providerOfficeId(UUID.randomUUID())
                .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
                .scheduleId("Schedule ID")
                .submissionDate(LocalDateTime.now())
                .submissionTypeCode("Type Code")
                .submissionPeriodStartDate(LocalDateTime.now())
                .submissionPeriodEndDate(LocalDateTime.now())
                .totalClaimed(new BigDecimal(10))
                .build(),
            Submission.builder()
                .id(UUID.fromString("423a6abf-f5dc-4908-9b7a-fe2607ae9c3d"))
                .providerOfficeId(UUID.randomUUID())
                .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
                .scheduleId("Schedule ID")
                .submissionDate(LocalDateTime.now())
                .submissionTypeCode("Type Code")
                .submissionPeriodStartDate(LocalDateTime.now())
                .submissionPeriodEndDate(LocalDateTime.now())
                .totalClaimed(new BigDecimal(20))
                .build());
    when(mockClaimService.getAllSubmissionsForProvider(any(UUID.class), eq(true)))
        .thenReturn(submissions);

    mockMvc
        .perform(
            get(
                "/api/v1/submissions?includeTotals=true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$.[0].totalClaimed").value("10"))
        .andExpect(jsonPath("$.[1].totalClaimed").value("20"));
  }

  @Test
  void getSubmssionById_returnsOkStatusAndOneSubmission() throws Exception {
    when(mockClaimService.getSubmission(any(UUID.class), eq(false)))
        .thenReturn(
            Submission.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .providerOfficeId(UUID.randomUUID())
                .providerUserId(UUID.randomUUID())
                .scheduleId("Schedule ID")
                .submissionDate(LocalDateTime.now())
                .submissionTypeCode("Type Code")
                .submissionPeriodStartDate(LocalDateTime.now())
                .submissionPeriodEndDate(LocalDateTime.now())
                .build());

    mockMvc
        .perform((get("/api/v1/submissions/123e4567-e89b-12d3-a456-426614174000")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalClaimed").doesNotExist())
        .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"));
  }

  @Test
  void getSubmssionByIdWithGetClaimTotal_returnsOkStatusAndOneSubmissionWithClaimTotal()
      throws Exception {
    when(mockClaimService.getSubmission(any(UUID.class), eq(true)))
        .thenReturn(
            Submission.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .providerOfficeId(UUID.randomUUID())
                .providerUserId(UUID.randomUUID())
                .scheduleId("Schedule ID")
                .submissionDate(LocalDateTime.now())
                .submissionTypeCode("Type Code")
                .submissionPeriodStartDate(LocalDateTime.now())
                .submissionPeriodEndDate(LocalDateTime.now())
                .totalClaimed(new BigDecimal(10.0))
                .build());

    mockMvc
        .perform(
            (get("/api/v1/submissions/123e4567-e89b-12d3-a456-426614174000?includeTotals=true")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalClaimed").value("10"))
        .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"));
  }

  @Test
  void createSubmission_returnsCreatedStatusAndLocationHeader() throws Exception {
    UUID submissionId = UUID.randomUUID();
    when(mockClaimService.createSubmission(any(SubmissionRequestBody.class)))
        .thenReturn(submissionId);

    String requestBody =
        "{"
            + "\"providerUserId\":\"fcb6e669-a17e-4894-8bed-572d7357ba91\","
            + "\"providerOfficeId\":\"123e4567-e89b-12d3-a456-426614174000\","
            + "\"submissionTypeCode\":\"Type Code\","
            + "\"submissionDate\":\"2023-10-01T12:00:00\","
            + "\"submissionPeriodStartDate\":\"2023-10-01T00:00:00\","
            + "\"submissionPeriodEndDate\":\"2023-10-31T23:59:59\","
            + "\"scheduleId\":\"Schedule ID\","
            + "\"claims\":[]"
            + "}";

    mockMvc
        .perform(
            post("/api/v1/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/v1/submissions/" + submissionId));
  }

  @Test
  void updateSubmission_returnsNoContentStatus() throws Exception {
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    String requestBody =
        "{"
            + "\"providerUserId\":\"fcb6e669-a17e-4894-8bed-572d7357ba91\","
            + "\"providerOfficeId\":\"123e4567-e89b-12d3-a456-426614174000\","
            + "\"submissionTypeCode\":\"Type Code\","
            + "\"submissionDate\":\"2023-10-01T12:00:00\","
            + "\"submissionPeriodStartDate\":\"2023-10-01T00:00:00\","
            + "\"submissionPeriodEndDate\":\"2023-10-31T23:59:59\","
            + "\"scheduleId\":\"Schedule ID\","
            + "\"claims\":[]"
            + "}";
    mockMvc
        .perform(
            put("/api/v1/submissions/" + submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isNoContent());
    verify(mockClaimService).updateSubmission(eq(submissionId), any(SubmissionRequestBody.class));
  }

  @Test
  void updateSubmission_returnsBadRequestStatus() throws Exception {
    UUID submissionId = UUID.randomUUID();
    String requestBody = "{ \"invalidField\": \"value\" }"; // Invalid request body

    mockMvc
        .perform(
            put("/api/v1/submissions/" + submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());

    verify(mockClaimService, never())
        .updateSubmission(any(UUID.class), any(SubmissionRequestBody.class));
  }

  @Test
  void deleteSubmission_returnsNoContentStatus() throws Exception {
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    mockMvc
        .perform(delete("/api/v1/submissions/" + submissionId))
        .andExpect(status().isNoContent());
    verify(mockClaimService).deleteSubmission(submissionId);
  }
}
