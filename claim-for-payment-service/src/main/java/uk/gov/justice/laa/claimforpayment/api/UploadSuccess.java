package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/** Success response for upload operations. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UploadSuccess", allOf = {})
public record UploadSuccess(

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    UUID evidenceId,

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    UploadFile file,

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String message

) implements UploadResponse {

  @Override
  @JsonProperty("type")
  public String type() {
    return "success";
  }
}
