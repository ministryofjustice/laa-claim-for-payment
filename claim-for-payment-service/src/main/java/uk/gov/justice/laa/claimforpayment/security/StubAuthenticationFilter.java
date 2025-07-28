package uk.gov.justice.laa.claimforpayment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A stub authentication filter that gives some useful defaults without
 * a real user principle.
 */
@Component
public class StubAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Always the same fake user for now
    ProviderUserPrincipal principal =
        new ProviderUserPrincipal(
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "stub-user");

    var authentication =
        new UsernamePasswordAuthenticationToken(
            principal, null, List.of() // no authorities needed yet
            );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}
