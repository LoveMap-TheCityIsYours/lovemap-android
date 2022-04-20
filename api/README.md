# org.openapitools.client - Kotlin client library for Smackmap API

## Requires

* Kotlin 1.4.30
* Gradle 6.8.3

## Build

First, create the gradle wrapper script:

```
gradle wrapper
```

Then, run:

```
./gradlew check assemble
```

This runs all tests and packages the library.

## Features/Implementation Notes

* Supports JSON inputs/outputs, File inputs, and Form inputs.
* Supports collection formats for query parameters: csv, tsv, ssv, pipes.
* Some Kotlin and Java types are fully qualified to avoid conflicts with types defined in OpenAPI definitions.
* Implementation of ApiClient is intended to reduce method counts, specifically to benefit Android targets.

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://192.168.0.58:8090*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AuthenticationControllerApi* | [**login**](docs/AuthenticationControllerApi.md#login) | **POST** /authentication/login | 
*AuthenticationControllerApi* | [**register**](docs/AuthenticationControllerApi.md#register) | **POST** /authentication/register | 
*PartnershipControllerApi* | [**getSmackerPartnerships**](docs/PartnershipControllerApi.md#getsmackerpartnerships) | **GET** /partnership | 
*PartnershipControllerApi* | [**requestPartnership**](docs/PartnershipControllerApi.md#requestpartnership) | **PUT** /partnership/requestPartnership | 
*PartnershipControllerApi* | [**respondPartnership**](docs/PartnershipControllerApi.md#respondpartnership) | **PUT** /partnership/respondPartnership | 
*SmackControllerApi* | [**create**](docs/SmackControllerApi.md#create) | **POST** /smack | 
*SmackControllerApi* | [**list**](docs/SmackControllerApi.md#list) | **GET** /smack/{smackerId} | 
*SmackLocationControllerApi* | [**create1**](docs/SmackLocationControllerApi.md#create1) | **POST** /smack/location | 
*SmackLocationControllerApi* | [**reviewLocation**](docs/SmackLocationControllerApi.md#reviewlocation) | **POST** /smack/location/review | 
*SmackLocationControllerApi* | [**search**](docs/SmackLocationControllerApi.md#search) | **POST** /smack/location/search | 
*SmackerControllerApi* | [**generateSmackerLink**](docs/SmackerControllerApi.md#generatesmackerlink) | **PUT** /smacker/generateLink | 
*SmackerControllerApi* | [**getSmacker**](docs/SmackerControllerApi.md#getsmacker) | **GET** /smacker/{smackerId} | 
*SmackerControllerApi* | [**getSmackerByLink**](docs/SmackerControllerApi.md#getsmackerbylink) | **GET** /smacker/byLink/ | 
*SmackerControllerApi* | [**test**](docs/SmackerControllerApi.md#test) | **GET** /smacker | 


<a name="documentation-for-models"></a>
## Documentation for Models

 - [org.openapitools.client.models.ContinuationObject](docs/ContinuationObject.md)
 - [org.openapitools.client.models.CreateSmackLocationRequest](docs/CreateSmackLocationRequest.md)
 - [org.openapitools.client.models.CreateSmackRequest](docs/CreateSmackRequest.md)
 - [org.openapitools.client.models.CreateSmackerRequest](docs/CreateSmackerRequest.md)
 - [org.openapitools.client.models.GenerateSmackerLinkRequest](docs/GenerateSmackerLinkRequest.md)
 - [org.openapitools.client.models.LoginSmackerRequest](docs/LoginSmackerRequest.md)
 - [org.openapitools.client.models.PartnershipResponse](docs/PartnershipResponse.md)
 - [org.openapitools.client.models.RequestPartnershipRequest](docs/RequestPartnershipRequest.md)
 - [org.openapitools.client.models.RespondPartnershipRequest](docs/RespondPartnershipRequest.md)
 - [org.openapitools.client.models.SmackDto](docs/SmackDto.md)
 - [org.openapitools.client.models.SmackListDto](docs/SmackListDto.md)
 - [org.openapitools.client.models.SmackLocationDto](docs/SmackLocationDto.md)
 - [org.openapitools.client.models.SmackLocationReviewDto](docs/SmackLocationReviewDto.md)
 - [org.openapitools.client.models.SmackLocationReviewRequest](docs/SmackLocationReviewRequest.md)
 - [org.openapitools.client.models.SmackLocationSearchRequest](docs/SmackLocationSearchRequest.md)
 - [org.openapitools.client.models.SmackerDto](docs/SmackerDto.md)
 - [org.openapitools.client.models.SmackerLinkDto](docs/SmackerLinkDto.md)
 - [org.openapitools.client.models.SmackerPartnershipsResponse](docs/SmackerPartnershipsResponse.md)
 - [org.openapitools.client.models.SmackerRelationsDto](docs/SmackerRelationsDto.md)
 - [org.openapitools.client.models.SmackerViewDto](docs/SmackerViewDto.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="bearerAuth"></a>
### bearerAuth

- **Type**: HTTP basic authentication

