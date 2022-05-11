package com.lovemap.lovemapandroid.api.love

import com.lovemap.lovemapandroid.data.love.Love
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoveApi {

    @GET("/loves/{loverId}")
    fun list(@Path("loverId") loverId: Long): Call<List<Love>>

    @POST("/loves")
    fun create(@Body request: CreateLoveRequest): Call<Love>
}