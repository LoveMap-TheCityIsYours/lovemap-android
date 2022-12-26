package com.lovemap.lovemapandroid.ui.utils

import android.app.Activity
import android.app.ProgressDialog
import android.app.ProgressDialog.STYLE_SPINNER
import android.view.WindowManager
import com.lovemap.lovemapandroid.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoadingBarShower(private val activity: Activity) {

    private lateinit var progressBar: ProgressDialog

    init {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                progressBar = ProgressDialog(activity)
            }
        }
    }

    fun show(resId: Int = R.string.loadingDotDotDot): LoadingBarShower {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                progressBar.isIndeterminate = true
                progressBar.setMessage(activity.getString(resId))
                progressBar.setCanceledOnTouchOutside(false)
                progressBar.show()
            }
        }
        return this
    }

    fun onResponse() {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                progressBar.dismiss()
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}