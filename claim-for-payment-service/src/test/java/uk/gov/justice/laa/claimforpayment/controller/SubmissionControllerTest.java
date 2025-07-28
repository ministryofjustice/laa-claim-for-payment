package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import uk.gov.justice.laa.claimforpayment.security.SecurityConfig;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

@WebMvcTest(SubmissionController.class)
@Import(SecurityConfig.class)
class SubmissionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ClaimService mockClaimService;

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
    when(mockClaimService.getAllSubmissionsForProvider(any(UUID.class))).thenReturn(submissions);

    mockMvc
        .perform(get("/api/v1/submissions?providerUseId=fcb6e669-a17e-4894-8bed-572d7357ba91"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(2)));
  }

  @Test
  void getSubmssionById_returnsOkStatusAndOneSubmission() throws Exception {
    when(mockClaimService.getSubmission(any(UUID.class)))
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
        .andExpect(jsonPath("$.id").value("123e4567-e89b-12d3-a456-426614174000"));
  }
}
