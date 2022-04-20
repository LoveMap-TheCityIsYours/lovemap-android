/**
 * Smackmap API
 *
 * Smackmap API
 *
 * The version of the OpenAPI document: v0.0.1
 * 
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.smackmap.api.client.models


import com.squareup.moshi.Json

/**
 * 
 *
 * @param initiatorId 
 * @param respondentId 
 */

data class RequestPartnershipRequest (

    @Json(name = "initiatorId")
    val initiatorId: kotlin.Long? = null,

    @Json(name = "respondentId")
    val respondentId: kotlin.Long? = null

)

