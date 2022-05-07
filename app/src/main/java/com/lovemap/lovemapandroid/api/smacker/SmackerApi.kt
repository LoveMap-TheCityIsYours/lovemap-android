package com.lovemap.lovemapandroid.api.smacker

import retrofit2.Call
import retrofit2.http.*

interface SmackerApi {

    @GET("/lover/{loverId}")
    fun getById(@Path("loverId") loverId: Long): Call<SmackerRelationsDto>

    @POST("/lover/{loverId}/shareableLink")
    fun generateLink(@Path("loverId") loverId: Long): Call<SmackerDto>

    @DELETE("/lover/{loverId}/shareableLink")
    fun deleteLink(@Path("loverId") loverId: Long): Call<SmackerDto>

    @GET("/lover/byLink")
    fun getByLink(@Query("loverLink") loverLink: String): Call<SmackerViewDto>

    @GET("/lover/ranks")
    fun getRanks(): Call<SmackerRanks>
}