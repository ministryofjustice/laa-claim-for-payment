package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.exception.ClaimNotFoundException;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimMapper;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.repository.ClaimRepository;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

  @Mock
  private ClaimRepository mockClaimRepository;

  @Mock
  private ClaimMapper mockClaimMapper;

  @InjectMocks
  private ClaimService claimService;

  @Test
  void shouldGetAllClaims() {
    ClaimEntity firstClaimEntity = ClaimEntity.builder()
        .id(1L)
        .ufn("UFN123")
        .client("John Doe")
        .category("Category A")
        .concluded(LocalDate.of(2025, 7, 1))
        .feeType("Fixed")
        .claimed(1000.0)
        .build();

    ClaimEntity secondClaimEntity = ClaimEntity.builder()
        .id(2L)
        .ufn("UFN456")
        .client("Jane Smith")
        .category("Category B")
        .concluded(LocalDate.of(2025, 7, 2))
        .feeType("Hourly")
        .claimed(2000.0)
        .build();

    Claim firstClaim = Claim.builder()
        .id(1L)
        .ufn("UFN123")
        .client("John Doe")
        .category("Category A")
        .concluded(LocalDate.of(2025, 7, 1))
        .feeType("Fixed")
        .claimed(1000.0)
        .build();

    Claim secondClaim = Claim.builder()
        .id(2L)
        .ufn("UFN456")
        .client("Jane Smith")
        .category("Category B")
        .concluded(LocalDate.of(2025, 7, 2))
        .feeType("Hourly")
        .claimed(2000.0)
        .build();

    when(mockClaimRepository.findAll()).thenReturn(List.of(firstClaimEntity, secondClaimEntity));
    when(mockClaimMapper.toClaim(firstClaimEntity)).thenReturn(firstClaim);
    when(mockClaimMapper.toClaim(secondClaimEntity)).thenReturn(secondClaim);

    List<Claim> result = claimService.getAllClaims();

    assertThat(result).hasSize(2).contains(firstClaim, secondClaim);
  }

  @Test
  void shouldGetClaimById() {
    Long id = 1L;
    ClaimEntity claimEntity = ClaimEntity.builder()
        .id(id)
        .ufn("UFN123")
        .client("John Doe")
        .category("Category A")
        .concluded(LocalDate.of(2025, 7, 1))
        .feeType("Fixed")
        .claimed(1000.0)
        .build();

    Claim claim = Claim.builder()
        .id(id)
        .ufn("UFN123")
        .client("John Doe")
        .category("Category A")
        .concluded(LocalDate.of(2025, 7, 1))
        .feeType("Fixed")
        .claimed(1000.0)
        .build();

    when(mockClaimRepository.findById(id)).thenReturn(Optional.of(claimEntity));
    when(mockClaimMapper.toClaim(claimEntity)).thenReturn(claim);

    Claim result = claimService.getClaim(id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(1000.0);
  }

  @Test
  void shouldNotGetClaimById_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    when(mockClaimRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ClaimNotFoundException.class, () -> claimService.getClaim(id));

    verify(mockClaimMapper, never()).toClaim(any(ClaimEntity.class));
  }

  @Test
  void shouldCreateClaim() {
    ClaimRequestBody claimRequestBody = ClaimRequestBody.builder()
        .ufn("UFN789")
        .client("Alice Example")
        .category("Category C")
        .concluded(LocalDate.of(2025, 7, 3))
        .feeType("Capped")
        .claimed(1500.0)
        .build();

    ClaimEntity savedClaimEntity = ClaimEntity.builder()
        .id(3L)
        .ufn("UFN789")
        .client("Alice Example")
        .category("Category C")
        .concluded(LocalDate.of(2025, 7, 3))
        .feeType("Capped")
        .claimed(1500.0)
        .build();

    when(mockClaimRepository.save(any(ClaimEntity.class))).thenReturn(savedClaimEntity);

    Long result = claimService.createClaim(claimRequestBody);

    assertThat(result).isNotNull().isEqualTo(3L);
  }

  @Test
  void shouldUpdateClaim() {
    Long id = 1L;
    ClaimRequestBody claimRequestBody = ClaimRequestBody.builder()
        .ufn("UFN999")
        .client("Updated Client")
        .category("Updated Category")
        .concluded(LocalDate.of(2025, 7, 4))
        .feeType("Revised")
        .claimed(2500.0)
        .build();

    ClaimEntity claimEntity = ClaimEntity.builder()
        .id(id)
        .ufn("UFN123")
        .client("John Doe")
        .category("Category A")
        .concluded(LocalDate.of(2025, 7, 1))
        .feeType("Fixed")
        .claimed(1000.0)
        .build();

    when(mockClaimRepository.findById(id)).thenReturn(Optional.of(claimEntity));

    claimService.updateClaim(id, claimRequestBody);

    assertThat(claimEntity.getUfn()).isEqualTo("UFN999");
    assertThat(claimEntity.getClient()).isEqualTo("Updated Client");
    assertThat(claimEntity.getCategory()).isEqualTo("Updated Category");
    assertThat(claimEntity.getConcluded()).isEqualTo(LocalDate.of(2025, 7, 4));
    assertThat(claimEntity.getFeeType()).isEqualTo("Revised");
    assertThat(claimEntity.getClaimed()).isEqualTo(2500.0);

    verify(mockClaimRepository).save(claimEntity);
  }

  @Test
  void shouldNotUpdateClaim_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    ClaimRequestBody claimRequestBody = ClaimRequestBody.builder()
        .ufn("UFN000")
        .client("Non-existent Client")
        .build();

    when(mockClaimRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ClaimNotFoundException.class, () -> claimService.updateClaim(id, claimRequestBody));

    verify(mockClaimRepository, never()).save(any(ClaimEntity.class));
  }

  @Test
  void shouldDeleteClaim() {
    Long id = 1L;
    ClaimEntity claimEntity = ClaimEntity.builder()
        .id(id)
        .ufn("UFN123")
        .client("John Doe")
        .build();

    when(mockClaimRepository.findById(id)).thenReturn(Optional.of(claimEntity));

    claimService.deleteClaim(id);

    verify(mockClaimRepository).deleteById(id);
  }

  @Test
  void shouldNotDeleteClaim_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    when(mockClaimRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ClaimNotFoundException.class, () -> claimService.deleteClaim(id));

    verify(mockClaimRepository, never()).deleteById(id);
  }
}
