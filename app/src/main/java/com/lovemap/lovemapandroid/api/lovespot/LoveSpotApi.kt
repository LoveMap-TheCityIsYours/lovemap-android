package com.lovemap.lovemapandroid.api.lovespot

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoveSpotApi {
    @POST("/lovespots")
    fun create(@Body request: CreateLoveSpotRequest): Call<LoveSpot>

    @GET("/lovespots/{id}")
    fun find(@Path("id") id: Long): Call<LoveSpot>

    @POST("/lovespots/search")
    fun search(@Body request: LoveSpotSearchRequest): Call<List<LoveSpot>>

    @GET("/lovespots/risks")
    fun getRisks(): Call<LoveSpotRisks>
}