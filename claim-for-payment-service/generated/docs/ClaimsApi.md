# ClaimsApi

All URIs are relative to *https://laa-data-stewardship-payments-dev.apps.live.cloud-platform.service.justice.gov.uk*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createClaim**](ClaimsApi.md#createClaim) | **POST** /api/v1/claims | Create an claim |
| [**deleteClaim**](ClaimsApi.md#deleteClaim) | **DELETE** /api/v1/claims/{id} | Delete claim by id |
| [**getClaimById**](ClaimsApi.md#getClaimById) | **GET** /api/v1/claims/{id} | Get claim by id |
| [**getClaims**](ClaimsApi.md#getClaims) | **GET** /api/v1/claims | Get claims |
| [**updateClaim**](ClaimsApi.md#updateClaim) | **PUT** /api/v1/claims/{id} | Update an claim |



## createClaim

> createClaim(claimRequestBody)

Create an claim

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://laa-data-stewardship-payments-dev.apps.live.cloud-platform.service.justice.gov.uk");

        ClaimsApi apiInstance = new ClaimsApi(defaultClient);
        ClaimRequestBody claimRequestBody = new ClaimRequestBody(); // ClaimRequestBody | 
        try {
            apiInstance.createClaim(claimRequestBody);
        } catch (ApiException e) {
            System.err.println("Exception when calling ClaimsApi#createClaim");
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
| **claimRequestBody** | [**ClaimRequestBody**](ClaimRequestBody.md)|  | |

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
| **201** | Created |  -  |
| **400** | Bad request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not found |  -  |
| **500** | Internal server error |  -  |


## deleteClaim

> deleteClaim(id)

Delete claim by id

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://laa-data-stewardship-payments-dev.apps.live.cloud-platform.service.justice.gov.uk");

        ClaimsApi apiInstance = new ClaimsApi(defaultClient);
        Long id = 56L; // Long | The id of the claim to be deleted
        try {
            apiInstance.deleteClaim(id);
        } catch (ApiException e) {
            System.err.println("Exception when calling ClaimsApi#deleteClaim");
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
| **id** | **Long**| The id of the claim to be deleted | |

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
| **204** | No content |  -  |
| **400** | Bad request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not found |  -  |
| **500** | Internal server error |  -  |


## getClaimById

> Claim getClaimById(id)

Get claim by id

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://laa-data-stewardship-payments-dev.apps.live.cloud-platform.service.justice.gov.uk");

        ClaimsApi apiInstance = new ClaimsApi(defaultClient);
        Long id = 56L; // Long | The id of the claim to retrieve
        try {
            Claim result = apiInstance.getClaimById(id);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ClaimsApi#getClaimById");
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
| **id** | **Long**| The id of the claim to retrieve | |

### Return type

[**Claim**](Claim.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Bad request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not found |  -  |
| **500** | Internal server error |  -  |


## getClaims

> List&lt;Claim&gt; getClaims()

Get claims

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://laa-data-stewardship-payments-dev.apps.live.cloud-platform.service.justice.gov.uk");

        ClaimsApi apiInstance = new ClaimsApi(defaultClient);
        try {
            List<Claim> result = apiInstance.getClaims();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ClaimsApi#getClaims");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**List&lt;Claim&gt;**](Claim.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Bad request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not found |  -  |
| **500** | Internal server error |  -  |


## updateClaim

> updateClaim(id, claimRequestBody)

Update an claim

### Example

```java
// Import classes:
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiClient;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.ApiException;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.Configuration;
import uk.gov.justice.laa.claimforpayment.civilclaims.invoker.models.*;
import uk.gov.justice.laa.claimforpayment.civilclaims.api.ClaimsApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://laa-data-stewardship-payments-dev.apps.live.cloud-platform.service.justice.gov.uk");

        ClaimsApi apiInstance = new ClaimsApi(defaultClient);
        Long id = 56L; // Long | The id of the claim to be updated
        ClaimRequestBody claimRequestBody = new ClaimRequestBody(); // ClaimRequestBody | 
        try {
            apiInstance.updateClaim(id, claimRequestBody);
        } catch (ApiException e) {
            System.err.println("Exception when calling ClaimsApi#updateClaim");
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
| **id** | **Long**| The id of the claim to be updated | |
| **claimRequestBody** | [**ClaimRequestBody**](ClaimRequestBody.md)|  | [optional] |

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
| **204** | No content |  -  |
| **400** | Bad request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Forbidden |  -  |
| **404** | Not found |  -  |
| **500** | Internal server error |  -  |

