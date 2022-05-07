package com.lovemap.lovemapandroid.service

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.lovemap.lovemapandroid.R

class Toaster(
    private val looper: Looper,
    private val context: Context,
) {

    fun showToast(message: String) {
        Handler(looper).post {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun showToast(resId: Int) {
        Handler(looper).post {
            Toast.makeText(
                context,
                context.getString(resId),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun showNoServerToast() {
        Handler(looper).post {
            Toast.makeText(
                context,
                context.getString(R.string.noServer),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}