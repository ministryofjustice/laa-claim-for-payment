package uk.gov.justice.laa.claimforpayment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Auth config. */
@Component("authProps")
public class AuthConfig {

  @Value("${auth.scopes.claims-write}")
  private String claimsWriteScope;

  public String getClaimsWrite() {
    return claimsWriteScope;
  }
}
