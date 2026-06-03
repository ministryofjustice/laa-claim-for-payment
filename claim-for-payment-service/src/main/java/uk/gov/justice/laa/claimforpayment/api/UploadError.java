package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Contains details of an upload error. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UploadError(UploadFile file, String message) implements UploadResponse {}
