package com.lovemap.lovemapandroid.ui.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.lovemap.lovemapandroid.config.AppContext


fun hideKeyboard(view: View) {
    val imm: InputMethodManager? =
        getSystemService(AppContext.INSTANCE, InputMethodManager::class.java)

    imm?.hideSoftInputFromWindow(
        view.windowToken,
        InputMethodManager.RESULT_UNCHANGED_SHOWN
    )
}