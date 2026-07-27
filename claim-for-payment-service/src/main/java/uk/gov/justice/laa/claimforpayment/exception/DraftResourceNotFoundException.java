package uk.gov.justice.laa.claimforpayment.exception;

import java.util.UUID;

/**
 * Thrown when a requested resource cannot be found within a deserialised draft claim payload.
 *
 * <p>This exception is intended for resources contained within the draft payload itself
 * and is mapped to an HTTP 404 response by the global exception handler.
 */
public class DraftResourceNotFoundException extends RuntimeException {

  public DraftResourceNotFoundException(String resourceType, UUID resourceId) {
    super("%s %s not found in draft claim payload"
        .formatted(resourceType, resourceId));
  }
}