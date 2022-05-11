package com.lovemap.lovemapandroid.api.lovespot

import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import retrofit2.Call
import retrofit2.http.*

interface LoveSpotApi {
    @POST("/lovespots")
    fun create(@Body request: CreateLoveSpotRequest): Call<LoveSpot>

    @GET("/lovespots/{id}")
    fun find(@Path("id") id: Long): Call<LoveSpot>

    @POST("/lovespots/search")
    fun search(@Body request: LoveSpotSearchRequest): Call<List<LoveSpot>>

    @PUT("/lovespots/reviews")
    fun addReview(@Body request: LoveSpotReviewRequest): Call<LoveSpot>

    @GET("/lovespots/reviews/bySpot/{loveSpotId}")
    fun getReviewsForSpot(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespots/reviews/byLover/{loverId}")
    fun getReviewsByLover(@Path("loverId") loverId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespots/risks")
    fun getRisks(): Call<LoveSpotRisks>
}