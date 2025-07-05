package com.lovemap.lovemapandroid.service.lovespot

import android.app.Activity
import android.util.Log
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.ErrorCode
import com.lovemap.lovemapandroid.api.getErrorCodes
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhotoApi
import com.lovemap.lovemapandroid.service.Toaster
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
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

    suspend fun likePhoto(loveSpotId: Long, photoId: Long): LoveSpotPhoto? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotPhotoApi.likePhoto(loveSpotId, photoId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_like_photo)
                return@withContext null
            }
            if (response.isSuccessful) {
                response.body()!!
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun dislikePhoto(loveSpotId: Long, photoId: Long): LoveSpotPhoto? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotPhotoApi.dislikePhoto(loveSpotId, photoId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_dislike_photo)
                return@withContext null
            }
            if (response.isSuccessful) {
                response.body()!!
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun uploadToLoveSpot(
        loveSpotId: Long,
        photos: List<File>,
        activity: Activity
    ): Boolean {
        return if (photos.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                val parts: List<MultipartBody.Part> = photos.map { prepareFilePart(it) }
                val call = loveSpotPhotoApi.uploadToLoveSpot(loveSpotId, parts)
                try {
                    val response = call.execute()
                    if (response.isSuccessful) {
                        toaster.showToast(R.string.photo_uploaded_succesfully)
                        true
                    } else {
                        toaster.showResponseError(response)
                        showPermissionDialog(response, activity)
                        Log.e("LoveSpotPhotoService", "Photo upload exception: $response")
                        false
                    }
                } catch (e: Exception) {
                    Log.e("LoveSpotPhotoService", "Photo upload exception", e)
                    showUploadFailedAlert(e, activity)
                    false
                }
            }
        } else {
            false
        }
    }

    private fun showPermissionDialog(
        response: Response<ResponseBody>,
        activity: Activity
    ) {
        if (response.getErrorCodes().contains(ErrorCode.UploadedPhotoFileEmpty)) {
            Log.i("showPermissionDialog", "Showing permissionDialog")
            activity.runOnUiThread {
                PhotoUtils.permissionDialog(activity)
            }
        }
    }

    suspend fun uploadToReview(
        loveSpotId: Long,
        reviewId: Long,
        photos: List<File>,
        activity: Activity
    ): Boolean {
        return if (photos.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                val parts: List<MultipartBody.Part> = photos.map { prepareFilePart(it) }
                val call = loveSpotPhotoApi.uploadToLoveSpotReview(loveSpotId, reviewId, parts)
                try {
                    val response = call.execute()
                    if (response.isSuccessful) {
                        toaster.showToast(
                            R.string.photo_uploaded_succesfully
                        )
                        true
                    } else {
                        toaster.showResponseError(response)
                        showPermissionDialog(response, activity)
                        Log.e("LoveSpotPhotoService", "Photo upload exception: $response")
                        false
                    }
                } catch (e: Exception) {
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
            Log.i("showUploadFailedAlert", "Showing permissionDialog")
            activity.runOnUiThread {
                PhotoUtils.permissionDialog(activity)
            }
        }
    }

    private fun prepareFilePart(file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            "image/*".toMediaTypeOrNull(), // Use the extension function
            file
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("photos", file.name, requestFile)
    }
}