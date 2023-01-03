package com.lovemap.lovemapandroid.api.lover.wishlist

import retrofit2.Call
import retrofit2.http.*

interface WishlistApi {

    @GET("/lovers/{loverId}/wishlist")
    fun getWishlist(@Path("loverId") loverId: Long): Call<List<WishlistResponse>>

    @POST("/lovers/{loverId}/wishlist/addSpot/{loveSpotId}")
    fun addToWishlist(
        @Path("loverId") loverId: Long,
        @Path("loveSpotId") loveSpotId: Long
    ): Call<List<WishlistResponse>>
}
