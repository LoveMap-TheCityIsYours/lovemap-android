package com.lovemap.lovemapandroid.api.newsfeed

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsFeedApi {

    @GET("/newsfeed/v2")
    fun getPage(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<NewsFeedItemResponse>>
}
