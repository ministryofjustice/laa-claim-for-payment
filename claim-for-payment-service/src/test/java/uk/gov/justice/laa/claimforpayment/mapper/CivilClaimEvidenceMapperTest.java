package uk.gov.justice.laa.claimforpayment.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidence;
import uk.gov.justice.laa.claimforpayment.model.ClaimEvidence;

import static org.assertj.core.api.Assertions.assertThat;

public class CivilClaimEvidenceMapperTest {

  private final CivilClaimEvidenceMapper mapper =
      Mappers.getMapper(CivilClaimEvidenceMapper.class);

  @Test
  void shouldMapToCivilClaimEvidence() {
    ClaimEvidence claimEvidence = ClaimEvidence.builder()
        .id(1L)
        .fileKey("fileKey")
        .fileSize(1000L)
        .build();

    var result = mapper.toCivilClaimEvidence(claimEvidence);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(claimEvidence.getId());
    assertThat(result.getFileKey()).isEqualTo(claimEvidence.getFileKey());
    assertThat(result.getFileSize()).isEqualTo(claimEvidence.getFileSize());
  }

  @Test
  void shouldMapToClaimEvidence() {
    var civilClaimEvidence = new CivilClaimEvidence();
    civilClaimEvidence.setId(1L);
    civilClaimEvidence.setFileKey("fileKey");
    civilClaimEvidence.setFileSize(1000L);

    var result = mapper.toClaimEvidence(civilClaimEvidence);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(civilClaimEvidence.getId());
    assertThat(result.getFileKey()).isEqualTo(civilClaimEvidence.getFileKey());
    assertThat(result.getFileSize()).isEqualTo(civilClaimEvidence.getFileSize());
  }
}
