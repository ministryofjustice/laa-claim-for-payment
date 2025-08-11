package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents a claim for payment with details such as client, category, and amount claimed. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  private Long id;

  @Schema(description = "universal file number")
  @JsonProperty("ufn")
  private String ufn;

  @Schema(description = "client name")
  @JsonProperty("client")
  private String client;

  @Schema(description = "claim category")
  @JsonProperty("category")
  private String category;

  @Schema(description = "claim concluded date")
  @JsonProperty("concluded")
  private LocalDate concluded;

  @Schema(description = "fee type")
  @JsonProperty("feeType")
  private String feeType;

  @Schema(description = "amount claimed")
  @JsonProperty("claimed")
  private BigDecimal claimed;

  @NotNull
  @Schema(description = "id of the submission this claim belongs to")
  @JsonProperty("submissionId")
  private UUID submissionId;
}
