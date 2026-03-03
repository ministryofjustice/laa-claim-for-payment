package uk.gov.justice.laa.claimforpayment.model;

import java.util.List;

/**
 * A record representing a page of claims with pagination information.
 *
 * @param claims the list of claims on this page
 * @param page the current page number
 * @param limit the maximum number of claims per page
 * @param totalElements the total number of claims
 * @param totalPages the total number of pages
 */
public record  ClaimPage(
    List<Claim> claims, int page, int limit, long totalElements, int totalPages) {
  public static ClaimPage empty(int page, int limit) {
    return new ClaimPage(List.of(), page, limit, 0, 0);
  }
}
