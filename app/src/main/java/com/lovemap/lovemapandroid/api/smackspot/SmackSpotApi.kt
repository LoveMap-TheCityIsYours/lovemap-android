package com.lovemap.lovemapandroid.api.smackspot

import com.lovemap.lovemapandroid.api.smackspot.review.SmackSpotReviewDto
import com.lovemap.lovemapandroid.api.smackspot.review.SmackSpotReviewRequest
import com.lovemap.lovemapandroid.data.smackspot.SmackSpot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmackSpotApi {
    @POST("/lovespot")
    fun create(@Body request: CreateSmackSpotRequest): Call<SmackSpot>

    @GET("/lovespot/{id}")
    fun find(@Path("id") id: Long): Call<SmackSpot>

    @POST("/lovespot/search")
    fun search(@Body request: SmackSpotSearchRequest): Call<List<SmackSpot>>

    @POST("/lovespot/review")
    fun addReview(@Body request: SmackSpotReviewRequest): Call<SmackSpot>

    @GET("/lovespot/review/{smackSpotId}")
    fun getReviews(@Path("smackSpotId") loveSpotId: Long): Call<List<SmackSpotReviewDto>>

    @GET("/lovespot/risks")
    fun getRisks(): Call<SmackSpotRisks>
}