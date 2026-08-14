package uk.gov.justice.laa.claimforpayment.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.claimforpayment.access.api.AccessApplicationApi;
import uk.gov.justice.laa.claimforpayment.access.model.AccessApplicationResponse;
import uk.gov.justice.laa.claimforpayment.model.CaseDto;

/** Service class for retrieving case details from the Access API. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccessDataService {

  private final AccessApplicationApi accessApplicationApi;

  /**
   * Retrieves case details for the given case ID.
   *
   * @param caseId the case ID
   * @return the case details
   */
  public CaseDto getCase(UUID caseId) {
    AccessApplicationResponse application = accessApplicationApi.getApplicationById(null, caseId);
    CaseDto caseDto =
        CaseDto.builder()
            .applicationId(application.getApplicationId())
            .decisionStatus(application.getDecisionStatus())
            .build();

    return caseDto;
  }
}
