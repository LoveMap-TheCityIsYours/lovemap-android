package com.lovemap.lovemapandroid.api.lover

import retrofit2.Call
import retrofit2.http.*

interface LoverApi {

    @GET("/lover/{loverId}")
    fun getById(@Path("loverId") loverId: Long): Call<LoverRelationsDto>

    @POST("/lover/{loverId}/shareableLink")
    fun generateLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @DELETE("/lover/{loverId}/shareableLink")
    fun deleteLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @GET("/lover/byLink")
    fun getByLink(@Query("loverLink") loverLink: String): Call<LoverViewDto>

    @GET("/lover/ranks")
    fun getRanks(): Call<LoverRanks>
}