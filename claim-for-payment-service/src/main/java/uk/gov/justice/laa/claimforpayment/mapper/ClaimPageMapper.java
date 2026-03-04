package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;

/** Mapper for converting ClaimPageResponse to ClaimPage domain object. */
@Mapper(componentModel = "spring", uses = CivilClaimMapper.class)
public interface ClaimPageMapper {

  @Mapping(target = "totalElements", source = "total")
  ClaimPage toDomain(CivilClaimPageResponse response);
}
