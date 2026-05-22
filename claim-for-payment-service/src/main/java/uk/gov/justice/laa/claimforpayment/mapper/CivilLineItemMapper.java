package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilLineItem;
import uk.gov.justice.laa.claimforpayment.model.LineItem;

/** the mapper between civil line item and api line item. */
@Mapper(componentModel = "spring")
public interface CivilLineItemMapper {
  LineItem toLineItem(CivilLineItem civilLineItem);

  CivilLineItem toCivilLineItem(LineItem lineItem);
}
