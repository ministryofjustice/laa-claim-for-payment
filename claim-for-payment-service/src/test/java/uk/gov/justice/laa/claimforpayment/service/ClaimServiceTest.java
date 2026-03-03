package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateClaimResponse;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamForbiddenException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamUnauthorisedException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamValidationException;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapper;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimPageMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimPageMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimRequestBodyMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimRequestBodyMapperImpl;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

  @Mock private CivilClaimsApi mockCivilClaimsApi;

  @Spy private CivilClaimMapper mockClaimMapper = new CivilClaimMapperImpl();

  @Spy private ClaimRequestBodyMapper mockClaimRequestBodyMapper = new ClaimRequestBodyMapperImpl();

  @Spy private ClaimPageMapper mockClaimPageMapper = new ClaimPageMapperImpl();

  @InjectMocks private ClaimService claimService;

  // Private "constructor" helper to create CivilClaim test data consistently
  private CivilClaim civilClaim(
      Long id,
      String ufn,
      String client,
      String category,
      LocalDate concluded,
      String feeType,
      BigDecimal claimed,
      UUID providerUserId) {

    CivilClaim claim = new CivilClaim();
    claim.setId(id);
    claim.setUfn(ufn);
    claim.setClient(client);
    claim.setCategory(category);
    claim.setConcluded(concluded);
    claim.setFeeType(feeType);
    claim.setClaimed(claimed);
    claim.setProviderUserId(providerUserId);
    return claim;
  }

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(mockClaimPageMapper, "civilClaimMapper", mockClaimMapper);
  }

  @Test
  void shouldGetAllClaimsForProviderUser() {
    UUID providerUserId = UUID.randomUUID();

    CivilClaim firstCivilClaim =
        civilClaim(
            1L,
            "UFN123",
            "John Doe",
            "Category A",
            LocalDate.of(2025, 7, 1),
            "Fixed",
            new BigDecimal("1000.00"),
            providerUserId);

    CivilClaim secondCivilClaim =
        civilClaim(
            2L,
            "UFN456",
            "Jane Smith",
            "Category B",
            LocalDate.of(2025, 7, 2),
            "Hourly",
            new BigDecimal("2000.00"),
            providerUserId);

    Claim firstClaim =
        Claim.builder()
            .id(1L)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal("1000.00"))
            .providerUserId(providerUserId)
            .build();

    Claim secondClaim =
        Claim.builder()
            .id(2L)
            .ufn("UFN456")
            .client("Jane Smith")
            .category("Category B")
            .concluded(LocalDate.of(2025, 7, 2))
            .feeType("Hourly")
            .claimed(new BigDecimal("2000.00"))
            .providerUserId(providerUserId)
            .build();

    CivilClaimPageResponse pageResponse = new CivilClaimPageResponse();
    pageResponse.setClaims(List.of(firstCivilClaim, secondCivilClaim));
    when(mockCivilClaimsApi.getClaims(any(), any())).thenReturn(pageResponse);
    when(mockClaimMapper.toClaim(firstCivilClaim)).thenReturn(firstClaim);
    when(mockClaimMapper.toClaim(secondCivilClaim)).thenReturn(secondClaim);

    ClaimPage result = claimService.getClaims(0, 10);

    assertThat(result.claims()).hasSize(2).contains(firstClaim, secondClaim);
  }

  @Test
  void shouldGetClaimById() {
    Long id = 1L;

    CivilClaim civilClaim =
        civilClaim(
            id,
            "UFN123",
            "John Doe",
            "Category A",
            LocalDate.of(2025, 7, 1),
            "Fixed",
            new BigDecimal("1000.00"),
            UUID.randomUUID());

    Claim claim =
        Claim.builder()
            .id(id)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .claimed(new BigDecimal("1000.00"))
            .build();

    when(mockCivilClaimsApi.getClaim(id)).thenReturn(civilClaim);

    Claim result = claimService.getClaim(id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(new BigDecimal("1000.00"));
  }

  @Test
  void shouldNotGetClaimById_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    when(mockCivilClaimsApi.getClaim(id))
        .thenThrow(
            HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

    assertThrows(ResourceNotFoundException.class, () -> claimService.getClaim(id));

    verify(mockClaimMapper, never()).toClaim(any(CivilClaim.class));
  }

  @Test
  void shouldCreateClaim() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN789")
            .client("Alice Example")
            .category("Category C")
            .concluded(LocalDate.of(2025, 7, 3))
            .feeType("Capped")
            .claimed(new BigDecimal("1500.00"))
            .build();

    CivilClaim savedCivilClaim =
        civilClaim(
            3L,
            "UFN789",
            "Alice Example",
            "Category C",
            LocalDate.of(2025, 7, 3),
            "Capped",
            new BigDecimal("1500.00"),
            UUID.randomUUID());

    when(mockCivilClaimsApi.createClaim(any(CivilClaimRequestBody.class)))
        .thenReturn(new CivilCreateClaimResponse().id(3L));

    Long result = claimService.createClaim(claimRequestBody, UUID.randomUUID());

    assertThat(result).isNotNull().isEqualTo(3L);
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
            .claimed(new BigDecimal("2500.00"))
            .build();

    CivilClaimRequestBody civilClaimRequestBody =
        new CivilClaimRequestBody()
            .ufn("UFN999")
            .client("Updated Client")
            .category("Updated Category")
            .concluded(LocalDate.of(2025, 7, 4))
            .feeType("Revised")
            .claimed(new BigDecimal("2500.00"));

    UUID providerUserId = UUID.randomUUID();

    CivilClaim civilClaim =
        civilClaim(
            id,
            "UFN123",
            "John Doe",
            "Category A",
            LocalDate.of(2025, 7, 1),
            "Fixed",
            new BigDecimal("1000.00"),
            providerUserId);

    claimService.updateClaim(id, claimRequestBody);

    verify(mockCivilClaimsApi).updateClaim(id, civilClaimRequestBody);
  }

  @Test
  void shouldNotUpdateClaim_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder().ufn("UFN000").client("Non-existent Client").build();

    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .when(mockCivilClaimsApi)
        .updateClaim(any(Long.class), any(CivilClaimRequestBody.class));

    assertThrows(
        ResourceNotFoundException.class, () -> claimService.updateClaim(id, claimRequestBody));
  }

  @Test
  void shouldDeleteClaim() {
    Long id = 1L;

    claimService.deleteClaim(id);

    verify(mockCivilClaimsApi).deleteClaim(id);
    ;
  }

  /** Should not delete a claim when it does not exist. */
  @Test
  void shouldNotDeleteClaim_whenClaimNotFoundThenThrowsException() {
    Long id = 5L;
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .when(mockCivilClaimsApi)
        .deleteClaim(id);

    assertThrows(ResourceNotFoundException.class, () -> claimService.deleteClaim(id));
  }

  @Test
  void shouldThrowExceptionWhenForbidden() {
    Long id = 1L;
    doThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN))
        .when(mockCivilClaimsApi)
        .getClaim(id);

    assertThrows(UpstreamForbiddenException.class, () -> claimService.getClaim(id));
  }

  @Test
  void shouldThrowExceptionWhenNotAuthorized() {
    Long id = 1L;
    doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
        .when(mockCivilClaimsApi)
        .getClaim(id);

    assertThrows(UpstreamUnauthorisedException.class, () -> claimService.getClaim(id));
  } 

    @Test
  void shouldThrowExceptionWhenFailsValidation() {
    Long id = 1L;
    doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
        .when(mockCivilClaimsApi)
        .getClaim(id);

    assertThrows(UpstreamValidationException.class, () -> claimService.getClaim(id));
  } 
}
