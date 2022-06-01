package com.lovemap.lovemapandroid.api.admin

import com.lovemap.lovemapandroid.api.lovespot.UpdateLoveSpotRequest
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApi {

    @DELETE("/admin/lovespots/{loveSpotId}")
    fun deleteLoveSpot(@Path("loveSpotId") loveSpotId: Long): Call<LoveSpot>

    @PUT("/admin/lovespots/{loveSpotId}")
    fun updateLoveSpot(@Path("loveSpotId") loveSpotId: Long, @Body request: UpdateLoveSpotRequest): Call<LoveSpot>
}