package com.lovemap.lovemapandroid.ui.utils

import android.app.Activity
import android.app.AlertDialog
import com.lovemap.lovemapandroid.R

object AlertDialogUtils {

    fun newDialog(
        activity: Activity,
        titleResId: Int,
        messageResId: Int,
        yesAction: () -> Unit,
        noAction: () -> Unit = {}
    ): AlertDialog {
        val alertDialog = AlertDialog.Builder(activity, R.style.MyDialogTheme)
            .setTitle(activity.getString(titleResId))
            .setMessage(activity.getString(messageResId))
            .setPositiveButton(activity.getString(R.string.yes)) { dialog, _ -> // Do nothing but close the dialog
                yesAction.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.no)) { dialog, _ -> // Do nothing
                noAction.invoke()
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