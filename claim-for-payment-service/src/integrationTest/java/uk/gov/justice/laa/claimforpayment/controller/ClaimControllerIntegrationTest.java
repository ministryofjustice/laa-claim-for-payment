package uk.gov.justice.laa.claimforpayment.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlassian.oai.validator.wiremock.OpenApiValidationListener;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.gov.justice.laa.claimforpayment.ClaimForPaymentApplication;
import uk.gov.justice.laa.claimforpayment.config.auth.EntraOboTokenProvider;

@SpringBootTest(
    classes = ClaimForPaymentApplication.class,
    properties = "security.enabled=true",
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableWireMock({
  @ConfigureWireMock(
      name = "civil-claims-service",
      baseUrlProperties = "civilclaims.api.base-url",
      filesUnderClasspath = "wiremock/civil-claims-service")
})
@ActiveProfiles("test")
class ClaimControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  private UUID providerUserId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @InjectWireMock("civil-claims-service")
  private WireMockServer wireMockServer;

  private OpenApiValidationListener validationListener;

  @MockitoBean private EntraOboTokenProvider oboTokenProvider;

  @BeforeEach
  void setUp() {
    validationListener =
        new OpenApiValidationListener("src/main/openapi/stub-civil-claims-api.json");
    wireMockServer.addMockServiceRequestListener(validationListener);
    when(oboTokenProvider.getToken(any())).thenReturn("mock-obo-token");
  }

  @Test
  void shouldGetAllClaimsForUser() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/claims?page=0&limit=100")
                .param("status", "SUBMITTED")
                .with(
                    jwt()
                        .jwt(
                            jwt ->
                                jwt.claim("USER_NAME", providerUserId1.toString())
                                    .claim("sub", "jwt"))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.claims", hasSize(11)));
  }

  @Test
  void shouldGetClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    mockMvc
        .perform(
            get("/api/v1/claims/{claimId}", claimId)
                .param("status", "SUBMITTED")
                .with(
                    jwt()
                        .jwt(
                            jwt ->
                                jwt.claim("USER_NAME", providerUserId1.toString())
                                    .claim("sub", "jwt"))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(claimId.toString()))
        .andExpect(jsonPath("$.ufn").value("121120/467"))
        .andExpect(jsonPath("$.client").value("Giordano"))
        .andExpect(jsonPath("$.category").value("Family"))
        .andExpect(jsonPath("$.concluded").value("2025-03-18"))
        .andExpect(jsonPath("$.feeType").value("Escape"))
        .andExpect(jsonPath("$.escaped").value(true))
        .andExpect(jsonPath("$.counselPayment").value("Paid and Reconciled"))
        .andExpect(jsonPath("$.claimed").value(234.56))
        .andExpect(jsonPath("$.lineItems", hasSize(1)))
        .andExpect(jsonPath("$.lineItems[0].id").value("c4c6b98b-3f78-45dc-abdd-869010d57e69"))
        .andExpect(jsonPath("$.lineItems[0].title").value("Interim hearing"))
        .andExpect(jsonPath("$.lineItems[0].category").value("Work Item"))
        .andExpect(jsonPath("$.lineItems[0].date").value("2023-12-20"))
        .andExpect(jsonPath("$.lineItems[0].evidenceItems", hasSize(1)))
        .andExpect(jsonPath("$.lineItems[0].evidenceItems[0]").value("dc2dd276-b747-43fd-bb04-4223e0a70282"))
        .andExpect(jsonPath("$.evidence", hasSize(1)))
        .andExpect(jsonPath("$.evidence[0].id").value("dc2dd276-b747-43fd-bb04-4223e0a70282"))
        .andExpect(jsonPath("$.evidence[0].fileKey").value("test.pdf"))
        .andExpect(jsonPath("$.evidence[0].fileSize").value(1000))
        .andExpect(jsonPath("$.evidence[0].submittedOn").value("2026-06-17T12:51:00.402426Z"));
  }

  @Test
  void shouldCreateClaim() throws Exception {
    String requestBody =
        """
        {
          "ufn": "NEW/999",
          "client": "New Client",
          "category": "Family",
          "concluded": "2025-07-09",
          "feeType": "Hourly",
          "escaped": false,
          "counselPayment": "Paid and Reconciled",
          "claimed": 123.45
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/claims")
                .param("status", "SUBMITTED")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(
                            jwt ->
                                jwt.claim("USER_NAME", providerUserId1.toString())
                                    .claim("sub", "jwt"))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateClaim() throws Exception {
    UUID claimId = UUID.randomUUID();

    String requestBody =
        """
        {
          "ufn": "UPDATED/123",
          "client": "Updated Client",
          "category": "Immigration and Asylum",
          "concluded": "2025-07-10",
          "feeType": "Fixed",
          "escaped": false,
          "counselPayment": "Paid and Reconciled",
          "claimed": 999.99
        }
        """;

    mockMvc
        .perform(
            put("/api/v1/claims/{claimId}", claimId)
                .param("status", "SUBMITTED")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(
                            jwt ->
                                jwt.claim("USER_NAME", providerUserId1.toString())
                                    .claim("sub", "jwt"))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldDeleteClaim() throws Exception {
    mockMvc
        .perform(
            delete("/api/v1/claims/{claimId}", UUID.randomUUID())
                .param("status", "SUBMITTED")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldAddEvidenceToClaim() throws Exception {
    UUID evidenceId = UUID.fromString("a2c7e79a-e292-44de-8924-e64f0f0e45d0");

    MockMultipartFile file =
        new MockMultipartFile(
            "documents", // must match @RequestParam name
            "file1.pdf",
            "application/pdf",
            "Test file 1".getBytes());

    mockMvc
        .perform(
            multipart("/api/v1/claims/{id}/upload-evidence", UUID.randomUUID())
                .file(file)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(String.format("File uploaded with ID: %s", evidenceId)))
        .andExpect(jsonPath("$.file.filename").value("file1.pdf"))
        .andExpect(jsonPath("$.file.originalname").value("file1.pdf"))
        .andExpect(jsonPath("$.file.filesize").value(11))
        .andExpect(jsonPath("$.evidenceId").value(evidenceId.toString()));
  }

  @Test
  void shouldLinkEvidenceToExistingLineItem() throws Exception {
    UUID claimId = UUID.randomUUID();
    UUID lineItemId = UUID.randomUUID();
    UUID evidenceId = UUID.randomUUID();
    String requestBody = String.format("""
        [
          "%s"
        ]
        """, evidenceId);
    mockMvc
        .perform(
            post("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence", claimId, lineItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldLinkMultipleEvidenceToExistingLineItem() throws Exception {
    UUID claimId = UUID.randomUUID();
    UUID lineItemId = UUID.randomUUID();
    UUID evidence1Id = UUID.randomUUID();
    UUID evidence2Id = UUID.randomUUID();
    String requestBody = String.format("""
        [
          "%s",
          "%s"
        ]
        """, evidence1Id, evidence2Id);
    mockMvc
        .perform(
            post("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence", claimId, lineItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldAddEvidenceToLineItem() throws Exception {
    UUID claimId = UUID.randomUUID();
    UUID lineItemId = UUID.randomUUID();
    UUID evidenceId = UUID.fromString("a2c7e79a-e292-44de-8924-e64f0f0e45d0");

    MockMultipartFile file =
        new MockMultipartFile(
            "documents", // must match @RequestParam name
            "file1.pdf",
            "application/pdf",
            "Test file 1".getBytes());

    mockMvc
        .perform(
            multipart("/api/v1/claims/{claimId}/line-items/{lineItemId}/upload-evidence", claimId, lineItemId)
                .file(file)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.message")
                .value(String.format("File uploaded with ID: %s and linked to line item: %s", evidenceId, lineItemId)))
        .andExpect(jsonPath("$.file.filename").value("file1.pdf"))
        .andExpect(jsonPath("$.file.originalname").value("file1.pdf"))
        .andExpect(jsonPath("$.file.filesize").value(11))
        .andExpect(jsonPath("$.evidenceId").value(evidenceId.toString()));
  }

  @Test
  void shouldDeleteEvidenceFromExistingClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    UUID evidenceId = UUID.randomUUID();

    mockMvc
        .perform(
            delete("/api/v1/claims/{claimId}/evidence/{evidenceId}", claimId, evidenceId)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldUnlinkEvidenceFromExistingLineItem() throws Exception {
    UUID claimId = UUID.randomUUID();
    UUID lineItemId = UUID.randomUUID();
    UUID evidenceId = UUID.randomUUID();

    mockMvc
        .perform(
            delete("/api/v1/claims/{claimId}/line-items/{lineItemId}/evidence/{evidenceId}", claimId, lineItemId, evidenceId)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldGetDraftClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    mockMvc
        .perform(
            get("/api/v1/claims/{claimId}", claimId)
                .param("status", "DRAFT")
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(claimId.toString()))
        .andExpect(jsonPath("$.ufn").value("121120/467"))
        .andExpect(jsonPath("$.client").value("Giordano"))
        .andExpect(jsonPath("$.category").value("Family"))
        .andExpect(jsonPath("$.concluded").value("2025-03-18"))
        .andExpect(jsonPath("$.feeType").value("Escape"))
        .andExpect(jsonPath("$.escaped").value(true))
        .andExpect(jsonPath("$.counselPayment").value("Paid and Reconciled"))
        .andExpect(jsonPath("$.claimed").value(234.56))
        .andExpect(jsonPath("$.costType").value("PROFIT_COST"))
        .andExpect(jsonPath("$.courtType").value("COUNTY_COURT"))
        .andExpect(jsonPath("$.clientPartyStatus").value("JOINED_PARTY"))
        .andExpect(jsonPath("$.firstActingSolicitorFlag").value(true))
        .andExpect(jsonPath("$.transferOfSolicitorFlag").value(false))
        .andExpect(jsonPath("$.clientsRetainedCount").value("ZERO"))
        .andExpect(jsonPath("$.clientsStartCount").value("TWO_OR_MORE"))
        .andExpect(jsonPath("$.multiClientHearingFlag").value(true));
  }

  @Test
  void shouldCreateDraftClaim() throws Exception {
    String requestBody =
        """
        {
          "ufn": "NEW/999",
          "client": "New Client",
          "category": "Family",
          "concluded": "2025-07-09",
          "feeType": "Hourly",
          "escaped": false,
          "counselPayment": "Paid and Reconciled",
          "claimed": 123.45,
          "costType": "PROFIT_COST",
          "courtType": "COUNTY_COURT",
          "clientPartyStatus": "JOINED_PARTY",
          "firstActingSolicitorFlag": true,
          "transferOfSolicitorFlag": false,
          "clientsRetainedCount": "ZERO",
          "clientsStartCount": "TWO_OR_MORE",
          "multiClientHearingFlag": true
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/claims")
                .param("status", "DRAFT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .with(
                    jwt()
                        .jwt(
                            jwt ->
                                jwt.claim("USER_NAME", providerUserId1.toString())
                                    .claim("sub", "jwt"))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldAddLineItemToDraftClaim() throws Exception {
    UUID claimId = UUID.randomUUID();
    String requestBody =
        """
            {
              "title": "New Line Item",
              "category": "Work Item",
              "date": "2025-07-11"
            }
            """;

    mockMvc
        .perform(
            post("/api/v1/claims/{claimId}/line-items", claimId)
                .param("status", "DRAFT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(
                    jwt()
                        .jwt(jwt -> jwt.claim("USER_NAME", providerUserId1.toString()))
                        .authorities(() -> "SCOPE_Claims.Write")))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }
}
