package com.lovemap.lovemapandroid.api.lovespot

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import retrofit2.Call
import retrofit2.http.*

interface LoveSpotApi {
    @POST("/lovespots")
    fun create(@Body request: CreateLoveSpotRequest): Call<LoveSpot>

    @GET("/lovespots/{id}")
    fun find(@Path("id") id: Long): Call<LoveSpot>

    @POST("/lovespots/list")
    fun list(@Body request: LoveSpotListRequest): Call<List<LoveSpot>>

    @POST("/lovespots/advancedSearch")
    fun search(
        @Query("searchResultOrdering") searchResultOrdering: SearchResultOrdering,
        @Query("searchLocation") searchLocation: SearchLocation,
        @Body request: LoveSpotSearchRequest
    ): Call<List<LoveSpot>>

    @GET("/lovespots/risks")
    fun getRisks(): Call<LoveSpotRisks>

    @PUT("/lovespots/{loveSpotId}")
    fun update(
        @Path("loveSpotId") loveSpotId: Long,
        @Body request: UpdateLoveSpotRequest
    ): Call<LoveSpot>
}