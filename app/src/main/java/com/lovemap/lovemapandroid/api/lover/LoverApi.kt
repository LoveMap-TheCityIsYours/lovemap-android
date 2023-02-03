package com.lovemap.lovemapandroid.api.lover

import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import retrofit2.Call
import retrofit2.http.*

interface LoverApi {

    @GET("/lovers/{loverId}")
    fun getById(@Path("loverId") loverId: Long): Call<LoverRelationsDto>

    @POST("/lovers/{loverId}/shareableLink")
    fun generateLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @POST("/lovers/{loverId}/registerFirebaseToken")
    fun registerFirebaseToken(
        @Path("loverId") loverId: Long,
        @Body token: FirebaseTokenRegistration
    ): Call<LoverDto>

    @DELETE("/lovers/{loverId}/shareableLink")
    fun deleteLink(@Path("loverId") loverId: Long): Call<LoverDto>

    @GET("/lovers")
    fun getByUuid(@Query("uuid") uuid: String): Call<LoverViewDto>

    @GET("/lovers/view/{loverId}")
    fun getLoverView(@Path("loverId") loverId: Long): Call<LoverViewDto>

    @GET("/lovers/cachedView/{loverId}")
    fun getCachedLoverView(@Path("loverId") loverId: Long): Call<LoverViewWithoutRelationDto>

    @PUT("/lovers/{loverId}")
    fun updateLover(
        @Path("loverId") loverId: Long,
        @Body update: UpdateLoverRequest
    ): Call<LoverDto>

    @GET("/lovers/ranks")
    fun getRanks(): Call<LoverRanks>

    @GET("/lovers/{loverId}/activities")
    fun getLoverActivities(@Path("loverId") loverId: Long): Call<List<NewsFeedItemResponse>>

    @GET("/lovers/hallOfFame")
    fun getHallOfFame(): Call<List<LoverViewWithoutRelationDto>>
}
