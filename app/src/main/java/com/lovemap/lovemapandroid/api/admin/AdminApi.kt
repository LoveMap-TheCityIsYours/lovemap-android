package com.lovemap.lovemapandroid.api.admin

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface AdminApi {

    @DELETE("/admin/lovespots/{loveSpotId}")
    fun deleteLoveSpot(@Path("loveSpotId") loveSpotId: Long): Call<LoveSpot>
}