package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;

/**
 * This class provides a method to deserialise a raw API response into a Claim object. The
 * deserialisation process involves parsing the outer JSON wrapper, extracting the inner JSON
 * string, and then converting that string into a Claim instance.
 */
public class DraftClaimPayloadDeserializer {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModule(new JavaTimeModule());
  }

  /**
   * Deserialises a raw API response into a Claim object.
   *
   * @param civilDraftClaim from the api
   * @return Deserialised Claim object
   */
  public static Claim deserialise(CivilDraftClaim civilDraftClaim) {
    return MAPPER.convertValue(civilDraftClaim.getPayload(), Claim.class);
  }

  /**
   * Serialises a ClaimRequestBody into a Map.
   *
   * @param requestBody from the request
   * @return Serialised Claim object
   */
  public static Map<String, Object> serialise(ClaimRequestBody requestBody) {
    return MAPPER.convertValue(requestBody, new TypeReference<>() {});
  }
}
