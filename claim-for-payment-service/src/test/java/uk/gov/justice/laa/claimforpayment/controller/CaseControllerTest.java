package uk.gov.justice.laa.claimforpayment.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.claimforpayment.access.model.AccessApplicationResponse;
import uk.gov.justice.laa.claimforpayment.config.ScopePropertyConfig;
import uk.gov.justice.laa.claimforpayment.model.CaseDto;
import uk.gov.justice.laa.claimforpayment.security.SecurityConfig;
import uk.gov.justice.laa.claimforpayment.service.AccessDataService;

@WebMvcTest(controllers = CaseController.class)
@TestPropertySource(properties = "security.enabled=true")
@Import({SecurityConfig.class, ScopePropertyConfig.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
public class CaseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AccessDataService mockAccessDataService;
  private static final UUID CASE_ID = UUID.randomUUID();
  private static final UUID PROVIDER_USER_ID = UUID.randomUUID();

  private static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt =
      jwt()
          .jwt(jwt -> jwt.claim("USER_NAME", PROVIDER_USER_ID.toString()))
          .authorities(() -> "SCOPE_Claims.Write");

  @Test
  void returnsOkStatusAndOneClaim() throws Exception {

    when(mockAccessDataService.getCase(CASE_ID))
        .thenReturn(
            CaseDto.builder()
                .applicationId(CASE_ID)
                .decisionStatus(AccessApplicationResponse.DecisionStatusEnum.GRANTED)
                .build());

    mockMvc
        .perform(get("/api/v1/cases/{id}", CASE_ID).param("status", "SUBMITTED").with(validJwt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.applicationId").value(CASE_ID.toString()))
        .andExpect(jsonPath("$.decisionStatus").value("GRANTED"));
  }
}
