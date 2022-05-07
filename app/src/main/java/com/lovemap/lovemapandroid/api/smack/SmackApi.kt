package com.lovemap.lovemapandroid.api.smack

import com.lovemap.lovemapandroid.data.smack.Smack
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmackApi {

    @GET("/love/{smackerId}")
    fun list(@Path("smackerId") smackerId: Long): Call<SmackListDto>

    @POST("/love")
    fun create(@Body request: CreateSmackRequest): Call<Smack>
}