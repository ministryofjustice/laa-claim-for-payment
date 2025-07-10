package uk.gov.justice.laa.claimforpayment.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Civil Claims API client.
 */
@Data
@Component
@ConfigurationProperties(prefix = "civilclaims.api")
public class CivilClaimsProperties {
  private String baseUrl;
  private String token;
  private Duration timeout;
}