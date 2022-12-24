package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhotoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


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

    suspend fun uploadToLoveSpot(loveSpotId: Long, photos: List<File>) {
        withContext(Dispatchers.IO) {
            val parts: List<MultipartBody.Part> = photos.map { prepareFilePart(it.name, it) }
            val call = loveSpotPhotoApi.uploadToLoveSpot(loveSpotId, parts)
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    toaster.showToast(R.string.photo_uploaded_succesfully)
                } else {
                    toaster.showResponseError(response)
                }
            } catch (e: Exception) {
                toaster.showToast(R.string.photo_upload_failed)
            }
        }
    }

    private fun prepareFilePart(partName: String, file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            MediaType.get("multipart/form-data"),
            file
        )

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}