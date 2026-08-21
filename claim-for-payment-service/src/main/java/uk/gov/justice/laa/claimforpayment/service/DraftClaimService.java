package uk.gov.justice.laa.claimforpayment.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.justice.laa.claimforpayment.api.DraftClaimPayloadDeserializer;
import uk.gov.justice.laa.claimforpayment.api.UploadFile;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilDraftClaimsApi;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateDraftClaimResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPatch;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilDraftClaimPost;
import uk.gov.justice.laa.claimforpayment.exception.DraftResourceNotFoundException;
import uk.gov.justice.laa.claimforpayment.exception.UpstreamServiceException;
import uk.gov.justice.laa.claimforpayment.model.Claim;
import uk.gov.justice.laa.claimforpayment.model.ClaimEvidence;
import uk.gov.justice.laa.claimforpayment.model.ClaimPage;
import uk.gov.justice.laa.claimforpayment.model.ClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.model.LineItem;
import uk.gov.justice.laa.claimforpayment.model.LineItemRequestBody;

/**
 * Service class for managing draft claims operations. Handles retrieval, creation, update, and
 * deletion of draft claims from the Civil Claims API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DraftClaimService implements ClaimServiceInterface {

  private final CivilDraftClaimsApi civilDraftClaimsApi;

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public UUID addEvidenceToClaim(UUID claimId, UploadFile uploadFile) {
    Claim claim = getClaim(claimId);
    ClaimEvidence claimEvidence =
        ClaimEvidence.builder()
            .id(generateUuid7())
            .fileKey(uploadFile.filename())
            .fileSize(uploadFile.filesize())
            .submittedOn(Instant.now())
            .build();
    Optional.ofNullable(claim.getEvidence())
        .ifPresentOrElse(
            evidenceList -> evidenceList.add(claimEvidence),
            () -> claim.setEvidence(new ArrayList<>(List.of(claimEvidence))));

    patchDraftClaim(claimId, claim);
    return claimEvidence.getId();
  }

  @Override
  public UUID createClaim(ClaimRequestBody claimRequestBody, UUID providerUserId) {
    CivilDraftClaimPost body = new CivilDraftClaimPost();
    UUID claimId = generateUuid7();
    body.setId(claimId);
    body.setProviderUserId(providerUserId);
    body.setPayload(
        DraftClaimPayloadDeserializer.serialise(claimRequestBody, providerUserId, claimId));
    CivilCreateDraftClaimResponse response =
        executeCivilClaimsApi(
            () -> civilDraftClaimsApi.createDraftClaim(body), "POST /api/v1/drafts/");

    return response.getId();
  }

  @Override
  public void deleteClaim(UUID id) {
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.deleteDraftClaim(id);
          return null;
        },
        "DELETE /api/v1/drafts/{claimId}");
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public void deleteEvidenceFromClaim(UUID claimId, UUID evidenceId) {
    Claim claim = getClaim(claimId);
    ClaimEvidence claimEvidence = getClaimEvidenceOrThrow(claim, evidenceId);
    claim.getEvidence().remove(claimEvidence);

    CivilDraftClaimPatch civilDraftClaimPatch = new CivilDraftClaimPatch();
    civilDraftClaimPatch.setPayload(DraftClaimPayloadDeserializer.serialise(claim, claimId));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.patchDraftClaim(
              claimId, String.valueOf(claim.getVersion()), civilDraftClaimPatch);
          return null;
        },
        "PATCH /api/v1/drafts/{claimId}");
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public void deleteAllEvidenceFromClaim(UUID claimId) {
    Claim claim = getClaim(claimId);
    claim.setEvidence(new ArrayList<>());

    patchDraftClaim(claimId, claim);
  }

  @Override
  public Claim getClaim(UUID claimId) {
    CivilDraftClaim draftClaim =
        executeCivilClaimsApi(
            () -> civilDraftClaimsApi.getDraftClaim(claimId), "GET /api/v1/drafts/{claimId}");

    try {
      var deserialisedClaim = DraftClaimPayloadDeserializer.deserialise(draftClaim);
      deserialisedClaim.setVersion(draftClaim.getVersion());
      return deserialisedClaim;
    } catch (Exception e) {
      throw new UpstreamServiceException("Draft Claims API", "call", e);
    }
  }

  @Override
  public ClaimPage getClaims(int page, int limit) {
    CivilDraftClaimPageResponse response =
        executeCivilClaimsApi(
            () -> civilDraftClaimsApi.getDraftClaims(page, limit), "GET /api/v1/drafts");
    if (response == null) {
      return ClaimPage.empty(page, limit);
    }

    if (response.getDraftClaims() == null
        || response.getTotal() == null
        || response.getTotalPages() == null) {
      throw new IllegalStateException("Civil claims API returned an incomplete response");
    }

    List<Claim> claims =
        response.getDraftClaims().stream().map(DraftClaimPayloadDeserializer::deserialise).toList();
    return new ClaimPage(claims, page, limit, response.getTotal(), response.getTotalPages());
  }

  @Override
  public void linkEvidenceToLineItem(UUID claimId, UUID lineItemId, List<UUID> evidenceIds) {
    // TODO Auto-generated method stub, Not required by POA.
    throw new UnsupportedOperationException("Unimplemented method 'linkEvidenceToLineItem'");
  }

  @Override
  public void unlinkEvidenceFromLineItem(UUID claimId, UUID lineItemId, UUID evidenceId) {
    // TODO Auto-generated method stub. Not required by POA.
    throw new UnsupportedOperationException("Unimplemented method 'unlinkEvidenceFromLineItem'");
  }

  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  @Override
  public void updateClaim(UUID id, ClaimRequestBody claimRequestBody, UUID providerUserId) {
    Claim claim = getClaim(id);
    Map<String, Object> serialisedPayload =
            DraftClaimPayloadDeserializer.serialise(claimRequestBody, providerUserId, id);

    serialisedPayload.put("evidence", claim.getEvidence());
    serialisedPayload.put("lineItems", claim.getLineItems());

    CivilDraftClaimPatch civilDraftClaimPatch = new CivilDraftClaimPatch();
    civilDraftClaimPatch.setPayload(serialisedPayload);
    executeCivilClaimsApi(
            () -> {
              civilDraftClaimsApi.patchDraftClaim(
                      id, String.valueOf(claim.getVersion()), civilDraftClaimPatch);
              return null;
            },
            "PATCH /api/v1/drafts/{claimId}");
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public UUID addLineItemToClaim(UUID claimId, LineItemRequestBody lineItemRequestBody) {
    LineItem lineItem =
        LineItem.builder()
            .id(generateUuid7())
            .title(lineItemRequestBody.getTitle())
            .category(lineItemRequestBody.getCategory())
            .date(lineItemRequestBody.getDate())
            .actualNetValue(lineItemRequestBody.getActualNetValue())
            .netProfitCostAmount(lineItemRequestBody.getNetProfitCostAmount())
            .netAdvocacyCostAmount(lineItemRequestBody.getNetAdvocacyCostAmount())
            .vatApplicable(lineItemRequestBody.getVatApplicable())
            .feeEarnerName(lineItemRequestBody.getFeeEarnerName())
            .build();

    Claim claim = getClaim(claimId);
    if (claim.getLineItems() == null) {
      claim.setLineItems(new ArrayList<>());
    }
    claim.getLineItems().add(lineItem);

    patchDraftClaim(claimId, claim);

    return lineItem.getId();
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public void updateLineItem(
      UUID claimId, UUID lineItemId, LineItemRequestBody lineItemRequestBody) {
    Claim claim = getClaim(claimId);
    LineItem lineItemToUpdate = getLineItemOrThrow(claim, lineItemId);

    lineItemToUpdate.setTitle(lineItemRequestBody.getTitle());
    lineItemToUpdate.setCategory(lineItemRequestBody.getCategory());
    lineItemToUpdate.setDate(lineItemRequestBody.getDate());
    lineItemToUpdate.setActualNetValue(lineItemRequestBody.getActualNetValue());
    lineItemToUpdate.setNetProfitCostAmount(lineItemRequestBody.getNetProfitCostAmount());
    lineItemToUpdate.setNetAdvocacyCostAmount(lineItemRequestBody.getNetAdvocacyCostAmount());
    lineItemToUpdate.setVatApplicable(lineItemRequestBody.getVatApplicable());
    lineItemToUpdate.setFeeEarnerName(lineItemRequestBody.getFeeEarnerName());

    CivilDraftClaimPatch civilDraftClaimPatch = new CivilDraftClaimPatch();
    civilDraftClaimPatch.setPayload(DraftClaimPayloadDeserializer.serialise(claim, claimId));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.patchDraftClaim(
              claimId, String.valueOf(claim.getVersion()), civilDraftClaimPatch);
          return null;
        },
        "PATCH /api/v1/drafts/{claimId}");
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public void deleteLineItem(UUID claimId, UUID lineItemId) {
    Claim claim = getClaim(claimId);
    LineItem lineItem = getLineItemOrThrow(claim, lineItemId);
    claim.getLineItems().remove(lineItem);

    CivilDraftClaimPatch civilDraftClaimPatch = new CivilDraftClaimPatch();
    civilDraftClaimPatch.setPayload(DraftClaimPayloadDeserializer.serialise(claim, claimId));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.patchDraftClaim(
              claimId, String.valueOf(claim.getVersion()), civilDraftClaimPatch);
          return null;
        },
        "PATCH /api/v1/drafts/{claimId}");
  }

  @Override
  @Retryable(
      retryFor = HttpClientErrorException.Conflict.class,
      backoff = @Backoff(delay = 100, maxDelay = 500, multiplier = 2.0))
  public void deleteAllLineItemsFromClaim(UUID claimId) {
    Claim claim = getClaim(claimId);
    claim.setLineItems(new ArrayList<>());

    patchDraftClaim(claimId, claim);
  }

  private void patchDraftClaim(UUID claimId, Claim claim) {
    CivilDraftClaimPatch civilDraftClaimPatch = new CivilDraftClaimPatch();
    civilDraftClaimPatch.setPayload(DraftClaimPayloadDeserializer.serialise(claim, claimId));
    executeCivilClaimsApi(
        () -> {
          civilDraftClaimsApi.patchDraftClaim(
              claimId, String.valueOf(claim.getVersion()), civilDraftClaimPatch);
          return null;
        },
        "PATCH /api/v1/drafts/{claimId}");
  }

  private LineItem getLineItemOrThrow(Claim claim, UUID lineItemId) {
    return claim.getLineItems().stream()
        .filter(lineItem -> lineItem.getId().equals(lineItemId))
        .findFirst()
        .orElseThrow(() -> new DraftResourceNotFoundException("LineItem", lineItemId));
  }

  private ClaimEvidence getClaimEvidenceOrThrow(Claim claim, UUID evidenceId) {
    return claim.getEvidence().stream()
        .filter(lineItem -> lineItem.getId().equals(evidenceId))
        .findFirst()
        .orElseThrow(() -> new DraftResourceNotFoundException("ClaimEvidence", evidenceId));
  }
}
