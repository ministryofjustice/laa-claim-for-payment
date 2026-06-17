package uk.gov.justice.laa.claimforpayment.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidence;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilLineItem;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimEvidence;
import uk.gov.justice.laa.claimforpayment.model.LineItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CivilClaimMapperImpl.class, CivilClaimEvidenceMapperImpl.class})
public class CivilClaimMapperTest {

  private static final String UFN = "UFN123";
  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();
  private static final String CLIENT = "John Doe";
  private static final String CATEGORY = "A";
  private static final LocalDate CONCLUDED = LocalDate.of(2020, 1, 1);
  private static final String FEE_TYPE = "Standard";
  private static final Boolean ESCAPED = false;
  private static final String COUNSEL_PAYMENT = "Paid and Reconciled";
  private static final BigDecimal CLAIMED = BigDecimal.valueOf(100.0);
  private static final UUID SUBMISSION_ID = UUID.randomUUID();

  @Autowired
  private CivilClaimMapper mapper;

  @Test
  void shouldMapToCivilClaim() {
    ClaimEvidence claimEvidence1 = ClaimEvidence.builder().id(1L).fileKey("fileKey1").fileSize(1000L).build();
    ClaimEvidence claimEvidence2 = ClaimEvidence.builder().id(2L).fileKey("fileKey2").fileSize(2000L).build();

    CivilClaimEvidence civilClaimEvidence1 = new CivilClaimEvidence();
    civilClaimEvidence1.setId(1L);
    civilClaimEvidence1.setFileKey("fileKey1");
    civilClaimEvidence1.setFileSize(1000L);

    CivilClaimEvidence civilClaimEvidence2 = new CivilClaimEvidence();
    civilClaimEvidence2.setId(2L);
    civilClaimEvidence2.setFileKey("fileKey2");
    civilClaimEvidence2.setFileSize(2000L);

    LineItem lineItem1 = LineItem.builder().id(1L).evidenceItems(List.of(1L)).build();
    LineItem lineItem2 = LineItem.builder().id(2L).evidenceItems(List.of(1L, 2L)).build();

    CivilLineItem civilLineItem1 = new CivilLineItem();
    civilLineItem1.setId(1L);
    civilLineItem1.setEvidenceItems(List.of(1L));

    CivilLineItem civilLineItem2 = new CivilLineItem();
    civilLineItem2.setId(2L);
    civilLineItem2.setEvidenceItems(List.of(1L, 2L));

    Claim claim = Claim.builder()
        .id(1L)
        .ufn(UFN)
        .providerUserId(PROVIDER_USER_ID)
        .client(CLIENT)
        .category(CATEGORY)
        .concluded(CONCLUDED)
        .feeType(FEE_TYPE)
        .escaped(ESCAPED)
        .counselPayment(COUNSEL_PAYMENT)
        .claimed(CLAIMED)
        .submissionId(SUBMISSION_ID)
        .lineItems(List.of(lineItem1, lineItem2))
        .evidence(List.of(claimEvidence1, claimEvidence2))
        .build();

    var result = mapper.toCivilClaim(claim);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(claim.getId());
    assertThat(result.getUfn()).isEqualTo(claim.getUfn());
    assertThat(result.getProviderUserId()).isEqualTo(claim.getProviderUserId());
    assertThat(result.getClient()).isEqualTo(claim.getClient());
    assertThat(result.getCategory()).isEqualTo(claim.getCategory());
    assertThat(result.getConcluded()).isEqualTo(claim.getConcluded());
    assertThat(result.getFeeType()).isEqualTo(claim.getFeeType());
    assertThat(result.getEscaped()).isEqualTo(claim.getEscaped());
    assertThat(result.getCounselPayment()).isEqualTo(claim.getCounselPayment());
    assertThat(result.getClaimed()).isEqualTo(claim.getClaimed());
    assertThat(result.getSubmissionId()).isEqualTo(claim.getSubmissionId());
    assertThat(result.getLineItems()).containsExactly(civilLineItem1, civilLineItem2);
    assertThat(result.getEvidence()).containsExactly(civilClaimEvidence1, civilClaimEvidence2);
  }

  @Test
  void shouldMapToClaim() {
    ClaimEvidence claimEvidence1 = ClaimEvidence.builder().id(1L).fileKey("fileKey1").fileSize(1000L).build();
    ClaimEvidence claimEvidence2 = ClaimEvidence.builder().id(2L).fileKey("fileKey2").fileSize(2000L).build();

    CivilClaimEvidence civilClaimEvidence1 = new CivilClaimEvidence();
    civilClaimEvidence1.setId(1L);
    civilClaimEvidence1.setFileKey("fileKey1");
    civilClaimEvidence1.setFileSize(1000L);

    CivilClaimEvidence civilClaimEvidence2 = new CivilClaimEvidence();
    civilClaimEvidence2.setId(2L);
    civilClaimEvidence2.setFileKey("fileKey2");
    civilClaimEvidence2.setFileSize(2000L);

    LineItem lineItem1 = LineItem.builder().id(1L).evidenceItems(List.of(1L)).build();
    LineItem lineItem2 = LineItem.builder().id(2L).evidenceItems(List.of(1L, 2L)).build();

    CivilLineItem civilLineItem1 = new CivilLineItem();
    civilLineItem1.setId(1L);
    civilLineItem1.setEvidenceItems(List.of(1L));

    CivilLineItem civilLineItem2 = new CivilLineItem();
    civilLineItem2.setId(2L);
    civilLineItem2.setEvidenceItems(List.of(1L, 2L));

    var civilClaim = new CivilClaim();
    civilClaim.setId(1L);
    civilClaim.setUfn(UFN);
    civilClaim.setProviderUserId(PROVIDER_USER_ID);
    civilClaim.setClient(CLIENT);
    civilClaim.setCategory(CATEGORY);
    civilClaim.setConcluded(CONCLUDED);
    civilClaim.setFeeType(FEE_TYPE);
    civilClaim.setEscaped(ESCAPED);
    civilClaim.setCounselPayment(COUNSEL_PAYMENT);
    civilClaim.setClaimed(CLAIMED);
    civilClaim.setSubmissionId(SUBMISSION_ID);
    civilClaim.setLineItems(List.of(civilLineItem1, civilLineItem2));
    civilClaim.setEvidence(List.of(civilClaimEvidence1, civilClaimEvidence2));

    var result = mapper.toClaim(civilClaim);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(civilClaim.getId());
    assertThat(result.getUfn()).isEqualTo(civilClaim.getUfn());
    assertThat(result.getProviderUserId()).isEqualTo(civilClaim.getProviderUserId());
    assertThat(result.getClient()).isEqualTo(civilClaim.getClient());
    assertThat(result.getCategory()).isEqualTo(civilClaim.getCategory());
    assertThat(result.getConcluded()).isEqualTo(civilClaim.getConcluded());
    assertThat(result.getFeeType()).isEqualTo(civilClaim.getFeeType());
    assertThat(result.getEscaped()).isEqualTo(civilClaim.getEscaped());
    assertThat(result.getCounselPayment()).isEqualTo(civilClaim.getCounselPayment());
    assertThat(result.getClaimed()).isEqualTo(civilClaim.getClaimed());
    assertThat(result.getSubmissionId()).isEqualTo(civilClaim.getSubmissionId());
    assertThat(result.getLineItems()).containsExactly(lineItem1, lineItem2);
    assertThat(result.getEvidence()).containsExactly(claimEvidence1, claimEvidence2);
  }
}
