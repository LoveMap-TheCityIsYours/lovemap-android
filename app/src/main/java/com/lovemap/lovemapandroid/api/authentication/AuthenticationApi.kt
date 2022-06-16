package com.lovemap.lovemapandroid.api.authentication

import com.lovemap.lovemapandroid.api.lover.LoverDto
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("/authentication/register")
    fun register(@Body request: CreateLoverRequest): Call<LoverDto>

    @POST("/authentication/login")
    fun login(@Body request: LoginLoverRequest): Call<LoverRelationsDto>

    @POST("/authentication/request-password-reset")
    fun requestPasswordReset(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>

    @POST("/authentication/new-password")
    fun newPassword(@Body request: NewPasswordRequest): Call<LoverRelationsDto>
}