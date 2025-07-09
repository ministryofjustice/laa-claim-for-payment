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
 * Claim
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-09T14:59:13.025823+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class Claim implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String name;

  private String description;

  public Claim() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Claim(Long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
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

  public Claim name(String name) {
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

  public Claim description(String description) {
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
    Claim claim = (Claim) o;
    return Objects.equals(this.id, claim.id) &&
        Objects.equals(this.name, claim.name) &&
        Objects.equals(this.description, claim.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Claim {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

    private Claim instance;

    public Builder() {
      this(new Claim());
    }

    protected Builder(Claim instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Claim value) { 
      this.instance.setId(value.id);
      this.instance.setName(value.name);
      this.instance.setDescription(value.description);
      return this;
    }

    public Claim.Builder id(Long id) {
      this.instance.id(id);
      return this;
    }
    
    public Claim.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public Claim.Builder description(String description) {
      this.instance.description(description);
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

