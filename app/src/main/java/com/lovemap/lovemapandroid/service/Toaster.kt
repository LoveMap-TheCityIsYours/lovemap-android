package com.lovemap.lovemapandroid.service

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.getErrorMessages
import retrofit2.Response

class Toaster(
    private val looper: Looper,
    private val context: Context,
) {
    private var lastShownTimestamp = System.currentTimeMillis()

    fun showToast(message: String) {
        doShow(message)
    }

    fun showToast(resId: Int) {
        doShow(context.getString(resId))
    }

    fun showResponseError(response: Response<out Any>) {
        doShowNullable(response.getErrorMessages().firstOrNull()?.translatedString(context))
    }

    fun showNoServerToast() {
        doShow(context.getString(R.string.noServer))
    }

    private fun doShow(message: String) {
        val currentTimeMillis = System.currentTimeMillis()
        if (haveFiveSecondsPassed(currentTimeMillis)) {
            lastShownTimestamp = currentTimeMillis
            Handler(looper).post {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun doShowNullable(message: String?) {
        val currentTimeMillis = System.currentTimeMillis()
        if (haveFiveSecondsPassed(currentTimeMillis)) {
            lastShownTimestamp = currentTimeMillis
            Handler(looper).post {
                Toast.makeText(
                    context,
                    message ?: context.getString(R.string.noServer),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun haveFiveSecondsPassed(currentTimeMillis: Long) =
        currentTimeMillis - lastShownTimestamp > 5000
}