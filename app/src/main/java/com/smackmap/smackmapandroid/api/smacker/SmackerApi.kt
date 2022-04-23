package com.smackmap.smackmapandroid.api.smacker

import retrofit2.Call
import retrofit2.http.*

interface SmackerApi {

    @GET("/smacker/{smackerId}")
    fun getById(@Path("smackerId") smackerId: Long): Call<SmackerRelationsDto>

    @POST("/smacker/{smackerId}/shareableLink")
    fun generateLink(@Path("smackerId") smackerId: Long): Call<SmackerDto>

    @DELETE("/smacker/{smackerId}/shareableLink")
    fun deleteLink(@Path("smackerId") smackerId: Long): Call<SmackerDto>

    @GET("/smacker/byLink")
    fun getByLink(@Query("smackerLink") smackerLink: String): Call<SmackerViewDto>
}