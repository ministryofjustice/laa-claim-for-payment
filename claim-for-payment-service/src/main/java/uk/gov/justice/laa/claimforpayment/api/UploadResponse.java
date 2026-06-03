package uk.gov.justice.laa.claimforpayment.api;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/** Response for upload operations. */
@Schema(
    name = "UploadResponse",
    discriminatorProperty = "type",
    discriminatorMapping = {
        @DiscriminatorMapping(value = "success", schema = UploadSuccess.class),
        @DiscriminatorMapping(value = "error", schema = UploadError.class)
    },
    oneOf = {UploadSuccess.class, UploadError.class}
)
public sealed interface UploadResponse permits UploadSuccess, UploadError {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  String type();
}

