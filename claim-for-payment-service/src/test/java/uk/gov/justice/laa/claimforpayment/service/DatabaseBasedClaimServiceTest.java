package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.entity.SubmissionEntity;
import uk.gov.justice.laa.claimforpayment.exception.ClaimNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.SubmissionNotFoundException;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimMapper;
import uk.gov.justice.laa.claimforpayment.mapper.SubmissionMapper;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.Submission;
import uk.gov.justice.laa.claimforpayment.model.SubmissionRequestBody;
import uk.gov.justice.laa.claimforpayment.repository.ClaimRepository;
import uk.gov.justice.laa.claimforpayment.repository.SubmissionRepository;

@ExtendWith(MockitoExtension.class)
class DatabaseBasedClaimServiceTest {

  @Mock private ClaimRepository mockClaimRepository;
  @Mock private SubmissionRepository mockSubmissionRepository;

  @Mock private ClaimMapper mockClaimMapper;
  @Mock private SubmissionMapper mockSubmissionMapper;

  @InjectMocks private DatabaseBasedClaimService claimService;

  @Test
  void shouldGetAllSubmissionsForProvider() {
    SubmissionEntity firstSubmissionEntity =
        SubmissionEntity.builder()
            .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            .providerOfficeId(UUID.randomUUID())
            .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
            .scheduleId("Schedule ID")
            .submissionDate(LocalDateTime.now())
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    SubmissionEntity secondSubmissionEntity =
        SubmissionEntity.builder()
            .id(UUID.fromString("423a6abf-f5dc-4908-9b7a-fe2607ae9c3d"))
            .providerOfficeId(UUID.randomUUID())
            .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
            .scheduleId("Schedule ID")
            .submissionDate(LocalDateTime.now())
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    Submission firstSubmission =
        Submission.builder()
            .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            .providerOfficeId(UUID.randomUUID())
            .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
            .scheduleId("Schedule ID")
            .submissionDate(LocalDateTime.now())
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    Submission secondSubmission =
        Submission.builder()
            .id(UUID.fromString("423a6abf-f5dc-4908-9b7a-fe2607ae9c3d"))
            .providerOfficeId(UUID.randomUUID())
            .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
            .scheduleId("Schedule ID")
            .submissionDate(LocalDateTime.now())
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    when(mockSubmissionRepository.findByProviderUserId(any(UUID.class)))
        .thenReturn(List.of(firstSubmissionEntity, secondSubmissionEntity));
    when(mockSubmissionMapper.toSubmission(firstSubmissionEntity)).thenReturn(firstSubmission);
    when(mockSubmissionMapper.toSubmission(secondSubmissionEntity)).thenReturn(secondSubmission);

    List<Submission> result = claimService.getAllSubmissionsForProvider(UUID.randomUUID(), false);

    assertThat(result).hasSize(2).contains(firstSubmission, secondSubmission);
  }

  @Test
  void shouldGetAllSubmissionsForProviderWithClaimTotals() {
    SubmissionEntity firstSubmissionEntity =
        SubmissionEntity.builder()
            .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            .providerOfficeId(UUID.randomUUID())
            .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
            .scheduleId("Schedule ID")
            .submissionDate(LocalDateTime.now())
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    SubmissionEntity secondSubmissionEntity =
        SubmissionEntity.builder()
            .id(UUID.fromString("423a6abf-f5dc-4908-9b7a-fe2607ae9c3d"))
            .providerOfficeId(UUID.randomUUID())
            .providerUserId(UUID.fromString("fcb6e669-a17e-4894-8bed-572d7357ba91"))
            .scheduleId("Schedule ID")
            .submissionDate(LocalDateTime.now())
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    Submission firstSubmission =
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
            .build();

    Submission secondSubmission =
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
            .build();

    when(mockSubmissionRepository.findByProviderUserId(any(UUID.class)))
        .thenReturn(List.of(firstSubmissionEntity, secondSubmissionEntity));
    when(mockSubmissionRepository.findSubmissionTotalById(firstSubmission.getId()))
        .thenReturn(new BigDecimal(10));
    when(mockSubmissionRepository.findSubmissionTotalById(secondSubmission.getId()))
        .thenReturn(new BigDecimal(20));
    when(mockSubmissionMapper.toSubmission(firstSubmissionEntity)).thenReturn(firstSubmission);
    when(mockSubmissionMapper.toSubmission(secondSubmissionEntity)).thenReturn(secondSubmission);

    List<Submission> result = claimService.getAllSubmissionsForProvider(UUID.randomUUID(), true);

    assertThat(result).hasSize(2).contains(firstSubmission, secondSubmission);
    assertThat(result.get(0).getTotalClaimed()).isEqualTo(new BigDecimal(10));
    assertThat(result.get(1).getTotalClaimed()).isEqualTo(new BigDecimal(20));
  }

