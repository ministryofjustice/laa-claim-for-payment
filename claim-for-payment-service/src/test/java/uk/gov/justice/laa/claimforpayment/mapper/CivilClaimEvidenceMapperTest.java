package uk.gov.justice.laa.claimforpayment.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidence;
import uk.gov.justice.laa.claimforpayment.model.ClaimEvidence;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CivilClaimEvidenceMapperTest {

  private final CivilClaimEvidenceMapper mapper =
      Mappers.getMapper(CivilClaimEvidenceMapper.class);

  private static final UUID EVIDENCE_ID = UUID.randomUUID();

  @Test
  void shouldMapToCivilClaimEvidence() {
    ClaimEvidence claimEvidence = ClaimEvidence.builder()
        .id(EVIDENCE_ID)
        .fileKey("fileKey")
        .fileSize(1000L)
        .submittedOn(Instant.ofEpochMilli(1781696400000L))
        .build();

    var result = mapper.toCivilClaimEvidence(claimEvidence);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(claimEvidence.getId());
    assertThat(result.getFileKey()).isEqualTo(claimEvidence.getFileKey());
    assertThat(result.getFileSize()).isEqualTo(claimEvidence.getFileSize());
    assertThat(result.getSubmittedOn()).isEqualTo(OffsetDateTime.of(2026, 6, 17, 11, 40, 0, 0, ZoneOffset.UTC));
  }

  @Test
  void shouldMapToClaimEvidence() {
    var civilClaimEvidence = new CivilClaimEvidence();
    civilClaimEvidence.setId(EVIDENCE_ID);
    civilClaimEvidence.setFileKey("fileKey");
    civilClaimEvidence.setFileSize(1000L);
    civilClaimEvidence.setSubmittedOn(OffsetDateTime.of(2026, 6, 17, 11, 40, 0, 0, ZoneOffset.UTC));

    var result = mapper.toClaimEvidence(civilClaimEvidence);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(civilClaimEvidence.getId());
    assertThat(result.getFileKey()).isEqualTo(civilClaimEvidence.getFileKey());
    assertThat(result.getFileSize()).isEqualTo(civilClaimEvidence.getFileSize());
    assertThat(result.getSubmittedOn()).isEqualTo(Instant.ofEpochMilli(1781696400000L));
  }
}
