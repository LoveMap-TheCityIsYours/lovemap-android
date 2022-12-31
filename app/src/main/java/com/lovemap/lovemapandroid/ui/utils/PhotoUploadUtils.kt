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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
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

        return alertDialog
    }
}