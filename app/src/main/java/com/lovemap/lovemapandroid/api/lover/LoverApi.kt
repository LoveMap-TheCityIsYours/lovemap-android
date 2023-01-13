package com.lovemap.lovemapandroid.api.lover

import retrofit2.Call
import retrofit2.http.*

interface LoverApi {

    @GET("/lovers/{loverId}")
    fun getById(@Path("loverId") loverId: Long): Call<LoverRelationsDto>

    @GET("/lovers/contributions/{loverId}")
    fun contributions(@Path("loverId") loverId: Long): Call<LoverContributionsDto>

    @POST("/lovers/{loverId}/shareableLink")
    fun generateLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @DELETE("/lovers/{loverId}/shareableLink")
    fun deleteLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @GET("/lovers")
    fun getByUuid(@Query("uuid") uuid: String): Call<LoverViewDto>

    @GET("/lovers/view/{loverId}")
    fun getLoverView(@Path("loverId") loverId: Long): Call<LoverViewDto>

    @PUT("/lovers/{loverId}")
    fun updateLover(@Path("loverId") loverId: Long, @Body update: UpdateLoverRequest): Call<LoverDto>

    @GET("/lovers/ranks")
    fun getRanks(): Call<LoverRanks>
}