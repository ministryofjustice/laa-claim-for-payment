package uk.gov.justice.laa.claimforpayment.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ClaimRequestBody
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-23T15:05:27.031648+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class ClaimRequestBody implements Serializable {

  private static final long serialVersionUID = 1L;

  private String ufn;

  private String client;

  private @Nullable String category;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate concluded;

  private @Nullable String feeType;

  private @Nullable Double claimed;

  public ClaimRequestBody() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ClaimRequestBody(String ufn, String client) {
    this.ufn = ufn;
    this.client = client;
  }

  public ClaimRequestBody ufn(String ufn) {
    this.ufn = ufn;
    return this;
  }

  /**
   * universal file number
   * @return ufn
   */
  @NotNull 
  @Schema(name = "ufn", description = "universal file number", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ufn")
  public String getUfn() {
    return ufn;
  }

  public void setUfn(String ufn) {
    this.ufn = ufn;
  }

  public ClaimRequestBody client(String client) {
    this.client = client;
    return this;
  }

  /**
   * client name
   * @return client
   */
  @NotNull 
  @Schema(name = "client", description = "client name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("client")
  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public ClaimRequestBody category(@Nullable String category) {
    this.category = category;
    return this;
  }

  /**
   * claim category
   * @return category
   */
  
  @Schema(name = "category", description = "claim category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("category")
  public @Nullable String getCategory() {
    return category;
  }

  public void setCategory(@Nullable String category) {
    this.category = category;
  }

  public ClaimRequestBody concluded(@Nullable LocalDate concluded) {
    this.concluded = concluded;
    return this;
  }

  /**
   * Get concluded
   * @return concluded
   */
  @Valid 
  @Schema(name = "concluded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("concluded")
  public @Nullable LocalDate getConcluded() {
    return concluded;
  }

  public void setConcluded(@Nullable LocalDate concluded) {
    this.concluded = concluded;
  }

  public ClaimRequestBody feeType(@Nullable String feeType) {
    this.feeType = feeType;
    return this;
  }

  /**
   * fee type
   * @return feeType
   */
  
  @Schema(name = "feeType", description = "fee type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("feeType")
  public @Nullable String getFeeType() {
    return feeType;
  }

  public void setFeeType(@Nullable String feeType) {
    this.feeType = feeType;
  }

  public ClaimRequestBody claimed(@Nullable Double claimed) {
    this.claimed = claimed;
    return this;
  }

  /**
   * amount claimed
   * @return claimed
   */
  
  @Schema(name = "claimed", description = "amount claimed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("claimed")
  public @Nullable Double getClaimed() {
    return claimed;
  }

  public void setClaimed(@Nullable Double claimed) {
    this.claimed = claimed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClaimRequestBody claimRequestBody = (ClaimRequestBody) o;
    return Objects.equals(this.ufn, claimRequestBody.ufn) &&
        Objects.equals(this.client, claimRequestBody.client) &&
        Objects.equals(this.category, claimRequestBody.category) &&
        Objects.equals(this.concluded, claimRequestBody.concluded) &&
        Objects.equals(this.feeType, claimRequestBody.feeType) &&
        Objects.equals(this.claimed, claimRequestBody.claimed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ufn, client, category, concluded, feeType, claimed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimRequestBody {\n");
    sb.append("    ufn: ").append(toIndentedString(ufn)).append("\n");
    sb.append("    client: ").append(toIndentedString(client)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    concluded: ").append(toIndentedString(concluded)).append("\n");
    sb.append("    feeType: ").append(toIndentedString(feeType)).append("\n");
    sb.append("    claimed: ").append(toIndentedString(claimed)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
  public static class Builder {

    private ClaimRequestBody instance;

    public Builder() {
      this(new ClaimRequestBody());
    }

    protected Builder(ClaimRequestBody instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ClaimRequestBody value) { 
      this.instance.setUfn(value.ufn);
      this.instance.setClient(value.client);
      this.instance.setCategory(value.category);
      this.instance.setConcluded(value.concluded);
      this.instance.setFeeType(value.feeType);
      this.instance.setClaimed(value.claimed);
      return this;
    }

    public ClaimRequestBody.Builder ufn(String ufn) {
      this.instance.ufn(ufn);
      return this;
    }
    
    public ClaimRequestBody.Builder client(String client) {
      this.instance.client(client);
      return this;
    }
    
    public ClaimRequestBody.Builder category(String category) {
      this.instance.category(category);
      return this;
    }
    
    public ClaimRequestBody.Builder concluded(LocalDate concluded) {
      this.instance.concluded(concluded);
      return this;
    }
    
    public ClaimRequestBody.Builder feeType(String feeType) {
      this.instance.feeType(feeType);
      return this;
    }
    
    public ClaimRequestBody.Builder claimed(Double claimed) {
      this.instance.claimed(claimed);
      return this;
    }
    
    /**
    * returns a built ClaimRequestBody instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ClaimRequestBody build() {
      try {
        return this.instance;
      } finally {
        // ensure that this.instance is not reused
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  /**
  * Create a builder with no initialized field (except for the default values).
  */
  public static ClaimRequestBody.Builder builder() {
    return new ClaimRequestBody.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ClaimRequestBody.Builder toBuilder() {
    ClaimRequestBody.Builder builder = new ClaimRequestBody.Builder();
    return builder.copyOf(this);
  }

}

