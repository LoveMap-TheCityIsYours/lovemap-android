# SmackLocationControllerApi

All URIs are relative to *http://192.168.0.58:8090*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create1**](SmackLocationControllerApi.md#create1) | **POST** /smack/location | 
[**reviewLocation**](SmackLocationControllerApi.md#reviewLocation) | **POST** /smack/location/review | 
[**search**](SmackLocationControllerApi.md#search) | **POST** /smack/location/search | 


<a name="create1"></a>
# **create1**
> SmackLocationDto create1(createSmackLocationRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackLocationControllerApi()
val createSmackLocationRequest : CreateSmackLocationRequest =  // CreateSmackLocationRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackLocationDto = apiInstance.create1(createSmackLocationRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackLocationControllerApi#create1")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackLocationControllerApi#create1")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createSmackLocationRequest** | [**CreateSmackLocationRequest**](CreateSmackLocationRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackLocationDto**](SmackLocationDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="reviewLocation"></a>
# **reviewLocation**
> SmackLocationDto reviewLocation(smackLocationReviewRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackLocationControllerApi()
val smackLocationReviewRequest : SmackLocationReviewRequest =  // SmackLocationReviewRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackLocationDto = apiInstance.reviewLocation(smackLocationReviewRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackLocationControllerApi#reviewLocation")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackLocationControllerApi#reviewLocation")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smackLocationReviewRequest** | [**SmackLocationReviewRequest**](SmackLocationReviewRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackLocationDto**](SmackLocationDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="search"></a>
# **search**
> kotlin.Any search(smackLocationSearchRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackLocationControllerApi()
val smackLocationSearchRequest : SmackLocationSearchRequest =  // SmackLocationSearchRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : kotlin.Any = apiInstance.search(smackLocationSearchRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackLocationControllerApi#search")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackLocationControllerApi#search")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smackLocationSearchRequest** | [**SmackLocationSearchRequest**](SmackLocationSearchRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**kotlin.Any**](kotlin.Any.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

