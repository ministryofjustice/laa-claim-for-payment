package uk.gov.justice.laa.claimforpayment.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilLineItem;
import uk.gov.justice.laa.claimforpayment.model.LineItem;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CivilLineItemMapperTest {

  private final CivilLineItemMapper mapper =
      Mappers.getMapper(CivilLineItemMapper.class);

  @Test
  void shouldMapToCivilLineItem() {
    LineItem lineItem = LineItem.builder()
        .id(1L)
        .title("Test line item")
        .category("category1")
        .date(LocalDate.of(2020, 1, 1))
        .evidenceItems(List.of(1L, 2L))
        .build();

    var result = mapper.toCivilLineItem(lineItem);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(lineItem.getId());
    assertThat(result.getTitle()).isEqualTo(lineItem.getTitle());
    assertThat(result.getCategory()).isEqualTo(lineItem.getCategory());
    assertThat(result.getDate()).isEqualTo(lineItem.getDate());
    assertThat(result.getEvidenceItems()).containsExactly(1L, 2L);
  }

  @Test
  void shouldMapToLineItem() {
    Long claimEvidence1Id = 1L;
    Long claimEvidence2Id = 2L;

    var civilLineItem = new CivilLineItem();
    civilLineItem.setId(1L);
    civilLineItem.setTitle("Test line item");
    civilLineItem.setCategory("category1");
    civilLineItem.setEvidenceItems(List.of(claimEvidence1Id, claimEvidence2Id));

    var result = mapper.toLineItem(civilLineItem);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(civilLineItem.getId());
    assertThat(result.getTitle()).isEqualTo(civilLineItem.getTitle());
    assertThat(result.getCategory()).isEqualTo(civilLineItem.getCategory());
    assertThat(result.getEvidenceItems()).containsExactly(1L, 2L);
  }
}
