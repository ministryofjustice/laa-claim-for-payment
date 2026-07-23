package uk.gov.justice.laa.claimforpayment.model;

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
 * Represents the request body for creating or updating a line item.
 *
 * <p>This model contains all necessary fields required to create a line item,
 * including title, category, date, actual net value, VAT applicability, and fee earner name.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = LineItemRequestBody.LineItemRequestBodyBuilder.class)
@Schema(name = "LineItemRequestBody", description = "Input model for creating or updating a line item")
public class LineItemRequestBody implements Serializable {

  private static final long serialVersionUID = 1L;

  private String title;

  private String category;

  private LocalDate date;

  private BigDecimal actualNetValue;

  private Boolean vatApplicable;

  private String feeEarnerName;

  /**
   * Builder for LineItemRequestBodyBuilder.
   */
  @JsonPOJOBuilder(withPrefix = "")
  public static class LineItemRequestBodyBuilder {
  }
}
