package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
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

/**
 * Represents the request body for creating or updating a claim.
 *
 * <p>This model contains all necessary fields required to submit a claim, including client details,
 * claim category, dates, fee type, claimed amount, and a unique submission identifier.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = ClaimRequestBody.ClaimRequestBodyBuilder.class)
@Schema(name = "ClaimRequestBody", description = "Input model for creating or updating a claim")
public class ClaimRequestBody implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @JsonProperty("ufn")
  @Schema(description = "universal file number")
  private String ufn;

  @NotNull
  @JsonProperty("client")
  @Schema(description = "client name")
  private String client;

  @JsonProperty("category")
  @Schema(description = "claim category")
  private String category;

  @JsonProperty("concluded")
  @Schema(description = "claim concluded date")
  private LocalDate concluded;

  @JsonProperty("feeType")
  @Schema(description = "fee type")
  private String feeType;

  @JsonProperty("claimed")
  @Schema(description = "amount claimed")
  private BigDecimal claimed;

  /** Builder for ClaimRequestBody. */
  @JsonPOJOBuilder(withPrefix = "")
  public static class ClaimRequestBodyBuilder {}
}
