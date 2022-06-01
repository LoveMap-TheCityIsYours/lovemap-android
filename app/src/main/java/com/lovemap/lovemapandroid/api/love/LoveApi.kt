package com.lovemap.lovemapandroid.api.love

import com.lovemap.lovemapandroid.data.love.Love
import retrofit2.Call
import retrofit2.http.*

interface LoveApi {

    @GET("/loves")
    fun listByLover(@Query("loverId") loverId: Long): Call<List<Love>>

    @POST("/loves")
    fun create(@Body request: CreateLoveRequest): Call<Love>

    @PUT("/loves/{id}")
    fun update(@Path("id") id: Long, @Body request: UpdateLoveRequest): Call<Love>

    @DELETE("/loves/{id}")
    fun delete(@Path("id") id: Long): Call<Love>
}