package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilLineItemRequestBody;
import uk.gov.justice.laa.claimforpayment.model.LineItemRequestBody;

/** the mapper between civil line item request body and api line item request body. */
@Mapper(componentModel = "spring")
public interface LineItemRequestBodyMapper {
  LineItemRequestBody toLineItemRequestBody(CivilLineItemRequestBody civilLineItemRequestBody);

  @Mapping(target = "id", ignore = true)
  CivilLineItemRequestBody toCivilLineItemRequestBody(LineItemRequestBody lineItemRequestBody);
}
