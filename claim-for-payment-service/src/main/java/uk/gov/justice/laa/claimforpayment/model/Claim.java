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
 * Claim
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-23T15:05:27.031648+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class Claim implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private @Nullable String ufn;

  private @Nullable String client;

  private @Nullable String category;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate concluded;

  private @Nullable String feeType;

  private @Nullable Double claimed;

  public Claim() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Claim(Long id) {
    this.id = id;
  }

  public Claim id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Claim ufn(@Nullable String ufn) {
    this.ufn = ufn;
    return this;
  }

  /**
   * universal file number
   * @return ufn
   */
  
  @Schema(name = "ufn", description = "universal file number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ufn")
  public @Nullable String getUfn() {
    return ufn;
  }

  public void setUfn(@Nullable String ufn) {
    this.ufn = ufn;
  }

  public Claim client(@Nullable String client) {
    this.client = client;
    return this;
  }

  /**
   * client name
   * @return client
   */
  
  @Schema(name = "client", description = "client name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client")
  public @Nullable String getClient() {
    return client;
  }

  public void setClient(@Nullable String client) {
    this.client = client;
  }

  public Claim category(@Nullable String category) {
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

  public Claim concluded(@Nullable LocalDate concluded) {
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

  public Claim feeType(@Nullable String feeType) {
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

  public Claim claimed(@Nullable Double claimed) {
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
    Claim claim = (Claim) o;
    return Objects.equals(this.id, claim.id) &&
        Objects.equals(this.ufn, claim.ufn) &&
        Objects.equals(this.client, claim.client) &&
        Objects.equals(this.category, claim.category) &&
        Objects.equals(this.concluded, claim.concluded) &&
        Objects.equals(this.feeType, claim.feeType) &&
        Objects.equals(this.claimed, claim.claimed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ufn, client, category, concluded, feeType, claimed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Claim {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

    private Claim instance;

    public Builder() {
      this(new Claim());
    }

    protected Builder(Claim instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Claim value) { 
      this.instance.setId(value.id);
      this.instance.setUfn(value.ufn);
      this.instance.setClient(value.client);
      this.instance.setCategory(value.category);
      this.instance.setConcluded(value.concluded);
      this.instance.setFeeType(value.feeType);
      this.instance.setClaimed(value.claimed);
      return this;
    }

    public Claim.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public Claim.Builder ufn(String ufn) {
      this.instance.ufn(ufn);
      return this;
    }
    
    public Claim.Builder client(String client) {
      this.instance.client(client);
      return this;
    }
    
    public Claim.Builder category(String category) {
      this.instance.category(category);
      return this;
    }
    
    public Claim.Builder concluded(LocalDate concluded) {
      this.instance.concluded(concluded);
      return this;
    }
    
    public Claim.Builder feeType(String feeType) {
      this.instance.feeType(feeType);
      return this;
    }
    
    public Claim.Builder claimed(Double claimed) {
      this.instance.claimed(claimed);
      return this;
    }
    
    /**
    * returns a built Claim instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public Claim build() {
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
  public static Claim.Builder builder() {
    return new Claim.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public Claim.Builder toBuilder() {
    Claim.Builder builder = new Claim.Builder();
    return builder.copyOf(this);
  }

}

