package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
  private UUID id;

  @JsonIgnore
  private Long version;

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

  @Schema(description = "ID of the provider user making the submission")
  @JsonProperty("providerUserId")
  private UUID providerUserId;

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

  @Schema(description = "escaped")
  @JsonProperty("escaped")
  private Boolean escaped;

  @Schema(description = "counsel payment")
  @JsonProperty("counselPayment")
  private String counselPayment;

  @Schema(description = "amount claimed")
  @JsonProperty("claimed")
  private BigDecimal claimed;

  @Schema(description = "line items associated with the claim")
  @JsonProperty("lineItems")
  private List<LineItem> lineItems;

  @Schema(description = "evidence associated with the claim")
  @JsonProperty("evidence")
  private List<ClaimEvidence> evidence;
}
