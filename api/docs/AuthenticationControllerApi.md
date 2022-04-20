# AuthenticationControllerApi

All URIs are relative to *http://192.168.0.58:8090*

Method | HTTP request | Description
------------- | ------------- | -------------
[**login**](AuthenticationControllerApi.md#login) | **POST** /authentication/login | 
[**register**](AuthenticationControllerApi.md#register) | **POST** /authentication/register | 


<a name="login"></a>
# **login**
> SmackerRelationsDto login(loginSmackerRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = AuthenticationControllerApi()
val loginSmackerRequest : LoginSmackerRequest =  // LoginSmackerRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerRelationsDto = apiInstance.login(loginSmackerRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuthenticationControllerApi#login")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuthenticationControllerApi#login")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **loginSmackerRequest** | [**LoginSmackerRequest**](LoginSmackerRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerRelationsDto**](SmackerRelationsDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="register"></a>
# **register**
> SmackerDto register(createSmackerRequest, dollarCompletion)



### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = AuthenticationControllerApi()
val createSmackerRequest : CreateSmackerRequest =  // CreateSmackerRequest | 
val dollarCompletion : ContinuationObject =  // ContinuationObject | 
try {
    val result : SmackerDto = apiInstance.register(createSmackerRequest, dollarCompletion)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling AuthenticationControllerApi#register")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling AuthenticationControllerApi#register")
    e.printStackTrace()
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createSmackerRequest** | [**CreateSmackerRequest**](CreateSmackerRequest.md)|  |
 **dollarCompletion** | [**ContinuationObject**](.md)|  | [optional]

### Return type

[**SmackerDto**](SmackerDto.md)

### Authorization


Configure bearerAuth:
    ApiClient.accessToken = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

