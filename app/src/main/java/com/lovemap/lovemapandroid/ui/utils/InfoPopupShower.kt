package com.lovemap.lovemapandroid.ui.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import com.lovemap.lovemapandroid.R


class InfoPopupShower(private val resId: Int) {

    fun show(view: View) {
        // inflate the layout of the popup window
        val inflater = getSystemService(view.context, LayoutInflater::class.java)
        val popupView: View? = inflater?.inflate(R.layout.info_popup_window_layout, null)

        val textView: TextView? = popupView?.findViewById(R.id.infoPopupText)
        textView?.text = view.context.getString(resId)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.animationStyle = R.style.PopupAnimation

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // dismiss the popup window when touched
        popupView?.setOnTouchListener { _, _ ->
            popupWindow.dismiss()
            true
        }
    }

}