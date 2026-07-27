package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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

  @Schema(description = "cost type")
  @JsonProperty("costType")
  private CostType costType;

  @Schema(description = "court type")
  @JsonProperty("courtType")
  private CourtType courtType;

  @Schema(description = "client party status")
  @JsonProperty("clientPartyStatus")
  private ClientPartyStatus clientPartyStatus;

  @Schema(description = "first acting solicitor")
  @JsonProperty("firstActingSolicitorFlag")
  private Boolean firstActingSolicitorFlag;

  @Schema(description = "transfer of solicitor")
  @JsonProperty("transferOfSolicitorFlag")
  private Boolean transferOfSolicitorFlag;

  @Schema(description = "number of clients retained")
  @JsonProperty("clientsRetainedCount")
  private Count clientsRetainedCount;

  @Schema(description = "number of clients at start of case")
  @JsonProperty("clientsStartCount")
  private Count clientsStartCount;

  @Schema(description = "at least one hearing representing more than one client")
  @JsonProperty("multiClientHearingFlag")
  private Boolean multiClientHearingFlag;

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

  @Schema(description = "is claim escaped")
  @JsonProperty("escaped")
  private Boolean escaped;

  @Schema(description = "counsel payment")
  @JsonProperty("counselPayment")
  private String counselPayment;

  @Schema(description = "amount claimed")
  @JsonProperty("claimed")
  private BigDecimal claimed;

  /** Builder for ClaimRequestBody. */
  @JsonPOJOBuilder(withPrefix = "")
  public static class ClaimRequestBodyBuilder {}
}
