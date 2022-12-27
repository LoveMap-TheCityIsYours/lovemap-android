package com.lovemap.lovemapandroid.api.lovespot.photo

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface LoveSpotPhotoApi {

    @Multipart
    @POST("/lovespots/{loveSpotId}/photos")
    fun uploadToLoveSpot(
        @Path("loveSpotId") loveSpotId: Long,
        @Part photos: List<MultipartBody.Part>
    ): Call<ResponseBody>

    @Multipart
    @POST("/lovespots/{loveSpotId}/reviews/{reviewId}/photos")
    fun uploadToLoveSpotReview(
        @Path("loveSpotId") loveSpotId: Long,
        @Path("reviewId") reviewId: Long,
        @Part photos: List<MultipartBody.Part>
    ): Call<ResponseBody>

    @GET("/lovespots/{loveSpotId}/photos")
    fun getPhotosForLoveSpot(@Path("loveSpotId") loveSpotId: Long): Call<List<LoveSpotPhoto>>

    @GET("/lovespots/{loveSpotId}/reviews/{reviewId}/photos")
    fun getPhotosForLoveSpotReview(
        @Path("loveSpotId") loveSpotId: Long,
        @Path("reviewId") reviewId: Long
    ): Call<List<LoveSpotPhoto>>

    @DELETE("/lovespots/{loveSpotId}/photos/{photoId}")
    fun deletePhoto(
        @Path("loveSpotId") loveSpotId: Long,
        @Path("photoId") photoId: Long
    ): Call<List<LoveSpotPhoto>>
}
