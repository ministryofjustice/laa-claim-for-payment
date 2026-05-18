package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Response for upload operations. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UploadResponse(UploadSuccess success, UploadError error, UploadFile file) {}
