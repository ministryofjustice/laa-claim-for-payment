package uk.gov.justice.laa.claimforpayment.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidenceRequestBody;

import static org.assertj.core.api.Assertions.assertThat;

public class ClaimEvidenceRequestBodyMapperTest {

  private final ClaimEvidenceRequestBodyMapper mapper =
      Mappers.getMapper(ClaimEvidenceRequestBodyMapper.class);

  private static final String FILE_KEY = "file key";
  private static final Long FILE_SIZE = 1000L;

  @Test
  void shouldMapToCivilClaimEvidenceRequestBody() {
    UploadFile uploadFile = new UploadFile(FILE_KEY, FILE_SIZE);

    CivilClaimEvidenceRequestBody result = mapper.toCivilClaimEvidenceRequestBody(uploadFile);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getFileKey()).isEqualTo(FILE_KEY);
    assertThat(result.getFileSize()).isEqualTo(FILE_SIZE);
    assertThat(result.getSubmittedOn()).isNotNull();
  }
}
