package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Success response for upload operations. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UploadSuccess(
    Long evidenceId,
    UploadFile file,
    String message)
    implements UploadResponse {}
