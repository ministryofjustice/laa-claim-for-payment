package uk.gov.justice.laa.claimforpayment.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Response for upload operations. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed interface UploadResponse permits UploadSuccess, UploadError {

  UploadFile file();

  String message();
}
