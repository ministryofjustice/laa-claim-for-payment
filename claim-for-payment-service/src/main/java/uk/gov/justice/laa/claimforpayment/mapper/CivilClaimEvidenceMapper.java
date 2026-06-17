package uk.gov.justice.laa.claimforpayment.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidence;
import uk.gov.justice.laa.claimforpayment.model.ClaimEvidence;

/** the mapper between civil claim evidence and api claim evidence. */
@Mapper(componentModel = "spring")
public interface CivilClaimEvidenceMapper {
  ClaimEvidence toClaimEvidence(CivilClaimEvidence claim);

  CivilClaimEvidence toCivilClaimEvidence(ClaimEvidence claim);

  default Instant map(OffsetDateTime value) {
    return value == null ? null : value.toInstant();
  }

  default OffsetDateTime map(Instant value) {
    return value == null ? null : value.atOffset(ZoneOffset.UTC);
  }
}
