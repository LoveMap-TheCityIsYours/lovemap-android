package com.lovemap.lovemapandroid.api.lovespot.review

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface LoveSpotReviewApi {
    @PUT("/lovespots/reviews")
    fun submitReview(@Body request: LoveSpotReviewRequest): Call<LoveSpot>

    @GET("/lovespots/reviews/bySpot/{loveSpotId}")
    fun getReviewsForSpot(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespots/reviews/byLover/{loverId}")
    fun getReviewsByLover(@Path("loverId") loverId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespots/reviews/{reviewId}")
    fun getReviewById(@Path("reviewId") reviewId: Long): Call<LoveSpotReview>
}