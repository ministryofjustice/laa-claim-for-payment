package uk.gov.justice.laa.claimforpayment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.claimforpayment.access.api.AccessApplicationApi;
import uk.gov.justice.laa.claimforpayment.access.model.AccessApplicationResponse;
import uk.gov.justice.laa.claimforpayment.model.CaseDto;

/**
 * AccessDataServiceTest.
 */
@ExtendWith(MockitoExtension.class)
public class AccessDataServiceTest {

  @Mock private AccessApplicationApi mockAccessApplicationApi;

  @InjectMocks private AccessDataService accessDataService;

  @Test
  @DisplayName("Test to verify that the getCase method retrieves case details correctly.")
  public void shouldGetCaseDetails() {

    UUID caseId = UUID.randomUUID();
    AccessApplicationResponse mockResponse = new AccessApplicationResponse();
    mockResponse.setApplicationId(caseId);
    mockResponse.setDecisionStatus(AccessApplicationResponse.DecisionStatusEnum.GRANTED);
    when(mockAccessApplicationApi.getApplicationById(null, caseId)).thenReturn(mockResponse);

    CaseDto caseDto = accessDataService.getCase(caseId);

    assertThat(caseDto.getApplicationId()).isEqualTo(caseId);
    assertThat(caseDto.getDecisionStatus()).isEqualTo(AccessApplicationResponse.DecisionStatusEnum.GRANTED);
  }
}
