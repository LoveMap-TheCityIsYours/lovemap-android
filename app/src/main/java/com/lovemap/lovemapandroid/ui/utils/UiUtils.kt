package com.lovemap.lovemapandroid.ui.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
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

fun setListItemAnimation(viewToAnimate: View, position: Int, lastPosition: Int) {
    // If the bound view wasn't previously displayed on screen, it's animated
    if (position > lastPosition) {
        val anim = ScaleAnimation(
            0.0f,
            1.0f,
            0.0f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        anim.duration = 500
        viewToAnimate.startAnimation(anim)
    }
}

fun setListItemAnimation(viewToAnimate: View) {
    val anim = ScaleAnimation(
        0.0f,
        1.0f,
        0.0f,
        1.0f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    anim.duration = 300
    viewToAnimate.startAnimation(anim)
}