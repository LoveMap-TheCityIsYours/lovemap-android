package com.lovemap.lovemapandroid.api.lovespot

import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewDto
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoveSpotApi {
    @POST("/lovespot")
    fun create(@Body request: CreateLoveSpotRequest): Call<LoveSpot>

    @GET("/lovespot/{id}")
    fun find(@Path("id") id: Long): Call<LoveSpot>

    @POST("/lovespot/search")
    fun search(@Body request: LoveSpotSearchRequest): Call<List<LoveSpot>>

    @POST("/lovespot/review")
    fun addReview(@Body request: LoveSpotReviewRequest): Call<LoveSpot>

    @GET("/lovespot/review/{loveSpotId}")
    fun getReviews(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotReviewDto>>

    @GET("/lovespot/risks")
    fun getRisks(): Call<LoveSpotRisks>
}