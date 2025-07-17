package uk.gov.justice.laa.claimforpayment.civilclaims.api;

import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;

import uk.gov.justice.laa.claimforpayment.civilclaims.model.Claim;
import uk.gov.justice.laa.claimforpayment.civilclaims.model.ClaimRequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-07-17T11:46:14.708668+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class ClaimsApi {
    private ApiClient apiClient;

    public ClaimsApi() {
        this(new ApiClient());
    }

    public ClaimsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    
    /**
     * Create an claim
     * 
     * <p><b>201</b> - Created
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param claimRequestBody The claimRequestBody parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec createClaimRequestCreation(@jakarta.annotation.Nonnull ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        Object postBody = claimRequestBody;
        // verify the required parameter 'claimRequestBody' is set
        if (claimRequestBody == null) {
            throw new WebClientResponseException("Missing the required parameter 'claimRequestBody' when calling createClaim", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/api/v1/claims", HttpMethod.POST, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Create an claim
     * 
     * <p><b>201</b> - Created
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param claimRequestBody The claimRequestBody parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> createClaim(@jakarta.annotation.Nonnull ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return createClaimRequestCreation(claimRequestBody).bodyToMono(localVarReturnType);
    }

    /**
     * Create an claim
     * 
     * <p><b>201</b> - Created
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param claimRequestBody The claimRequestBody parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> createClaimWithHttpInfo(@jakarta.annotation.Nonnull ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return createClaimRequestCreation(claimRequestBody).toEntity(localVarReturnType);
    }

    /**
     * Create an claim
     * 
     * <p><b>201</b> - Created
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param claimRequestBody The claimRequestBody parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec createClaimWithResponseSpec(@jakarta.annotation.Nonnull ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        return createClaimRequestCreation(claimRequestBody);
    }

    /**
     * Delete claim by id
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be deleted
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec deleteClaimRequestCreation(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling deleteClaim", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/api/v1/claims/{id}", HttpMethod.DELETE, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Delete claim by id
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be deleted
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> deleteClaim(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteClaimRequestCreation(id).bodyToMono(localVarReturnType);
    }

    /**
     * Delete claim by id
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be deleted
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> deleteClaimWithHttpInfo(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return deleteClaimRequestCreation(id).toEntity(localVarReturnType);
    }

    /**
     * Delete claim by id
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be deleted
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec deleteClaimWithResponseSpec(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        return deleteClaimRequestCreation(id);
    }

    /**
     * Get claim by id
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to retrieve
     * @return Claim
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getClaimByIdRequestCreation(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        Object postBody = null;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling getClaimById", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Claim> localVarReturnType = new ParameterizedTypeReference<Claim>() {};
        return apiClient.invokeAPI("/api/v1/claims/{id}", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get claim by id
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to retrieve
     * @return Claim
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Claim> getClaimById(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        ParameterizedTypeReference<Claim> localVarReturnType = new ParameterizedTypeReference<Claim>() {};
        return getClaimByIdRequestCreation(id).bodyToMono(localVarReturnType);
    }

    /**
     * Get claim by id
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to retrieve
     * @return ResponseEntity&lt;Claim&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Claim>> getClaimByIdWithHttpInfo(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        ParameterizedTypeReference<Claim> localVarReturnType = new ParameterizedTypeReference<Claim>() {};
        return getClaimByIdRequestCreation(id).toEntity(localVarReturnType);
    }

    /**
     * Get claim by id
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to retrieve
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getClaimByIdWithResponseSpec(@jakarta.annotation.Nonnull Long id) throws WebClientResponseException {
        return getClaimByIdRequestCreation(id);
    }

    /**
     * Get claims
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @return List&lt;Claim&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec getClaimsRequestCreation() throws WebClientResponseException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Claim> localVarReturnType = new ParameterizedTypeReference<Claim>() {};
        return apiClient.invokeAPI("/api/v1/claims", HttpMethod.GET, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Get claims
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @return List&lt;Claim&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Flux<Claim> getClaims() throws WebClientResponseException {
        ParameterizedTypeReference<Claim> localVarReturnType = new ParameterizedTypeReference<Claim>() {};
        return getClaimsRequestCreation().bodyToFlux(localVarReturnType);
    }

    /**
     * Get claims
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @return ResponseEntity&lt;List&lt;Claim&gt;&gt;
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<List<Claim>>> getClaimsWithHttpInfo() throws WebClientResponseException {
        ParameterizedTypeReference<Claim> localVarReturnType = new ParameterizedTypeReference<Claim>() {};
        return getClaimsRequestCreation().toEntityList(localVarReturnType);
    }

    /**
     * Get claims
     * 
     * <p><b>200</b> - Success
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec getClaimsWithResponseSpec() throws WebClientResponseException {
        return getClaimsRequestCreation();
    }

    /**
     * Update an claim
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be updated
     * @param claimRequestBody The claimRequestBody parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    private ResponseSpec updateClaimRequestCreation(@jakarta.annotation.Nonnull Long id, @jakarta.annotation.Nullable ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        Object postBody = claimRequestBody;
        // verify the required parameter 'id' is set
        if (id == null) {
            throw new WebClientResponseException("Missing the required parameter 'id' when calling updateClaim", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // create path and map variables
        final Map<String, Object> pathParams = new HashMap<String, Object>();

        pathParams.put("id", id);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/api/v1/claims/{id}", HttpMethod.PUT, pathParams, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * Update an claim
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be updated
     * @param claimRequestBody The claimRequestBody parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<Void> updateClaim(@jakarta.annotation.Nonnull Long id, @jakarta.annotation.Nullable ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return updateClaimRequestCreation(id, claimRequestBody).bodyToMono(localVarReturnType);
    }

    /**
     * Update an claim
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be updated
     * @param claimRequestBody The claimRequestBody parameter
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public Mono<ResponseEntity<Void>> updateClaimWithHttpInfo(@jakarta.annotation.Nonnull Long id, @jakarta.annotation.Nullable ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        return updateClaimRequestCreation(id, claimRequestBody).toEntity(localVarReturnType);
    }

    /**
     * Update an claim
     * 
     * <p><b>204</b> - No content
     * <p><b>400</b> - Bad request
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal server error
     * @param id The id of the claim to be updated
     * @param claimRequestBody The claimRequestBody parameter
     * @return ResponseSpec
     * @throws WebClientResponseException if an error occurs while attempting to invoke the API
     */
    public ResponseSpec updateClaimWithResponseSpec(@jakarta.annotation.Nonnull Long id, @jakarta.annotation.Nullable ClaimRequestBody claimRequestBody) throws WebClientResponseException {
        return updateClaimRequestCreation(id, claimRequestBody);
    }
}
