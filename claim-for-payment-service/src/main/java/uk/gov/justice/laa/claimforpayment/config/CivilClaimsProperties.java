package uk.gov.justice.laa.claimforpayment.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "civilclaims.api")
public class CivilClaimsProperties {
    private String baseUrl;
    private String token;
    private Duration timeout;
}