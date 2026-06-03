package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/** Contains details of an upload error. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UploadError", allOf = {})
public record UploadError(

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    UploadFile file,

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String message

) implements UploadResponse {

  @Override
  @JsonProperty("type")
  public String type() {
    return "error";
  }
}
