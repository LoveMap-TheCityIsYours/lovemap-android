package com.lovemap.lovemapandroid.service

import android.app.Activity
import android.util.Log
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhotoApi
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


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

    suspend fun deletePhoto(loveSpotId: Long, photoId: Long): List<LoveSpotPhoto> {
        return withContext(Dispatchers.IO) {
            val call = loveSpotPhotoApi.deletePhoto(loveSpotId, photoId)
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

    suspend fun uploadToLoveSpot(loveSpotId: Long, photos: List<File>, activity: Activity) {
        if (photos.isNotEmpty()) {
            val loadingBarShower = LoadingBarShower(activity).show(R.string.uploading_photo)
            withContext(Dispatchers.IO) {
                val parts: List<MultipartBody.Part> = photos.map { prepareFilePart(it) }
                val call = loveSpotPhotoApi.uploadToLoveSpot(loveSpotId, parts)
                try {
                    val response = call.execute()
                    loadingBarShower.onResponse()
                    if (response.isSuccessful) {
                        toaster.showToast(R.string.photo_uploaded_succesfully)
                    } else {
                        toaster.showResponseError(response)
                        Log.e("LoveSpotPhotoService", "Photo upload exception: $response")
                    }
                } catch (e: Exception) {
                    loadingBarShower.onResponse()
                    Log.e("LoveSpotPhotoService", "Photo upload exception", e)
                    toaster.showToast(R.string.photo_upload_failed)
                }
            }
        }
    }

    private fun prepareFilePart(file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            MediaType.get("image/*"),
            file
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("photos", file.name, requestFile)
    }
}