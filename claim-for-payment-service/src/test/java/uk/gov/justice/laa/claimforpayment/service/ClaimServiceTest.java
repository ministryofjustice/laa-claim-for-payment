package uk.gov.justice.laa.claimforpayment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilAddClaimEvidenceResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidenceRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateClaimResponse;
import uk.gov.justice.laa.claimforpayment.exception.ResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamForbiddenException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamUnauthorisedException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamValidationException;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapper;
import uk.gov.justice.laa.claimforpayment.mapper.CivilClaimMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimEvidenceRequestBodyMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimEvidenceRequestBodyMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimPageMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimPageMapperImpl;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimRequestBodyMapper;
import uk.gov.justice.laa.claimforpayment.mapper.ClaimRequestBodyMapperImpl;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

  @Mock private CivilClaimsApi mockCivilClaimsApi;

  @Spy private CivilClaimMapper mockClaimMapper = new CivilClaimMapperImpl();

  @Spy private ClaimRequestBodyMapper mockClaimRequestBodyMapper = new ClaimRequestBodyMapperImpl();

  @Spy private ClaimEvidenceRequestBodyMapper mockClaimEvidenceRequestBodyMapper = new ClaimEvidenceRequestBodyMapperImpl();

  @Spy private ClaimPageMapper mockClaimPageMapper = new ClaimPageMapperImpl();

  @InjectMocks private ClaimService claimService;

  private static final UUID CLAIM_1_ID = UUID.randomUUID();
  private static final UUID CLAIM_2_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_1_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_2_ID = UUID.randomUUID();
  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();

  // Private "constructor" helper to create CivilClaim test data consistently
  private CivilClaim civilClaim(
      UUID id,
      String ufn,
      String client,
      String category,
      LocalDate concluded,
      String feeType,
      Boolean escaped,
      String counselPayment,
      BigDecimal claimed,
      UUID providerUserId) {

    CivilClaim claim = new CivilClaim();
    claim.setId(id);
    claim.setUfn(ufn);
    claim.setClient(client);
    claim.setCategory(category);
    claim.setConcluded(concluded);
    claim.setFeeType(feeType);
    claim.setEscaped(escaped);
    claim.setCounselPayment(counselPayment);
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
    CivilClaim firstCivilClaim =
        civilClaim(
            CLAIM_1_ID,
            "UFN123",
            "John Doe",
            "Category A",
            LocalDate.of(2025, 7, 1),
            "Fixed",
            false,
            "Paid and Reconciled",
            new BigDecimal("1000.00"),
            PROVIDER_USER_ID);

    CivilClaim secondCivilClaim =
        civilClaim(
            CLAIM_2_ID,
            "UFN456",
            "Jane Smith",
            "Category B",
            LocalDate.of(2025, 7, 2),
            "Hourly",
            false,
            "Paid and Reconciled",
            new BigDecimal("2000.00"),
            PROVIDER_USER_ID);

    Claim firstClaim =
        Claim.builder()
            .id(CLAIM_1_ID)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("1000.00"))
            .providerUserId(PROVIDER_USER_ID)
            .build();

    Claim secondClaim =
        Claim.builder()
            .id(CLAIM_2_ID)
            .ufn("UFN456")
            .client("Jane Smith")
            .category("Category B")
            .concluded(LocalDate.of(2025, 7, 2))
            .feeType("Hourly")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("2000.00"))
            .providerUserId(PROVIDER_USER_ID)
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
    CivilClaim civilClaim =
        civilClaim(
            CLAIM_1_ID,
            "UFN123",
            "John Doe",
            "Category A",
            LocalDate.of(2025, 7, 1),
            "Fixed",
            false,
            "Paid and Reconciled",
            new BigDecimal("1000.00"),
            UUID.randomUUID());

    Claim claim =
        Claim.builder()
            .id(CLAIM_2_ID)
            .ufn("UFN123")
            .client("John Doe")
            .category("Category A")
            .concluded(LocalDate.of(2025, 7, 1))
            .feeType("Fixed")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("1000.00"))
            .build();

    when(mockCivilClaimsApi.getClaim(CLAIM_1_ID)).thenReturn(civilClaim);

    Claim result = claimService.getClaim(CLAIM_1_ID);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(CLAIM_1_ID);
    assertThat(result.getClient()).isEqualTo("John Doe");
    assertThat(result.getClaimed()).isEqualTo(new BigDecimal("1000.00"));
  }

  @Test
  void shouldNotGetClaimById_whenClaimNotFoundThenThrowsException() {
    when(mockCivilClaimsApi.getClaim(CLAIM_1_ID))
        .thenThrow(
            HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

    assertThrows(ResourceNotFoundException.class, () -> claimService.getClaim(CLAIM_1_ID));

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
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("1500.00"))
            .build();

    when(mockCivilClaimsApi.createClaim(any(CivilClaimRequestBody.class)))
        .thenReturn(new CivilCreateClaimResponse().id(CLAIM_1_ID));

    UUID result = claimService.createClaim(claimRequestBody, CLAIM_1_ID);

    assertThat(result).isNotNull().isEqualTo(CLAIM_1_ID);

    ArgumentCaptor<CivilClaimRequestBody> captor =
        ArgumentCaptor.forClass(CivilClaimRequestBody.class);

    verify(mockCivilClaimsApi).createClaim(captor.capture());

    var body = captor.getValue();

    assertThat(body.getId()).isNotNull();
    assertThat(body.getUfn()).isEqualTo(claimRequestBody.getUfn());
  }

  @Test
  void shouldUpdateClaim() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder()
            .ufn("UFN999")
            .client("Updated Client")
            .category("Updated Category")
            .concluded(LocalDate.of(2025, 7, 4))
            .feeType("Revised")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("2500.00"))
            .build();

    CivilClaimRequestBody civilClaimRequestBody =
        new CivilClaimRequestBody()
            .ufn("UFN999")
            .client("Updated Client")
            .category("Updated Category")
            .concluded(LocalDate.of(2025, 7, 4))
            .feeType("Revised")
            .escaped(false)
            .counselPayment("Paid and Reconciled")
            .claimed(new BigDecimal("2500.00"));

    claimService.updateClaim(CLAIM_1_ID, claimRequestBody, PROVIDER_USER_ID);

    verify(mockCivilClaimsApi).updateClaim(CLAIM_1_ID, civilClaimRequestBody);
  }

  @Test
  void shouldNotUpdateClaim_whenClaimNotFoundThenThrowsException() {
    ClaimRequestBody claimRequestBody =
        ClaimRequestBody.builder().ufn("UFN000").client("Non-existent Client").build();

    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .when(mockCivilClaimsApi)
        .updateClaim(any(UUID.class), any(CivilClaimRequestBody.class));

    assertThrows(
        ResourceNotFoundException.class, () -> claimService.updateClaim(CLAIM_1_ID, claimRequestBody, PROVIDER_USER_ID));
  }

  @Test
  void shouldDeleteClaim() {
    claimService.deleteClaim(CLAIM_1_ID);

    verify(mockCivilClaimsApi).deleteClaim(CLAIM_1_ID);
  }

  /** Should not delete a claim when it does not exist. */
  @Test
  void shouldNotDeleteClaim_whenClaimNotFoundThenThrowsException() {
    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .when(mockCivilClaimsApi)
        .deleteClaim(CLAIM_1_ID);

    assertThrows(ResourceNotFoundException.class, () -> claimService.deleteClaim(CLAIM_1_ID));
  }

  @Test
  void shouldThrowExceptionWhenForbidden() {
    doThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN))
        .when(mockCivilClaimsApi)
        .getClaim(CLAIM_1_ID);

    assertThrows(UpstreamForbiddenException.class, () -> claimService.getClaim(CLAIM_1_ID));
  }

  @Test
  void shouldThrowExceptionWhenNotAuthorized() {
    doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
        .when(mockCivilClaimsApi)
        .getClaim(CLAIM_1_ID);

    assertThrows(UpstreamUnauthorisedException.class, () -> claimService.getClaim(CLAIM_1_ID));
  }

  @Test
  void shouldThrowExceptionWhenFailsValidation() {
    doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
        .when(mockCivilClaimsApi)
        .getClaim(CLAIM_1_ID);

    assertThrows(UpstreamValidationException.class, () -> claimService.getClaim(CLAIM_1_ID));
  }

  @Test
  void shouldReturnIdWhenEvidenceAddedToClaim() {
    String fileName = "file_name.pdf";
    Long fileSize = 1000L;

    CivilAddClaimEvidenceResponse addClaimEvidenceResponse =
        new CivilAddClaimEvidenceResponse().id(EVIDENCE_1_ID);

    when(mockCivilClaimsApi.addEvidenceToClaim(
            any(UUID.class), any(CivilClaimEvidenceRequestBody.class)))
        .thenReturn(addClaimEvidenceResponse);

    var uploadFile = new UploadFile(fileName, fileSize);

    UUID result = claimService.addEvidenceToClaim(CLAIM_1_ID, uploadFile);

    assertThat(result).isNotNull().isEqualTo(EVIDENCE_1_ID);

    ArgumentCaptor<CivilClaimEvidenceRequestBody> captor =
        ArgumentCaptor.forClass(CivilClaimEvidenceRequestBody.class);

    verify(mockCivilClaimsApi).addEvidenceToClaim(eq(CLAIM_1_ID), captor.capture());

    var body = captor.getValue();

    assertThat(body.getId()).isNotNull();
    assertThat(body.getFileKey()).isEqualTo(fileName);
    assertThat(body.getFileSize()).isEqualTo(fileSize);
  }

  @Test
  void shouldDeleteEvidenceFromClaim() {
    claimService.deleteEvidenceFromClaim(CLAIM_1_ID, EVIDENCE_1_ID);

    verify(mockCivilClaimsApi).deleteEvidenceFromClaim(CLAIM_1_ID, EVIDENCE_1_ID);
  }

  @Test
  void shouldLinkEvidenceToLineItem() {
    claimService.linkEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID));
    verify(mockCivilClaimsApi).addEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID));
  }

  @Test
  void shouldLinkMultipleEvidenceToLineItem() {
    claimService.linkEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID, EVIDENCE_2_ID));
    verify(mockCivilClaimsApi).addEvidenceToLineItem(CLAIM_1_ID, LINE_ITEM_ID, List.of(EVIDENCE_1_ID, EVIDENCE_2_ID));
  }

  @Test
  void shouldUnlinkEvidenceFromLineItem() {
    claimService.unlinkEvidenceFromLineItem(CLAIM_1_ID, LINE_ITEM_ID, EVIDENCE_1_ID);

    verify(mockCivilClaimsApi).unlinkEvidenceFromLineItem(CLAIM_1_ID, LINE_ITEM_ID, EVIDENCE_1_ID);
  }
}
