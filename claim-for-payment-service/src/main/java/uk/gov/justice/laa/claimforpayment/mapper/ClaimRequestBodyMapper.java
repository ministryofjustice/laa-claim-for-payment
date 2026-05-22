package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

/** the mapper between civil clam request body and api claim request body. */
@Mapper(componentModel = "spring")
public interface ClaimRequestBodyMapper {
  ClaimRequestBody toClaimRequestBody(CivilClaimRequestBody civilClaimRequestBody);

  CivilClaimRequestBody toCivilClaimRequestBody(ClaimRequestBody claimRequestBody);
}
