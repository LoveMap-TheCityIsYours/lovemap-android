# SmackControllerApi

All URIs are relative to *http://192.168.0.58:8090*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create**](SmackControllerApi.md#create) | **POST** /smack | 
[**list**](SmackControllerApi.md#list) | **GET** /smack/{smackerId} | 


<a name="create"></a>
# **create**
> SmackDto create(createSmackRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackControllerApi()
val createSmackRequest : CreateSmackRequest =  // CreateSmackRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackDto = apiInstance.create(createSmackRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackControllerApi#create")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackControllerApi#create")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createSmackRequest** | [**CreateSmackRequest**](CreateSmackRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackDto**](SmackDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="list"></a>
# **list**
> SmackListDto list(smackerId, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SmackControllerApi()
val smackerId : kotlin.Long = 789 // kotlin.Long | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackListDto = apiInstance.list(smackerId, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SmackControllerApi#list")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SmackControllerApi#list")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smackerId** | **kotlin.Long**|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackListDto**](SmackListDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*

