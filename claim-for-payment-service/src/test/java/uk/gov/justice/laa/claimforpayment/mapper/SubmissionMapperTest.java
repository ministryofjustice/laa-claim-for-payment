package uk.gov.justice.laa.claimforpayment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.claimforpayment.entity.SubmissionEntity;
import uk.gov.justice.laa.claimforpayment.model.Submission;

@ExtendWith(MockitoExtension.class)
class SubmissionMapperTest {
  private static UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
  private static UUID officeId = UUID.randomUUID();
  private static UUID providerUserId = UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91");
  private static LocalDateTime submissionDate = LocalDateTime.now();
  private static String typeCode = "Type Code";
  private static LocalDateTime submissionPeriodStartDate = LocalDateTime.now();

  @InjectMocks private SubmissionMapper submissionMapper = new SubmissionMapperImpl();

  @Test
  void shouldMapToSubmissionEntity() {
    Submission submission =
        Submission.builder()
            .id(submissionId)
            .providerOfficeId(officeId)
            .providerUserId(providerUserId)
            .scheduleId("Schedule ID")
            .submissionDate(submissionDate)
            .submissionTypeCode(typeCode)
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    SubmissionEntity result = submissionMapper.toSubmissionEntity(submission);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(submissionId);
    assertThat(result.getProviderOfficeId()).isEqualTo(officeId);
    assertThat(result.getProviderUserId()).isEqualTo(providerUserId);
    assertThat(result.getScheduleId()).isEqualTo("Schedule ID");
    assertThat(result.getSubmissionDate()).isEqualTo(submissionDate);
    assertThat(result.getSubmissionTypeCode()).isEqualTo(typeCode);
    assertThat(result.getSubmissionPeriodStartDate()).isEqualTo(submissionPeriodStartDate);
    assertThat(result.getSubmissionPeriodEndDate()).isNotNull();
  }

  @Test
  void shouldMapToSubmission() {
    SubmissionEntity submissionEntity =
        SubmissionEntity.builder()
            .id(submissionId)
            .providerOfficeId(officeId)
            .providerUserId(providerUserId)
            .scheduleId("Schedule ID")
            .submissionDate(submissionDate)
            .submissionTypeCode(typeCode)
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();
    Submission result = submissionMapper.toSubmission(submissionEntity);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(submissionId);
    assertThat(result.getProviderOfficeId()).isEqualTo(officeId);
    assertThat(result.getProviderUserId()).isEqualTo(providerUserId);
    assertThat(result.getScheduleId()).isEqualTo("Schedule ID");
    assertThat(result.getSubmissionDate()).isEqualTo(submissionDate);
    assertThat(result.getSubmissionTypeCode()).isEqualTo(typeCode);
    assertThat(result.getSubmissionPeriodStartDate()).isEqualTo(submissionPeriodStartDate);
    assertThat(result.getSubmissionPeriodEndDate()).isNotNull();
  }

  // @Test
  // void shouldMapToClaim() {
  //   ClaimEntity claimEntity = ClaimEntity.builder()
  //       .id(CLAIM_ID)
  //       .ufn(UFN)
  //       .client(CLIENT)
  //       .category(CATEGORY)
  //       .concluded(CONCLUDED)
  //       .feeType(FEE_TYPE)
  //       .claimed(CLAIMED)
  //       .build();

  //   Claim result = claimMapper.toClaim(claimEntity);

  //   assertThat(result).isNotNull();
  //   assertThat(result.getId()).isEqualTo(CLAIM_ID);
  //   assertThat(result.getUfn()).isEqualTo(UFN);
  //   assertThat(result.getClient()).isEqualTo(CLIENT);
  //   assertThat(result.getCategory()).isEqualTo(CATEGORY);
  //   assertThat(result.getConcluded()).isEqualTo(CONCLUDED);
  //   assertThat(result.getFeeType()).isEqualTo(FEE_TYPE);
  //   assertThat(result.getClaimed()).isEqualTo(CLAIMED);
  // }
}
