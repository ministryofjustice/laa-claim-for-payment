package uk.gov.justice.laa.claimforpayment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.model.Claim;

@ExtendWith(MockitoExtension.class)
class ClaimMapperTest {
  private static final Long CLAIM_ID = 123L;
  private static final String UFN = "UFN123";
  private static final String CLIENT = "John Doe";
  private static final String CATEGORY = "A";
  private static final LocalDate CONCLUDED = LocalDate.of(2024, 7, 7);
  private static final String FEE_TYPE = "Standard";
  private static final BigDecimal CLAIMED = new BigDecimal(100.0);

  @InjectMocks private ClaimMapper claimMapper = new ClaimMapperImpl();

  @Test
  void shouldMapToClaimEntity() {
    Claim claim = Claim.builder()
        .id(CLAIM_ID)
        .ufn(UFN)
        .client(CLIENT)
        .category(CATEGORY)
        .concluded(CONCLUDED)
        .feeType(FEE_TYPE)
        .claimed(CLAIMED)
        .build();

    ClaimEntity result = claimMapper.toClaimEntity(claim);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(CLAIM_ID);
    assertThat(result.getUfn()).isEqualTo(UFN);
    assertThat(result.getClient()).isEqualTo(CLIENT);
    assertThat(result.getCategory()).isEqualTo(CATEGORY);
    assertThat(result.getConcluded()).isEqualTo(CONCLUDED);
    assertThat(result.getFeeType()).isEqualTo(FEE_TYPE);
    assertThat(result.getClaimed()).isEqualTo(CLAIMED);
  }

  @Test
  void shouldMapToClaim() {
    ClaimEntity claimEntity = ClaimEntity.builder()
        .id(CLAIM_ID)
        .ufn(UFN)
        .client(CLIENT)
        .category(CATEGORY)
        .concluded(CONCLUDED)
        .feeType(FEE_TYPE)
        .claimed(CLAIMED)
        .build();

    Claim result = claimMapper.toClaim(claimEntity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(CLAIM_ID);
    assertThat(result.getUfn()).isEqualTo(UFN);
    assertThat(result.getClient()).isEqualTo(CLIENT);
    assertThat(result.getCategory()).isEqualTo(CATEGORY);
    assertThat(result.getConcluded()).isEqualTo(CONCLUDED);
    assertThat(result.getFeeType()).isEqualTo(FEE_TYPE);
    assertThat(result.getClaimed()).isEqualTo(CLAIMED);
  }
}
