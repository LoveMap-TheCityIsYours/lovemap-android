package com.lovemap.lovemapandroid.api.newsfeed

import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Instant

interface NewsFeedApi {

    @GET("/newsfeed")
    fun getPage(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<NewsFeedItemResponse>>
}
