package uk.gov.justice.laa.claimforpayment.civilclaims.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * create an claim
 */

@Schema(name = "ClaimRequestBody", description = "create an claim")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-09T14:59:13.025823+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class ClaimRequestBody implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;

  private String description;

  public ClaimRequestBody() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ClaimRequestBody(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public ClaimRequestBody name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ClaimRequestBody description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @NotNull 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
    return Objects.equals(this.name, claimRequestBody.name) &&
        Objects.equals(this.description, claimRequestBody.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimRequestBody {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
      this.instance.setName(value.name);
      this.instance.setDescription(value.description);
      return this;
    }

    public ClaimRequestBody.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ClaimRequestBody.Builder description(String description) {
      this.instance.description(description);
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

