package uk.gov.justice.laa.claimforpayment.civilclaims.api;

import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.BaseApi;

import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimPageResponse;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilClaimRequestBody;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.CivilCreateClaimResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-02-27T13:39:05.754461Z[Europe/London]", comments = "Generator version: 7.16.0")
public class CivilClaimsApi extends BaseApi {

    public CivilClaimsApi() {
        super(new ApiClient());
    }

    public CivilClaimsApi(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Create a new claim
     * 
     * <p><b>201</b> - Claim created successfully
     * <p><b>400</b> - Invalid request body
     * @param civilClaimRequestBody  (required)
     * @return CivilCreateClaimResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public CivilCreateClaimResponse createClaim(CivilClaimRequestBody civilClaimRequestBody) throws RestClientException {
        return createClaimWithHttpInfo(civilClaimRequestBody).getBody();
    }

    /**
     * Create a new claim
     * 
     * <p><b>201</b> - Claim created successfully
     * <p><b>400</b> - Invalid request body
     * @param civilClaimRequestBody  (required)
     * @return ResponseEntity&lt;CivilCreateClaimResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CivilCreateClaimResponse> createClaimWithHttpInfo(CivilClaimRequestBody civilClaimRequestBody) throws RestClientException {
        Object localVarPostBody = civilClaimRequestBody;
        
        // verify the required parameter 'civilClaimRequestBody' is set
        if (civilClaimRequestBody == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'civilClaimRequestBody' when calling createClaim");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<CivilCreateClaimResponse> localReturnType = new ParameterizedTypeReference<CivilCreateClaimResponse>() {};
        return apiClient.invokeAPI("/api/v1/claims", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Delete a claim
     * 
     * <p><b>204</b> - Claim deleted successfully
     * <p><b>404</b> - Claim not found
     * @param claimId ID of the claim to delete (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteClaim(Long claimId) throws RestClientException {
        deleteClaimWithHttpInfo(claimId);
    }

    /**
     * Delete a claim
     * 
     * <p><b>204</b> - Claim deleted successfully
     * <p><b>404</b> - Claim not found
     * @param claimId ID of the claim to delete (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> deleteClaimWithHttpInfo(Long claimId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'claimId' is set
        if (claimId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'claimId' when calling deleteClaim");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("claimId", claimId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/api/v1/claims/{claimId}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Get a claim by ID
     * 
     * <p><b>200</b> - Claim found
     * <p><b>404</b> - Claim not found
     * @param claimId ID of the claim to retrieve (required)
     * @return CivilClaim
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public CivilClaim getClaim(Long claimId) throws RestClientException {
        return getClaimWithHttpInfo(claimId).getBody();
    }

    /**
     * Get a claim by ID
     * 
     * <p><b>200</b> - Claim found
     * <p><b>404</b> - Claim not found
     * @param claimId ID of the claim to retrieve (required)
     * @return ResponseEntity&lt;CivilClaim&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CivilClaim> getClaimWithHttpInfo(Long claimId) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'claimId' is set
        if (claimId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'claimId' when calling getClaim");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("claimId", claimId);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<CivilClaim> localReturnType = new ParameterizedTypeReference<CivilClaim>() {};
        return apiClient.invokeAPI("/api/v1/claims/{claimId}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Get paged claims for the authenticated user
     * 
     * <p><b>200</b> - Paged list of claims linked to a provider user
     * @param page  (optional, default to 0)
     * @param limit  (optional, default to 20)
     * @return CivilClaimPageResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public CivilClaimPageResponse getClaims(Integer page, Integer limit) throws RestClientException {
        return getClaimsWithHttpInfo(page, limit).getBody();
    }

    /**
     * Get paged claims for the authenticated user
     * 
     * <p><b>200</b> - Paged list of claims linked to a provider user
     * @param page  (optional, default to 0)
     * @param limit  (optional, default to 20)
     * @return ResponseEntity&lt;CivilClaimPageResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CivilClaimPageResponse> getClaimsWithHttpInfo(Integer page, Integer limit) throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "limit", limit));
        

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<CivilClaimPageResponse> localReturnType = new ParameterizedTypeReference<CivilClaimPageResponse>() {};
        return apiClient.invokeAPI("/api/v1/claims", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Update a claim
     * 
     * <p><b>204</b> - Claim updated successfully
     * <p><b>404</b> - Claim not found
     * @param id ID of the claim to update (required)
     * @param civilClaimRequestBody  (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void updateClaim(Long id, CivilClaimRequestBody civilClaimRequestBody) throws RestClientException {
        updateClaimWithHttpInfo(id, civilClaimRequestBody);
    }

    /**
     * Update a claim
     * 
     * <p><b>204</b> - Claim updated successfully
     * <p><b>404</b> - Claim not found
     * @param id ID of the claim to update (required)
     * @param civilClaimRequestBody  (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> updateClaimWithHttpInfo(Long id, CivilClaimRequestBody civilClaimRequestBody) throws RestClientException {
        Object localVarPostBody = civilClaimRequestBody;
        
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'id' when calling updateClaim");
        }
        
        // verify the required parameter 'civilClaimRequestBody' is set
        if (civilClaimRequestBody == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'civilClaimRequestBody' when calling updateClaim");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", id);

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/api/v1/claims/{id}", HttpMethod.PUT, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }

    @Override
    public <T> ResponseEntity<T> invokeAPI(String url, HttpMethod method, Object request, ParameterizedTypeReference<T> returnType) throws RestClientException {
        String localVarPath = url.replace(apiClient.getBasePath(), "");
        Object localVarPostBody = request;

        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        return apiClient.invokeAPI(localVarPath, method, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, returnType);
    }
}
