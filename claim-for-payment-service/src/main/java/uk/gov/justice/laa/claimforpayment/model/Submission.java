package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Submission – wraps a batch of claims along with submission metadata. */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Submission", description = "A submission containing multiple claims")
public class Submission implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(description = "Unique identifier for the submission")
  @JsonProperty("id")
  private UUID id;

  @Schema(description = "Reference number")
  private String friendlyId;

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

  @Schema(description = "List of claims in this submission")
  @JsonProperty("claims")
  private List<Claim> claims;

  @Schema(description = "Total amount claimed in this submission")
  @Column(precision = 10, scale = 2)
  private BigDecimal totalClaimed;
}
