package uk.gov.justice.laa.claimforpayment.mapper;

import com.fasterxml.uuid.Generators;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ClaimRequestBodyMapperTest {

  private final ClaimRequestBodyMapper mapper =
      Mappers.getMapper(ClaimRequestBodyMapper.class);

  private static final String UFN = "UFN123";
  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();
  private static final String CLIENT = "John Doe";
  private static final String CATEGORY = "A";
  private static final LocalDate CONCLUDED = LocalDate.of(2020, 1, 1);
  private static final String FEE_TYPE = "Standard";
  private static final Boolean ESCAPED = false;
  private static final String COUNSEL_PAYMENT = "Paid and Reconciled";
  private static final BigDecimal CLAIMED = BigDecimal.valueOf(100.0);

  private static final UUID CLAIM_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_1_ID = UUID.randomUUID();
  private static final UUID LINE_ITEM_2_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_1_ID = UUID.randomUUID();
  private static final UUID EVIDENCE_2_ID = UUID.randomUUID();

  @Test
  void shouldMapToCivilClaimRequestBody() {
    ClaimRequestBody body = ClaimRequestBody.builder()
        .ufn(UFN)
        .client(CLIENT)
        .category(CATEGORY)
        .concluded(CONCLUDED)
        .feeType(FEE_TYPE)
        .escaped(ESCAPED)
        .counselPayment(COUNSEL_PAYMENT)
        .claimed(CLAIMED)
        .build();

    UUID id = Generators.timeBasedEpochGenerator().generate();

    CivilClaimRequestBody result = mapper.toCivilClaimRequestBody(body, id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getUfn()).isEqualTo(body.getUfn());
    assertThat(result.getClient()).isEqualTo(body.getClient());
    assertThat(result.getCategory()).isEqualTo(body.getCategory());
    assertThat(result.getConcluded()).isEqualTo(body.getConcluded());
    assertThat(result.getFeeType()).isEqualTo(body.getFeeType());
    assertThat(result.getEscaped()).isEqualTo(body.getEscaped());
    assertThat(result.getCounselPayment()).isEqualTo(body.getCounselPayment());
    assertThat(result.getClaimed()).isEqualTo(body.getClaimed());
  }

  @Test
  void shouldMapToClaimRequestBody() {
    CivilClaimRequestBody body = new CivilClaimRequestBody();

    body.setUfn(UFN);
    body.setClient(CLIENT);
    body.setCategory(CATEGORY);
    body.setConcluded(CONCLUDED);
    body.setFeeType(FEE_TYPE);
    body.setEscaped(ESCAPED);
    body.setCounselPayment(COUNSEL_PAYMENT);
    body.setClaimed(CLAIMED);

    ClaimRequestBody result = mapper.toClaimRequestBody(body);

    assertThat(result).isNotNull();
    assertThat(result.getUfn()).isEqualTo(body.getUfn());
    assertThat(result.getClient()).isEqualTo(body.getClient());
    assertThat(result.getCategory()).isEqualTo(body.getCategory());
    assertThat(result.getConcluded()).isEqualTo(body.getConcluded());
    assertThat(result.getFeeType()).isEqualTo(body.getFeeType());
    assertThat(result.getEscaped()).isEqualTo(body.getEscaped());
    assertThat(result.getCounselPayment()).isEqualTo(body.getCounselPayment());
    assertThat(result.getClaimed()).isEqualTo(body.getClaimed());
  }
}