  @Test
  void shouldGetSubmissionById() {
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    UUID providerUserId = UUID.randomUUID();
    UUID providerOfficeId = providerUserId;
    LocalDateTime submissionDate = LocalDateTime.now();
    LocalDateTime submissionPeriodStartDate = LocalDateTime.now();
    LocalDateTime submissionPeriodEndDate = LocalDateTime.now();
    SubmissionEntity submissionEntity =
        SubmissionEntity.builder()
            .id(submissionId)
            .providerOfficeId(providerOfficeId)
            .providerUserId(providerUserId)
            .scheduleId("Schedule ID")
            .submissionDate(submissionDate)
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(submissionPeriodEndDate)
            .build();

    Submission submission =
        Submission.builder()
            .id(submissionId)
            .providerOfficeId(providerOfficeId)
            .providerUserId(providerUserId)
            .scheduleId("Schedule ID")
            .submissionDate(submissionDate)
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(submissionPeriodEndDate)
            .build();
    when(mockSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submissionEntity));
    when(mockSubmissionMapper.toSubmission(submissionEntity)).thenReturn(submission);
    Submission result = claimService.getSubmission(submissionId, false);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(submissionId);
    assertThat(result.getProviderOfficeId()).isEqualTo(submissionEntity.getProviderOfficeId());
    assertThat(result.getProviderUserId()).isEqualTo(submissionEntity.getProviderUserId());
    assertThat(result.getScheduleId()).isEqualTo(submissionEntity.getScheduleId());
    assertThat(result.getSubmissionDate()).isEqualTo(submissionEntity.getSubmissionDate());
    assertThat(result.getSubmissionTypeCode()).isEqualTo(submissionEntity.getSubmissionTypeCode());
    assertThat(result.getSubmissionPeriodStartDate())
        .isEqualTo(submissionEntity.getSubmissionPeriodStartDate());
    assertThat(result.getSubmissionPeriodEndDate())
        .isEqualTo(submissionEntity.getSubmissionPeriodEndDate());
  }

  @Test
  void shouldGetSubmissionByIdWithClaimTotals() {
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    UUID providerUserId = UUID.randomUUID();
    UUID providerOfficeId = providerUserId;
    LocalDateTime submissionDate = LocalDateTime.now();
    LocalDateTime submissionPeriodStartDate = LocalDateTime.now();
    LocalDateTime submissionPeriodEndDate = LocalDateTime.now();
    SubmissionEntity submissionEntity =
        SubmissionEntity.builder()
            .id(submissionId)
            .providerOfficeId(providerOfficeId)
            .providerUserId(providerUserId)
            .scheduleId("Schedule ID")
            .submissionDate(submissionDate)
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(submissionPeriodEndDate)
            .build();

    Submission submission =
        Submission.builder()
            .id(submissionId)
            .providerOfficeId(providerOfficeId)
            .providerUserId(providerUserId)
            .scheduleId("Schedule ID")
            .submissionDate(submissionDate)
            .submissionTypeCode("Type Code")
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(submissionPeriodEndDate)
            .build();
    when(mockSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submissionEntity));
    when(mockSubmissionRepository.findSubmissionTotalById(submissionId))
        .thenReturn(new BigDecimal(10));
    when(mockSubmissionMapper.toSubmission(submissionEntity)).thenReturn(submission);
    Submission result = claimService.getSubmission(submissionId, true);
    assertThat(result.getTotalClaimed()).isEqualTo(new BigDecimal(10));
  }

  @Test
  void shouldGetAllClaims() {

    UUID submissionId = UUID.randomUUID();

    ClaimEntity firstClaimEntity =
        ClaimEntity.builder()
            .id(1L)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal(1000.0))
            .build();

    ClaimEntity secondClaimEntity =
        ClaimEntity.builder()
            .id(2L)
            .ufn("UFN456")
            .client("Jane Smith")
            .category("Category B")
            .concluded(LocalDate.of(2025, 7, 2))
            .feeType("Hourly")
            .claimed(new BigDecimal(2000.0))
            .build();

    Claim firstClaim =
        Claim.builder()
            .id(1L)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal(1000.0))
            .build();

    Claim secondClaim =
        Claim.builder()
            .id(2L)
            .ufn("UFN456")
            .client("Jane Smith")
            .category("Category B")
            .concluded(LocalDate.of(2025, 7, 2))
            .feeType("Hourly")
            .claimed(new BigDecimal(2000.0))
            .build();

    when(mockClaimRepository.findBySubmissionId(submissionId))
        .thenReturn(List.of(firstClaimEntity, secondClaimEntity));
    when(mockClaimMapper.toClaim(firstClaimEntity)).thenReturn(firstClaim);
    when(mockClaimMapper.toClaim(secondClaimEntity)).thenReturn(secondClaim);

    List<Claim> result = claimService.getClaims(submissionId);

    assertThat(result).hasSize(2).contains(firstClaim, secondClaim);
  }

  @Test
  void shouldGetClaimById() {
    UUID submissionId = UUID.randomUUID();
    Long id = 1L;
    ClaimEntity claimEntity =
        ClaimEntity.builder()
            .id(id)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal(1000.0))
            .build();

    Claim claim =
        Claim.builder()
            .id(id)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal(1000.0))
            .build();

    when(mockClaimRepository.findById(id)).thenReturn(Optional.of(claimEntity));
    when(mockClaimMapper.toClaim(claimEntity)).thenReturn(claim);

    Claim result = claimService.getClaim(submissionId, id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(new BigDecimal(1000.0));
  }

  @Test
  void shouldNotGetClaimById_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    UUID submissionId = UUID.randomUUID();
    when(mockClaimRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ClaimNotFoundException.class, () -> claimService.getClaim(submissionId, id));

    verify(mockClaimMapper, never()).toClaim(any(ClaimEntity.class));
  }

  @Test
  void shouldCreateClaim() {
    UUID submissionId = UUID.randomUUID();
    when(mockSubmissionRepository.findById(submissionId))
        .thenReturn(Optional.of(new SubmissionEntity()));
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN789")
            .client("Alice Example")
            .category("Category C")
            .concluded(LocalDate.of(2025, 7, 3))
            .feeType("Capped")
            .claimed(new BigDecimal(1500.0))
            .build();

    ClaimEntity savedClaimEntity =
        ClaimEntity.builder()
            .id(3L)
            .ufn("UFN789")
            .client("Alice Example")
            .category("Category C")
            .concluded(LocalDate.of(2025, 7, 3))
            .feeType("Capped")
            .claimed(new BigDecimal(1500.0))
            .build();

    when(mockClaimRepository.save(any(ClaimEntity.class))).thenReturn(savedClaimEntity);

    Long result = claimService.createClaim(submissionId, claimRequestBody);

    assertThat(result).isNotNull().isEqualTo(3L);
  }

  /*
   * Tests the creation of a submission.
   *
   * This test verifies that a submission can be created with the provided request body and that
   * the correct ID is returned.
   */
  @Test
  void shouldCreateSubmission() {
    SubmissionRequestBody submissionRequestBody =
        SubmissionRequestBody.builder()
            .providerUserId(UUID.randomUUID())
            .scheduleId("Schedule ID")
            .providerOfficeId(UUID.randomUUID())
            .submissionTypeCode("Type Code")
            .submissionDate(LocalDateTime.now())
            .submissionPeriodStartDate(LocalDateTime.now())
            .submissionPeriodEndDate(LocalDateTime.now())
            .build();

    SubmissionEntity savedSubmissionEntity =
        SubmissionEntity.builder()
            .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            .providerUserId(submissionRequestBody.getProviderUserId())
            .scheduleId(submissionRequestBody.getScheduleId())
            .providerOfficeId(submissionRequestBody.getProviderOfficeId())
            .submissionTypeCode(submissionRequestBody.getSubmissionTypeCode())
            .submissionDate(submissionRequestBody.getSubmissionDate())
            .submissionPeriodStartDate(submissionRequestBody.getSubmissionPeriodStartDate())
            .submissionPeriodEndDate(submissionRequestBody.getSubmissionPeriodEndDate())
            .build();

    when(mockSubmissionRepository.save(any(SubmissionEntity.class)))
        .thenReturn(savedSubmissionEntity);

    UUID result = claimService.createSubmission(submissionRequestBody);

    assertThat(result).isNotNull().isEqualTo(savedSubmissionEntity.getId());
  }

  @Test
  void shouldUpdateClaim() {
    Long id = 1L;
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN999")
            .client("Updated Client")
            .category("Updated Category")
            .concluded(LocalDate.of(2025, 7, 4))
            .feeType("Revised")
            .claimed(new BigDecimal(2500.0))
            .build();

    ClaimEntity claimEntity =
        ClaimEntity.builder()
            .id(id)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal(1000.0))
            .build();

    UUID submissionId = UUID.randomUUID();
    when(mockSubmissionRepository.findById(submissionId))
        .thenReturn(Optional.of(new SubmissionEntity()));

    when(mockClaimRepository.findById(id)).thenReturn(Optional.of(claimEntity));

    claimService.updateClaim(submissionId, id, claimRequestBody);

    assertThat(claimEntity.getUfn()).isEqualTo("UFN999");
    assertThat(claimEntity.getClient()).isEqualTo("Updated Client");
    assertThat(claimEntity.getCategory()).isEqualTo("Updated Category");
    assertThat(claimEntity.getConcluded()).isEqualTo(LocalDate.of(2025, 7, 4));
    assertThat(claimEntity.getFeeType()).isEqualTo("Revised");
    assertThat(claimEntity.getClaimed()).isEqualTo(new BigDecimal(2500.0));

    verify(mockClaimRepository).save(claimEntity);
  }

  @Test
  void shouldNotUpdateClaim_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    UUID submissionId = UUID.randomUUID();
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder().ufn("UFN000").client("Non-existent Client").build();

    when(mockClaimRepository.findById(id)).thenReturn(Optional.empty());
    when(mockSubmissionRepository.findById(submissionId))
        .thenReturn(Optional.of(new SubmissionEntity()));

    assertThrows(
        ClaimNotFoundException.class,
        () -> claimService.updateClaim(submissionId, id, claimRequestBody));

    verify(mockClaimRepository, never()).save(any(ClaimEntity.class));
  }

  @Test
  void shouldDeleteClaim() {
    Long id = 1L;
    ClaimEntity claimEntity = ClaimEntity.builder().id(id).ufn("UFN123").client("John Doe").build();
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

    when(mockClaimRepository.findById(id)).thenReturn(Optional.of(claimEntity));
    when(mockSubmissionRepository.findById(submissionId))
        .thenReturn(Optional.of(new SubmissionEntity()));

    claimService.deleteClaim(submissionId, id);

    verify(mockClaimRepository).deleteById(id);
  }

  /*
   * Should delete a submission when it exists.
   */
  @Test
  void shouldDeleteSubmission() {
    UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    SubmissionEntity submissionEntity =
        SubmissionEntity.builder().id(id).providerUserId(UUID.randomUUID()).build();

    when(mockSubmissionRepository.findById(id)).thenReturn(Optional.of(submissionEntity));

    claimService.deleteSubmission(id);

    verify(mockSubmissionRepository).deleteById(id);
  }

  /** Should not delete a claim when it does not exist. */
  @Test
  void shouldNotDeleteClaim_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    when(mockClaimRepository.findById(id)).thenReturn(Optional.empty());
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    when(mockSubmissionRepository.findById(submissionId))
        .thenReturn(Optional.of(new SubmissionEntity()));

    assertThrows(ClaimNotFoundException.class, () -> claimService.deleteClaim(submissionId, id));

    verify(mockClaimRepository, never()).deleteById(id);
  }

  /** Should not delete a submission when it does not exist. */
  @Test
  void shouldNotDeleteSubmission_whenSubmissionNotFoundThenThrowsException() {
    UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    when(mockSubmissionRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(SubmissionNotFoundException.class, () -> claimService.deleteSubmission(id));
    verify(mockSubmissionRepository, never()).deleteById(id);
  }

  /** Should update a submission when it exists. */
  @Test
  void shouldUpdateSubmission() {
    UUID submissionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    UUID providerUserId = UUID.randomUUID();
    UUID providerOfficeId = providerUserId;
    LocalDateTime submissionDate = LocalDateTime.now();
    LocalDateTime submissionPeriodStartDate = LocalDateTime.now();
    LocalDateTime submissionPeriodEndDate = LocalDateTime.now();
    SubmissionRequestBody submissionRequestBody =
        SubmissionRequestBody.builder()
            .providerUserId(providerUserId)
            .scheduleId("Updated Schedule ID")
            .providerOfficeId(providerOfficeId)
            .submissionTypeCode("Updated Type Code")
            .submissionDate(submissionDate)
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(submissionPeriodEndDate)
            .build();
    SubmissionEntity submissionEntity =
        SubmissionEntity.builder()
            .id(submissionId)
            .providerUserId(providerUserId)
            .scheduleId("Old Schedule ID")
            .providerOfficeId(providerOfficeId)
            .submissionTypeCode("Old Type Code")
            .submissionDate(submissionDate)
            .submissionPeriodStartDate(submissionPeriodStartDate)
            .submissionPeriodEndDate(submissionPeriodEndDate)
            .build();
    when(mockSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submissionEntity));
    claimService.updateSubmission(submissionId, submissionRequestBody);
    assertThat(submissionEntity.getProviderUserId())
        .isEqualTo(submissionRequestBody.getProviderUserId());
    assertThat(submissionEntity.getScheduleId()).isEqualTo(submissionRequestBody.getScheduleId());
    assertThat(submissionEntity.getProviderOfficeId())
        .isEqualTo(submissionRequestBody.getProviderOfficeId());
    assertThat(submissionEntity.getSubmissionTypeCode())
        .isEqualTo(submissionRequestBody.getSubmissionTypeCode());
    assertThat(submissionEntity.getSubmissionDate())
        .isEqualTo(submissionRequestBody.getSubmissionDate());
    assertThat(submissionEntity.getSubmissionPeriodStartDate())
        .isEqualTo(submissionRequestBody.getSubmissionPeriodStartDate());
    assertThat(submissionEntity.getSubmissionPeriodEndDate())
        .isEqualTo(submissionRequestBody.getSubmissionPeriodEndDate());
    verify(mockSubmissionRepository).save(submissionEntity);
  }
}
