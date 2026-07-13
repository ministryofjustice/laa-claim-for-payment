package uk.gov.justice.laa.claimforpayment.api;

import org.springframework.web.multipart.MultipartFile;

/** File information for upload operations. */
public record UploadFile(String filename, String originalname, Long filesize) {

  public UploadFile(MultipartFile file) {
    this(file.getOriginalFilename(), file.getSize());
  }

  public UploadFile(String filename, Long filesize) {
    this(filename, filename, filesize);
  }
}
