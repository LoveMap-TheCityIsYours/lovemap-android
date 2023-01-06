package com.lovemap.lovemapandroid.ui.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.config.AppContext
import com.squareup.picasso.Picasso
import linc.com.heifconverter.HeifConverter
import java.io.File
import java.io.FileNotFoundException


object PhotoUtils {

    private val picasso = Picasso.get().apply { isLoggingEnabled = true }

    private const val REQUEST_EXTERNAL_STORAGE = 1

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
//            AppContext.INSTANCE.toaster.showToast(R.string.allow_access_to_storage)
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

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

    fun startPickerIntent(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_PICK
        launcher.launch(intent)
    }

    suspend fun readResultToFiles(
        activityResult: ActivityResult,
        contentResolver: ContentResolver
    ): Result<List<File>> {
        val itemCount: Int = activityResult.data?.clipData?.itemCount ?: 0
        val files = ArrayList<File>()
        var failed = false
        for (i in 0 until itemCount) {
            val clipData = activityResult.data!!.clipData!!
            val uri = clipData.getItemAt(i).uri
            Log.i("uri", "$uri")
            if (!addToFilesFromUri(uri, files, contentResolver)) {
                failed = true
            }
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
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        contentResolver.query(uri, projection, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    Log.i("columnIndex", "$columnIndex")
                    val filePath = cursor.getString(columnIndex)
                    Log.i("filePath", " $filePath")
                    if (filePath != null) {
                        val file = File(filePath)
                        Log.i("file", "$file")
                        if (isHeif(file.name)) {
                            val conversionResult = HeifConverter.useContext(AppContext.INSTANCE)
                                .fromFile(file.path)
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
                            files.add(file)
                        }
                    }
                }
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

    fun loadHeif(
        activity: Activity,
        imageView: ImageView,
        loveSpotType: LoveSpotType,
        url: String
    ) {
        imageView.setImageResource(LoveSpotUtils.getTypeImageResource(loveSpotType))
        HeifConverter.useContext(activity)
            .fromUrl(url)
            .withOutputFormat(HeifConverter.Format.JPEG)
            .convert { result ->
                val path = result[HeifConverter.Key.IMAGE_PATH] as String
                val file = File(path)
                picasso
                    .load(file)
                    .placeholder(LoveSpotUtils.getTypeImageResource(loveSpotType))
                    .into(imageView)
            }
    }

    fun loadSimpleImage(
        imageView: ImageView,
        loveSpotType: LoveSpotType,
        url: String
    ) {
        picasso
            .load(url)
            .placeholder(LoveSpotUtils.getTypeImageResource(loveSpotType))
            .into(imageView)
    }
}