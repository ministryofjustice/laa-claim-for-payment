package uk.gov.justice.laa.claimforpayment.api;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

/** Request body for uploading evidence files. */
public class UploadEvidenceRequest {

  @Schema(type = "string", format = "binary", description = "Evidence file")
  public MultipartFile documents;
}
