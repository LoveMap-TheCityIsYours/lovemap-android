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
 * @param userName 
 * @param email 
 * @param password 
 */

data class LoginSmackerRequest (

    @Json(name = "userName")
    val userName: kotlin.String? = null,

    @Json(name = "email")
    val email: kotlin.String? = null,

    @Json(name = "password")
    val password: kotlin.String? = null

)

