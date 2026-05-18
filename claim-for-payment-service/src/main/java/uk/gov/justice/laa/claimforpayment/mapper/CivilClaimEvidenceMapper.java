package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidence;
import uk.gov.justice.laa.claimforpayment.model.ClaimEvidence;

/** the mapper between civil claim evidence and api claim evidence. */
@Mapper(componentModel = "spring")
public interface CivilClaimEvidenceMapper {
  ClaimEvidence toClaimEvidence(CivilClaimEvidence claim);

  CivilClaimEvidence toCivilClaimEvidence(ClaimEvidence claim);
}
