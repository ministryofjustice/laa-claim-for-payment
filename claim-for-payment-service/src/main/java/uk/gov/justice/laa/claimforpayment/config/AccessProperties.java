package uk.gov.justice.laa.claimforpayment.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Access API client.
 */
@Data
@Component
@ConfigurationProperties(prefix = "access.api")
public class AccessProperties {
  private String baseUrl;
  private String token;
  private Duration timeout;
}