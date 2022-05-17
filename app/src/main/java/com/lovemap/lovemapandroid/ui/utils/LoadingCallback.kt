package com.lovemap.lovemapandroid.ui.utils

import android.app.Activity
import android.app.ProgressDialog
import android.view.WindowManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoadingCallback(private val activity: Activity) {

    private lateinit var progressBar: ProgressDialog

    init {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                progressBar = ProgressDialog(activity)
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                progressBar.setIndeterminate(true)
                progressBar.setMessage("Loading...")
                progressBar.setCanceledOnTouchOutside(false)
                progressBar.show()
            }
        }
    }

    fun onResponse() {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss()
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }
}