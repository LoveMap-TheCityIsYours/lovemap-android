package com.smackmap.smackmapandroid.api.authentication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {

    @POST("/authentication/register")
    fun register(@Body request: CreateSmackerRequest): Call<SmackerResponse>
}