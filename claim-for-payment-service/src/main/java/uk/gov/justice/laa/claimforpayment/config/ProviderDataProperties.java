package uk.gov.justice.laa.claimforpayment.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Provider Data API client.
 */
@Data
@Component
@ConfigurationProperties(prefix = "provider-data.api")
public class ProviderDataProperties {
  private String baseUrl;
  private String token;
  private Duration timeout;
}