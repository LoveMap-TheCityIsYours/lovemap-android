package com.lovemap.lovemapandroid.api.partnership

import com.lovemap.lovemapandroid.data.partnership.Partnership
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PartnershipApi {

    @GET("/v2/lovers/{loverId}/partnerships")
    fun getLoverPartnership(@Path("loverId") loverId: Long): Call<LoverPartnershipV2Response>

    @PUT("/v2/lovers/{loverId}/partnerships/requestPartnership")
    fun requestPartnership(@Path("loverId") loverId: Long, @Body request: RequestPartnershipRequest): Call<Partnership>

    @PUT("/v2/lovers/{loverId}/partnerships/respondPartnership")
    fun respondPartnership(@Path("loverId") loverId: Long, @Body request: RespondPartnershipRequest): Call<LoverPartnershipV2Response>

    @PUT("/v2/lovers/{loverId}/partnerships/cancelPartnershipRequest")
    fun cancelPartnershipRequest(@Path("loverId") loverId: Long, @Body request: CancelPartnershipRequest): Call<LoverPartnershipV2Response>

    @PUT("/v2/lovers/{loverId}/partnerships/endPartnership/{partnerLoverId}")
    fun endPartnership(@Path("loverId") loverId: Long, @Path("partnerLoverId") partnerLoverId: Long): Call<LoverPartnershipV2Response>
}