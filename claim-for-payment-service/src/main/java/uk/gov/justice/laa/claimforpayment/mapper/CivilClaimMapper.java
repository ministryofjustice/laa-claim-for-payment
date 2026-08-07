package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.model.Claim;

/** the mapper between civil claims and api claims. */
@Mapper(
    componentModel = "spring",
    uses = {CivilClaimEvidenceMapper.class})
public interface CivilClaimMapper {
  @Mapping(target = "version", ignore = true)
  Claim toClaim(CivilClaim claim);

  CivilClaim toCivilClaim(Claim claim);
}
