package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A class representing evidence for a claim. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEvidence {

  @NotNull
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  private Long id;

  private String fileKey;
}
