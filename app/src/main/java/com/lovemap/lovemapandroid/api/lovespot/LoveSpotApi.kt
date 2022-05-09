package com.lovemap.lovemapandroid.api.lovespot

import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import retrofit2.Call
import retrofit2.http.*

interface LoveSpotApi {
    @POST("/lovespot")
    fun create(@Body request: CreateLoveSpotRequest): Call<LoveSpot>

    @GET("/lovespot/{id}")
    fun find(@Path("id") id: Long): Call<LoveSpot>

    @POST("/lovespot/search")
    fun search(@Body request: LoveSpotSearchRequest): Call<List<LoveSpot>>

    @PUT("/lovespot/review")
    fun addReview(@Body request: LoveSpotReviewRequest): Call<LoveSpot>

    @GET("/lovespot/review/bySpot/{loveSpotId}")
    fun getReviewsForSpot(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespot/review/byLover/{loverId}")
    fun getReviewsByLover(@Path("loverId") loverId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespot/risks")
    fun getRisks(): Call<LoveSpotRisks>
}