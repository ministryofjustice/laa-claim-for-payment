package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Submission – wraps a batch of claims along with submission metadata. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "SubmissionRequestBody",
    description = "A new or updated submission containing zero to multiple claims")
public class SubmissionRequestBody implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Schema(description = "ID of the provider user making the submission")
  @JsonProperty("providerUserId")
  private UUID providerUserId;

  @Schema(description = "Office ID of the provider")
  @JsonProperty("providerOfficeId")
  private UUID providerOfficeId;

  @Schema(description = "Type of submission")
  @JsonProperty("submissionTypeCode")
  private String submissionTypeCode;

  @Schema(description = "Date of submission")
  @JsonProperty("submissionDate")
  private LocalDateTime submissionDate;

  @Schema(description = "Start of the claim period")
  @JsonProperty("submissionPeriodStartDate")
  private LocalDateTime submissionPeriodStartDate;

  @Schema(description = "End of the claim period")
  @JsonProperty("submissionPeriodEndDate")
  private LocalDateTime submissionPeriodEndDate;

  @Schema(description = "Associated schedule ID")
  @JsonProperty("scheduleId")
  private String scheduleId;

}
