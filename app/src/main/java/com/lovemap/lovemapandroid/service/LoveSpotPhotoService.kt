package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhotoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveSpotPhotoService(
    private val loveSpotPhotoApi: LoveSpotPhotoApi,
    private val toaster: Toaster
) {

    suspend fun getPhotosForLoveSpot(loveSpotId: Long): List<LoveSpotPhoto> {
        return withContext(Dispatchers.IO) {
            val call = loveSpotPhotoApi.getPhotosForLoveSpot(loveSpotId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_load_photos)
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                response.body()!!
            } else {
                toaster.showToast(R.string.failed_to_load_photos)
                emptyList()
            }
        }
    }
}