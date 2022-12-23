package com.lovemap.lovemapandroid.api.lovespot.photo

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface LoveSpotPhotoApi {

    @Multipart
    @POST("/lovespots/{loveSpotId}/photos")
    fun uploadToLoveSpot(
        @Path("loveSpotId") loveSpotId: Long,
        @Part("photos") photos: MultipartBody.Part
    ): Call<LoveSpot>

    @Multipart
    @POST("/lovespots/{loveSpotId}/reviews/{reviewId}/photos")
    fun uploadToLoveSpotReview(
        @Path("loveSpotId") loveSpotId: Long,
        @Path("reviewId") reviewId: Long,
        @Part("photos") photos: MultipartBody.Part
    ): Call<List<LoveSpotPhoto>>

    @GET("/lovespots/{loveSpotId}/photos")
    fun getPhotosForLoveSpot(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotPhoto>>

    @GET("/lovespots/{loveSpotId}/reviews/{reviewId}/photos")
    fun getPhotosForLoveSpotReview(
        @Path("loveSpotId") loveSpotId: Long,
        @Path("reviewId") reviewId: Long
    ): Call<List<LoveSpotPhoto>>
}