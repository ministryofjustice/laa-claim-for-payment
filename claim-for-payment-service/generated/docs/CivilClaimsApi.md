# CivilClaimsApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createClaim**](CivilClaimsApi.md#createClaim) | **POST** /api/v1/claims | Create a new claim |
| [**deleteClaim**](CivilClaimsApi.md#deleteClaim) | **DELETE** /api/v1/claims/{claimId} | Delete a claim |
| [**getClaim**](CivilClaimsApi.md#getClaim) | **GET** /api/v1/claims/{claimId} | Get a claim by ID |
| [**getClaims**](CivilClaimsApi.md#getClaims) | **GET** /api/v1/claims | Get paged claims for the authenticated user |
| [**updateClaim**](CivilClaimsApi.md#updateClaim) | **PUT** /api/v1/claims/{id} | Update a claim |



## createClaim

> CivilCreateClaimResponse createClaim(civilClaimRequestBody)

Create a new claim

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8080");

        CivilClaimsApi apiInstance = new CivilClaimsApi(defaultClient);
        CivilClaimRequestBody civilClaimRequestBody = new CivilClaimRequestBody(); // CivilClaimRequestBody | 
        try {
            CivilCreateClaimResponse result = apiInstance.createClaim(civilClaimRequestBody);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CivilClaimsApi#createClaim");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **civilClaimRequestBody** | [**CivilClaimRequestBody**](CivilClaimRequestBody.md)|  | |

### Return type

[**CivilCreateClaimResponse**](CivilCreateClaimResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Claim created successfully |  * Location - URI of the created claim resource <br>  |
| **400** | Invalid request body |  -  |


## deleteClaim

> deleteClaim(claimId)

Delete a claim

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8080");

        CivilClaimsApi apiInstance = new CivilClaimsApi(defaultClient);
        Long claimId = 56L; // Long | ID of the claim to delete
        try {
            apiInstance.deleteClaim(claimId);
        } catch (ApiException e) {
            System.err.println("Exception when calling CivilClaimsApi#deleteClaim");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **claimId** | **Long**| ID of the claim to delete | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Claim deleted successfully |  -  |
| **404** | Claim not found |  -  |


## getClaim

> CivilClaim getClaim(claimId)

Get a claim by ID

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8080");

        CivilClaimsApi apiInstance = new CivilClaimsApi(defaultClient);
        Long claimId = 56L; // Long | ID of the claim to retrieve
        try {
            CivilClaim result = apiInstance.getClaim(claimId);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CivilClaimsApi#getClaim");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **claimId** | **Long**| ID of the claim to retrieve | |

### Return type

[**CivilClaim**](CivilClaim.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Claim found |  -  |
| **404** | Claim not found |  -  |


## getClaims

> CivilClaimPageResponse getClaims(page, limit)

Get paged claims for the authenticated user

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8080");

        CivilClaimsApi apiInstance = new CivilClaimsApi(defaultClient);
        Integer page = 0; // Integer | 
        Integer limit = 20; // Integer | 
        try {
            CivilClaimPageResponse result = apiInstance.getClaims(page, limit);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CivilClaimsApi#getClaims");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **page** | **Integer**|  | [optional] [default to 0] |
| **limit** | **Integer**|  | [optional] [default to 20] |

### Return type

[**CivilClaimPageResponse**](CivilClaimPageResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Paged list of claims linked to a provider user |  -  |


## updateClaim

> updateClaim(id, civilClaimRequestBody)

Update a claim

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.CivilClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost:8080");

        CivilClaimsApi apiInstance = new CivilClaimsApi(defaultClient);
        Long id = 56L; // Long | ID of the claim to update
        CivilClaimRequestBody civilClaimRequestBody = new CivilClaimRequestBody(); // CivilClaimRequestBody | 
        try {
            apiInstance.updateClaim(id, civilClaimRequestBody);
        } catch (ApiException e) {
            System.err.println("Exception when calling CivilClaimsApi#updateClaim");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**| ID of the claim to update | |
| **civilClaimRequestBody** | [**CivilClaimRequestBody**](CivilClaimRequestBody.md)|  | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Claim updated successfully |  -  |
| **404** | Claim not found |  -  |

