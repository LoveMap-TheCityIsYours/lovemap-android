package com.lovemap.lovemapandroid.api.lovespot.report

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface LoveSpotReportApi {
    @PUT("/lovespots/reports")
    fun submitReport(@Body request: LoveSpotReportRequest): Call<LoveSpot>

    @GET("/lovespots/reports/bySpot/{loveSpotId}")
    fun getReviewsForSpot(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotReview>>

    @GET("/lovespots/reports/byLover/{loverId}")
    fun getReviewsByLover(@Path("loverId") loverId: Long): Call<List<LoveSpotReview>>
}