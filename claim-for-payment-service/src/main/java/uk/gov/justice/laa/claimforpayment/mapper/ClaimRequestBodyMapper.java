package uk.gov.justice.laa.claimforpayment.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

/** the mapper between civil clam request body and api claim request body. */
@Mapper(componentModel = "spring")
public interface ClaimRequestBodyMapper {
  ClaimRequestBody toClaimRequestBody(CivilClaimRequestBody civilClaimRequestBody);

  @Mapping(target = "id", source = "id")
  CivilClaimRequestBody toCivilClaimRequestBody(ClaimRequestBody claimRequestBody, UUID id);
}
