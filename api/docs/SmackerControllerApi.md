# SmackerControllerApi

All URIs are relative to *http://192.168.0.58:8090*

Method | HTTP request | Description
------------- | ------------- | -------------
[**generateSmackerLink**](SmackerControllerApi.md#generateSmackerLink) | **PUT** /smacker/generateLink | 
[**getSmacker**](SmackerControllerApi.md#getSmacker) | **GET** /smacker/{smackerId} | 
[**getSmackerByLink**](SmackerControllerApi.md#getSmackerByLink) | **GET** /smacker/byLink/ | 
[**test**](SmackerControllerApi.md#test) | **GET** /smacker | 


<a name="generateSmackerLink"></a>
# **generateSmackerLink**
> SmackerLinkDto generateSmackerLink(generateSmackerLinkRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackerControllerApi()
val generateSmackerLinkRequest : GenerateSmackerLinkRequest =  // GenerateSmackerLinkRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerLinkDto = apiInstance.generateSmackerLink(generateSmackerLinkRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackerControllerApi#generateSmackerLink")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackerControllerApi#generateSmackerLink")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **generateSmackerLinkRequest** | [**GenerateSmackerLinkRequest**](GenerateSmackerLinkRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerLinkDto**](SmackerLinkDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="getSmacker"></a>
# **getSmacker**
> SmackerRelationsDto getSmacker(smackerId, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackerControllerApi()
val smackerId : kotlin.Long = 789 // kotlin.Long | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerRelationsDto = apiInstance.getSmacker(smackerId, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackerControllerApi#getSmacker")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackerControllerApi#getSmacker")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smackerId** | **kotlin.Long**|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerRelationsDto**](SmackerRelationsDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

<a name="getSmackerByLink"></a>
# **getSmackerByLink**
> SmackerViewDto getSmackerByLink(smackerLink, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackerControllerApi()
val smackerLink : kotlin.String = smackerLink_example // kotlin.String | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerViewDto = apiInstance.getSmackerByLink(smackerLink, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackerControllerApi#getSmackerByLink")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackerControllerApi#getSmackerByLink")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smackerLink** | **kotlin.String**|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerViewDto**](SmackerViewDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

<a name="test"></a>
# **test**
> kotlin.String test(dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackerControllerApi()
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : kotlin.String = apiInstance.test(dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackerControllerApi#test")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackerControllerApi#test")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

**kotlin.String**

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

