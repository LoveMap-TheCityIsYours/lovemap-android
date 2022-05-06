package com.smackmap.smackmapandroid.api.smack

import com.smackmap.smackmapandroid.data.smack.Smack
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmackApi {

    @GET("/smack/{smackerId}")
    fun list(@Path("smackerId") smackerId: Long): Call<SmackListDto>

    @POST("/smack")
    fun create(@Body request: CreateSmackRequest): Call<Smack>
}