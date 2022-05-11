package com.lovemap.lovemapandroid.api.partnership

import com.lovemap.lovemapandroid.data.partnership.Partnership
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PartnershipApi {

    @GET("/partnerships/{loverId}")
    fun getLoverPartnerships(@Path("loverId") loverId: Long): Call<LoverPartnershipsResponse>

    @PUT("/partnerships/requestPartnership")
    fun requestPartnership(@Body request: RequestPartnershipRequest): Call<Partnership>

    @PUT("/partnerships/respondPartnership")
    fun respondPartnership(@Body request: RespondPartnershipRequest): Call<LoverPartnershipsResponse>
}