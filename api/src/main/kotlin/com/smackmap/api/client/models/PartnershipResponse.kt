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
 * @param partnershipStatus 
 * @param initiateDate 
 * @param respondDate 
 */

data class PartnershipResponse (

    @Json(name = "initiatorId")
    val initiatorId: kotlin.Long? = null,

    @Json(name = "respondentId")
    val respondentId: kotlin.Long? = null,

    @Json(name = "partnershipStatus")
    val partnershipStatus: PartnershipResponse.PartnershipStatus? = null,

    @Json(name = "initiateDate")
    val initiateDate: java.time.OffsetDateTime? = null,

    @Json(name = "respondDate")
    val respondDate: java.time.OffsetDateTime? = null

) {

    /**
     * 
     *
     * Values: pARTNERSHIPREQUESTED,iNPARTNERSHIP
     */
    enum class PartnershipStatus(val value: kotlin.String) {
        @Json(name = "PARTNERSHIP_REQUESTED") pARTNERSHIPREQUESTED("PARTNERSHIP_REQUESTED"),
        @Json(name = "IN_PARTNERSHIP") iNPARTNERSHIP("IN_PARTNERSHIP");
    }
}

