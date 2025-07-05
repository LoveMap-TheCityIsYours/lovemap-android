package com.lovemap.lovemapandroid.ui.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.config.AppContext
import linc.com.heifconverter.HeifConverter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


object PhotoUtils {

    private const val TAG = "PhotoUtils"

    suspend fun canUploadForReview(loveSpotId: Long): Boolean {
        return if (AppContext.INSTANCE.loveSpotReviewService.hasReviewedAlready(loveSpotId)) {
            true
        } else {
            AppContext.INSTANCE.toaster.showToast(R.string.write_review_before_photo_upload)
            false
        }
    }

    suspend fun canUploadForSpot(loveSpotId: Long): Boolean {
        val loveSpot = AppContext.INSTANCE.loveSpotService.findLocally(loveSpotId)
        if (loveSpot != null) {
            return if (AppContext.INSTANCE.isAdmin) {
                true
            } else {
                loveSpot.addedBy == AppContext.INSTANCE.userId
            }
        }
        return false
    }

    fun canDeletePhoto(loveSpotPhoto: LoveSpotPhoto): Boolean {
        return AppContext.INSTANCE.isAdmin
                || loveSpotPhoto.uploadedBy == AppContext.INSTANCE.userId
    }

    fun startPickerIntent(launcher: ActivityResultLauncher<PickVisualMediaRequest>) {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    suspend fun readResultToFiles(
        uri: Uri,
        contentResolver: ContentResolver
    ): Result<List<File>> {
        val files = ArrayList<File>()
        var failed = false

        Log.i("uri", "$uri")
        if (!addToFilesFromUri(uri, files, contentResolver)) {
            failed = true
        }

        Log.i("readResultToFiles", "returning failed: $failed")
        if (failed) {
            return Result.failure(FileNotFoundException("Failed to read files"))
        }
        return Result.success(files)
    }

    private suspend fun addToFilesFromUri(
        uri: Uri,
        files: ArrayList<File>,
        contentResolver: ContentResolver
    ): Boolean {
        var success = true
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val mimeType = contentResolver.getType(uri)
                val tempFile = File.createTempFile("IMG_", ".jpg", AppContext.INSTANCE.cacheDir)
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()

                if (isHeifMimeType(mimeType)) {
                    val conversionResult = HeifConverter.useContext(AppContext.INSTANCE)
                        .fromFile(tempFile.path)
                        .withOutputFormat(HeifConverter.Format.JPEG)
                        .withOutputQuality(80)
                        .convertBlocking()
                    val path = conversionResult[HeifConverter.Key.IMAGE_PATH] as String
                    val convertedFile = File(path)
                    Log.i(
                        "convertedFile",
                        "convertedFile length: ${convertedFile.length()}"
                    )
                    if (convertedFile.length() == 0L) {
                        Log.i("ConversionFailed", "Conversion Failed")
                        success = false
                    }
                    files.add(convertedFile)
                } else {
                    files.add(tempFile)
                }
            } else {
                success = false
            }
        } catch (e: Exception) {
            Log.e("addToFilesFromUri", "Failed to read URI: $uri", e)
            success = false
        }
        Log.i("addToFilesFromUri", "returning success: $success")
        return success
    }

    fun permissionDialog(activity: Activity): AlertDialog {
        val alertDialog = AlertDialog.Builder(activity, R.style.MyDialogTheme)
            .setTitle(activity.getString(R.string.photo_upload_failed))
            .setMessage(activity.getString(R.string.allow_access_to_storage_long))
            .setPositiveButton(activity.getString(R.string.ok_capital)) { dialog, _ -> // Do nothing but close the dialog
                activity.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.lovemap.lovemapandroid")
                    )
                )
                dialog.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.cancel_capital)) { dialog, _ -> // Do nothing
                dialog.dismiss()
            }.create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                activity.resources.getColor(
                    R.color.myColorOnSecondary,
                    activity.theme
                )
            )
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                activity.resources.getColor(
                    R.color.myColorOnSecondary,
                    activity.theme
                )
            )
        }
        alertDialog.show()
        return alertDialog
    }

    fun isHeif(photo: LoveSpotPhoto) = isHeif(photo.fileName)

    fun isHeif(fileName: String) =
        fileName.lowercase().endsWith("heic") || fileName.lowercase().endsWith("heif")

    fun isHeifMimeType(mimeType: String?) =
        mimeType?.lowercase() == "image/heic" || mimeType?.lowercase() == "image/heif"

    fun loadHeif(
        activity: Activity,
        imageView: ImageView,
        loveSpotType: LoveSpotType,
        url: String
    ) {
        try {
            imageView.setImageResource(LoveSpotUtils.getTypeImageResource(loveSpotType))
            HeifConverter.useContext(activity)
                .fromUrl(url)
                .withOutputFormat(HeifConverter.Format.JPEG)
                .convert { result ->
                    val path = result[HeifConverter.Key.IMAGE_PATH] as String
                    val file = File(path)
                    Glide.with(activity as Context)
                        .load(file)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .fitCenter()
                        .fallback(LoveSpotUtils.getTypeImageResource(loveSpotType))
                        .into(imageView)
                }
        } catch (e: Exception) {
            Log.e(TAG, "loadHeif shitted itself", e)
        }
    }

    fun loadSimpleImage(
        imageView: ImageView,
        loveSpotType: LoveSpotType,
        url: String
    ) {
        try {
            Glide.with(imageView.context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .fitCenter()
                .fallback(LoveSpotUtils.getTypeImageResource(loveSpotType))
                .into(imageView)
        } catch (e: Exception) {
            Log.e(TAG, "loadSimpleImage shitted itself", e)
        }
    }

    fun loadSimpleImage(
        imageView: ImageView,
        loveSpotType: LoveSpotType,
        url: String,
        overrideSize: Int
    ) {
        try {
            Glide.with(imageView.context)
                .load(url)
                .override(overrideSize)
                .transition(DrawableTransitionOptions.withCrossFade())
                .fallback(LoveSpotUtils.getTypeImageResource(loveSpotType))
                .into(imageView)
        } catch (e: Exception) {
            Log.e(TAG, "loadSimpleImage shitted itself", e)
        }
    }
}