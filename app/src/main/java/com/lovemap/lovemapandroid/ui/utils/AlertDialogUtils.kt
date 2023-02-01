package com.lovemap.lovemapandroid.ui.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.lovemap.lovemapandroid.R


object AlertDialogUtils {
    private const val TAG = "AlertDialogUtils"

    fun newAlert(
        activity: Activity,
        titleResId: Int,
        messageResId: Int
    ): AlertDialog {
        val alertDialog = AlertDialog.Builder(activity, R.style.MyDialogTheme)
            .setTitle(activity.getString(titleResId))
            .setMessage(activity.getString(messageResId)).create()
        alertDialog.show()
        return alertDialog
    }

    fun newDialog(
        activity: Activity,
        titleResId: Int,
        messageResId: Int,
        yesAction: () -> Unit,
        noAction: () -> Unit = {},
        isHtml: Boolean = false,
    ): AlertDialog {
        return newDialog(
            activity,
            titleResId,
            activity.getString(messageResId),
            yesAction,
            noAction,
            isHtml
        )
    }

    fun newDialog(
        activity: Activity,
        titleResId: Int,
        message: String,
        yesAction: () -> Unit,
        noAction: () -> Unit = {},
        isHtml: Boolean = false
    ): AlertDialog {
        Log.i(TAG, "message: $message")
        val dialogBuilder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
            .setTitle(activity.getString(titleResId))
            .setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY))
            .setPositiveButton(activity.getString(R.string.yes)) { dialog, _ -> // Do nothing but close the dialog
                yesAction.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.no)) { dialog, _ -> // Do nothing
                noAction.invoke()
                dialog.dismiss()
            }
            .setCancelable(false)
        if (isHtml) {
            dialogBuilder.setMessage(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY))
        } else {
            dialogBuilder.setMessage(message)
        }
        val alertDialog = dialogBuilder.create()

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
        if (isHtml) {
            val textView = alertDialog.findViewById(android.R.id.message) as TextView
            textView.movementMethod = LinkMovementMethod.getInstance()
//            textView.setTextColor(ResourcesCompat.getColor(activity.resources, R.color.myLinkColor, R.style.Theme_Lovemapandroid))
        }
        return alertDialog
    }
}

class LoveMapAlertDialog(context: Context): AlertDialog(context) {

}