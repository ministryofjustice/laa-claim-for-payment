package uk.gov.justice.laa.claimforpayment.mapper;

import com.fasterxml.uuid.Generators;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimEvidenceRequestBody;

/** the mapper between civil clam request body and api claim request body. */
@Mapper(componentModel = "spring")
public interface ClaimEvidenceRequestBodyMapper {

  @Mapping(target = "id", expression = "java(generateId())")
  @Mapping(target = "fileKey", source = "filename")
  @Mapping(target = "fileSize", source = "filesize")
  @Mapping(target = "submittedOn", expression = "java(now())")
  CivilClaimEvidenceRequestBody toCivilClaimEvidenceRequestBody(UploadFile uploadFile);

  default UUID generateId() {
    return Generators.timeBasedEpochGenerator().generate();
  }

  default OffsetDateTime now() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }
}
