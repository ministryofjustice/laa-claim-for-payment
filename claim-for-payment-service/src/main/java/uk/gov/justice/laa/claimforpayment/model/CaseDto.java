package uk.gov.justice.laa.claimforpayment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.claimforpayment.access.model.AccessApplicationResponse.DecisionStatusEnum;

/** CaseDto represents certificate/application data from AccessDS. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDto {

  @Schema(name = "applicationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("applicationId")
  private UUID applicationId;

  private DecisionStatusEnum decisionStatus;
}
