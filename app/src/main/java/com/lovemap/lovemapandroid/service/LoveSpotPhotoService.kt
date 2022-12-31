package com.lovemap.lovemapandroid.service

import android.app.Activity
import android.util.Log
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhotoApi
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import com.lovemap.lovemapandroid.ui.utils.PhotoUploadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException


class LoveSpotPhotoService(
    private val loveSpotPhotoApi: LoveSpotPhotoApi,
    private val loveSpotService: LoveSpotService,
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

    suspend fun deletePhoto(loveSpotId: Long, photoId: Long): Result<List<LoveSpotPhoto>> {
        return withContext(Dispatchers.IO) {
            val call = loveSpotPhotoApi.deletePhoto(loveSpotId, photoId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_load_photos)
                return@withContext Result.failure(e)
            }
            if (response.isSuccessful) {
                val remainingPhotos = response.body()!!
                loveSpotService.updatePhotoCount(loveSpotId, remainingPhotos.size)
                Result.success(remainingPhotos)
            } else {
                toaster.showResponseError(response)
                Result.failure(IllegalStateException("${response.errorBody()}"))
            }
        }
    }

    suspend fun uploadToLoveSpot(
        loveSpotId: Long,
        photos: List<File>,
        activity: Activity
    ): Boolean {
        return if (photos.isNotEmpty()) {
            val loadingBarShower = LoadingBarShower(activity).show(R.string.uploading_photo)
            withContext(Dispatchers.IO) {
                val parts: List<MultipartBody.Part> = photos.map { prepareFilePart(it) }
                val call = loveSpotPhotoApi.uploadToLoveSpot(loveSpotId, parts)
                try {
                    val response = call.execute()
                    loadingBarShower.onResponse()
                    if (response.isSuccessful) {
                        toaster.showToast(R.string.photo_uploaded_succesfully)
                        true
                    } else {
                        toaster.showResponseError(response)
                        Log.e("LoveSpotPhotoService", "Photo upload exception: $response")
                        false
                    }
                } catch (e: Exception) {
                    loadingBarShower.onResponse()
                    Log.e("LoveSpotPhotoService", "Photo upload exception", e)
                    showUploadFailedAlert(e, activity)
                    false
                }
            }
        } else {
            false
        }
    }

    suspend fun uploadToReview(
        loveSpotId: Long,
        reviewId: Long,
        photos: List<File>,
        activity: Activity
    ): Boolean {
        return if (photos.isNotEmpty()) {
            val loadingBarShower = LoadingBarShower(activity).show(R.string.uploading_photo)
            withContext(Dispatchers.IO) {
                val parts: List<MultipartBody.Part> = photos.map { prepareFilePart(it) }
                val call = loveSpotPhotoApi.uploadToLoveSpotReview(loveSpotId, reviewId, parts)
                try {
                    val response = call.execute()
                    loadingBarShower.onResponse()
                    if (response.isSuccessful) {
                        toaster.showToast(
                            R.string.photo_uploaded_succesfully
                        )
                        true
                    } else {
                        toaster.showResponseError(response)
                        Log.e("LoveSpotPhotoService", "Photo upload exception: $response")
                        false
                    }
                } catch (e: Exception) {
                    loadingBarShower.onResponse()
                    Log.e("LoveSpotPhotoService", "Photo upload exception", e)
                    showUploadFailedAlert(e, activity)
                    false
                }
            }
        } else {
            false
        }
    }

    private fun showUploadFailedAlert(exception: Exception, activity: Activity) {
        toaster.showToast(R.string.photo_upload_failed)
        if (exception is FileNotFoundException) {
            activity.runOnUiThread {
                PhotoUploadUtils.permissionDialog(activity).show()
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