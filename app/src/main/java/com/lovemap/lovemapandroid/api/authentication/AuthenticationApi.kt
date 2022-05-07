package com.lovemap.lovemapandroid.api.authentication

import com.lovemap.lovemapandroid.api.smacker.SmackerDto
import com.lovemap.lovemapandroid.api.smacker.SmackerRelationsDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("/authentication/register")
    fun register(@Body request: CreateSmackerRequest): Call<SmackerDto>

    @POST("/authentication/login")
    fun login(@Body request: LoginSmackerRequest): Call<SmackerRelationsDto>
}