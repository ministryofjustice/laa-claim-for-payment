package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.model.Claim;

/**
 * This class provides a method to deserialise a raw API response into a Claim object. The
 * deserialisation process involves parsing the outer JSON wrapper, extracting the inner JSON
 * string, and then converting that string into a Claim instance.
 */
public class DraftClaimPayloadDeserializer {
  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Deserialises a raw API response into a Claim object.
   *
   * @param civilDraftClaim from the api
   * @return Deserialised Claim object
   * @throws Exception if there is an error during deserialisation
   */
  public static Claim deserialise(CivilDraftClaim civilDraftClaim) throws Exception {

    String innerJson = civilDraftClaim.getPayload();
    mapper.registerModule(new JavaTimeModule());

    return mapper.readValue(innerJson, Claim.class);
  }
}
