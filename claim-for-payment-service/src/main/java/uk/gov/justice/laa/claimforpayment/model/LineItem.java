package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents a line item within a claim for payment. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {

  @NotNull
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  private Long id;

  private String title;

  private String category;

  private LocalDate date;

  @Builder.Default private List<ClaimEvidence> evidenceItems = new ArrayList<>();

  void addEvidence(ClaimEvidence evidence) {
    evidenceItems.add(evidence);
  }
}
