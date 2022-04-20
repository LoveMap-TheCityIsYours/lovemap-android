# PartnershipControllerApi

All URIs are relative to *http://192.168.0.58:8090*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getSmackerPartnerships**](PartnershipControllerApi.md#getSmackerPartnerships) | **GET** /partnership | 
[**requestPartnership**](PartnershipControllerApi.md#requestPartnership) | **PUT** /partnership/requestPartnership | 
[**respondPartnership**](PartnershipControllerApi.md#respondPartnership) | **PUT** /partnership/respondPartnership | 


<a name="getSmackerPartnerships"></a>
# **getSmackerPartnerships**
> SmackerPartnershipsResponse getSmackerPartnerships(smackerId, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = PartnershipControllerApi()
val smackerId : kotlin.Long = 789 // kotlin.Long | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerPartnershipsResponse = apiInstance.getSmackerPartnerships(smackerId, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling PartnershipControllerApi#getSmackerPartnerships")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling PartnershipControllerApi#getSmackerPartnerships")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smackerId** | **kotlin.Long**|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerPartnershipsResponse**](SmackerPartnershipsResponse.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

<a name="requestPartnership"></a>
# **requestPartnership**
> PartnershipResponse requestPartnership(requestPartnershipRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = PartnershipControllerApi()
val requestPartnershipRequest : RequestPartnershipRequest =  // RequestPartnershipRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : PartnershipResponse = apiInstance.requestPartnership(requestPartnershipRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling PartnershipControllerApi#requestPartnership")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling PartnershipControllerApi#requestPartnership")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **requestPartnershipRequest** | [**RequestPartnershipRequest**](RequestPartnershipRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**PartnershipResponse**](PartnershipResponse.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="respondPartnership"></a>
# **respondPartnership**
> SmackerPartnershipsResponse respondPartnership(respondPartnershipRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = PartnershipControllerApi()
val respondPartnershipRequest : RespondPartnershipRequest =  // RespondPartnershipRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerPartnershipsResponse = apiInstance.respondPartnership(respondPartnershipRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling PartnershipControllerApi#respondPartnership")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling PartnershipControllerApi#respondPartnership")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **respondPartnershipRequest** | [**RespondPartnershipRequest**](RespondPartnershipRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerPartnershipsResponse**](SmackerPartnershipsResponse.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

