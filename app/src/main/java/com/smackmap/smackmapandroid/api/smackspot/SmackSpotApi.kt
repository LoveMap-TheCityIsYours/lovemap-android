package com.smackmap.smackmapandroid.api.smackspot

import com.smackmap.smackmapandroid.api.smackspot.review.SmackSpotReviewDto
import com.smackmap.smackmapandroid.api.smackspot.review.SmackSpotReviewRequest
import com.smackmap.smackmapandroid.data.smackspot.SmackSpot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmackSpotApi {
    @POST("/smackspot")
    fun create(@Body request: CreateSmackSpotRequest): Call<SmackSpot>

    @GET("/smackspot/{id}")
    fun find(@Path("id") id: Long): Call<SmackSpot>

    @POST("/smackspot/search")
    fun search(@Body request: SmackSpotSearchRequest): Call<List<SmackSpot>>

    @POST("/smackspot/review")
    fun addReview(@Body request: SmackSpotReviewRequest): Call<SmackSpot>

    @GET("/smackspot/review/{smackSpotId}")
    fun getReviews(@Path("smackSpotId") smackSpotId: Long): Call<List<SmackSpotReviewDto>>

    @GET("/smackspot/risks")
    fun getRisks(): Call<SmackSpotRisks>
}