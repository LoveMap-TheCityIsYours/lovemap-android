package com.lovemap.lovemapandroid.api.lover

import retrofit2.Call
import retrofit2.http.*

interface LoverApi {

    @GET("/lover/{loverId}")
    fun getById(@Path("loverId") loverId: Long): Call<LoverRelationsDto>

    @GET("/lover/contributions/{loverId}")
    fun contributions(@Path("loverId") loverId: Long): Call<LoverContributionsDto>

    @POST("/lover/{loverId}/shareableLink")
    fun generateLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @DELETE("/lover/{loverId}/shareableLink")
    fun deleteLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @GET("/lover")
    fun getByUuid(@Query("uuid") uuid: String): Call<LoverViewDto>

    @GET("/lover/ranks")
    fun getRanks(): Call<LoverRanks>
}