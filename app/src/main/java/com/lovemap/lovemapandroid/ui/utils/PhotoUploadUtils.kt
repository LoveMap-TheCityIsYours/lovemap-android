package com.lovemap.lovemapandroid.ui.utils

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import java.io.File


object PhotoUploadUtils {

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
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    suspend fun canUploadForSpot(loveSpotId: Long): Boolean {
        val loveSpot = AppContext.INSTANCE.loveSpotService.findLocally(loveSpotId)
        if (loveSpot != null) {
            if (loveSpot.addedBy == AppContext.INSTANCE.userId) {
                return true
            }
        }
        if (AppContext.INSTANCE.loveSpotReviewService.hasReviewedAlready(loveSpotId)) {
            return true
        }
        AppContext.INSTANCE.toaster.showToast(R.string.write_review_before_photo_upload)
        return false
    }

    fun startPickerIntent(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_PICK
        launcher.launch(intent)
    }

    fun readResultToFiles(
        activityResult: ActivityResult,
        contentResolver: ContentResolver
    ): List<File> {
        val itemCount: Int = activityResult.data?.clipData?.itemCount ?: 0
        val files = ArrayList<File>()
        for (i in 0 until itemCount) {
            val clipData = activityResult.data!!.clipData!!
            val uri = clipData.getItemAt(i).uri
            Log.i("uri", "$uri")
            addToFilesFromUri(uri, files, contentResolver)

        }
        return files
    }

    private fun addToFilesFromUri(
        uri: Uri,
        files: ArrayList<File>,
        contentResolver: ContentResolver
    ) {
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
                        files.add(file)
                    }
                }
            }
    }
}