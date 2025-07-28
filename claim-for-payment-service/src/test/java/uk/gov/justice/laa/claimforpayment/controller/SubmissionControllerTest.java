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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.service.ClaimService;

@WebMvcTest(SubmissionController.class)
class SubmissionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ClaimService mockClaimService;

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
