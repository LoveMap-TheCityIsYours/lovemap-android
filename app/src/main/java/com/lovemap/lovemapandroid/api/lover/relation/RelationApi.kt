package com.lovemap.lovemapandroid.api.lover.relation

import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RelationApi {

    @GET("/lovers/{loverId}/relations")
    fun getRelations(@Path("loverId") loverId: Long): Call<LoverRelationsDto>

    @GET("/lovers/{loverId}/relations/followingNewsFeed")
    fun getFollowingNewsFeed(@Path("loverId") loverId: Long): Call<List<NewsFeedItemResponse>>

    @POST("/lovers/{loverId}/relations/{targetLoverId}/follow")
    fun followLover(
        @Path("loverId") loverId: Long,
        @Path("targetLoverId") targetLoverId: Long
    ): Call<LoverRelationsDto>

    @DELETE("/lovers/{loverId}/relations/{targetLoverId}/unfollow")
    fun unfollowLover(
        @Path("loverId") loverId: Long,
        @Path("targetLoverId") targetLoverId: Long
    ): Call<LoverRelationsDto>

    @GET("/lovers/{loverId}/relations/followers")
    fun getFollowers(@Path("loverId") loverId: Long): Call<List<LoverViewWithoutRelationDto>>

    @DELETE("/lovers/{loverId}/relations/{targetLoverId}/removeFollower")
    fun removeFollower(
        @Path("loverId") loverId: Long,
        @Path("targetLoverId") targetLoverId: Long
    ): Call<List<LoverViewWithoutRelationDto>>
}
