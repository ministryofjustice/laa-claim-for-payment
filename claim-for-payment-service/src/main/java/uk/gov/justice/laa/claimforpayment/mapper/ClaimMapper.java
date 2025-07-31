package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.model.Claim;

/** The mapper between Claim and ClaimEntity. */
@Mapper(componentModel = "spring")
public interface ClaimMapper {

  /**
   * Maps the given claim entity to an claim.
   *
   * @param claimEntity the claim entity
   * @return the claim
   */
  @Mapping(target = "submissionId", source = "submission.id")
  Claim toClaim(ClaimEntity claimEntity);

  /**
   * Maps the given claim to an claim entity.
   *
   * @param claim the claim
   * @return the claim entity
   */
  ClaimEntity toClaimEntity(Claim claim);
}
