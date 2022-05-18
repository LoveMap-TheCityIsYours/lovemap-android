package com.lovemap.lovemapandroid.ui.utils

import android.content.Context
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.relation.RelationStatus
import com.lovemap.lovemapandroid.api.relation.RelationStatus.*
import java.util.*

object I18nUtils {

    fun relationStatus(relationStatus: RelationStatus, context: Context): String {
        val language = Locale.getDefault().language
        return when (relationStatus) {
            NOTHING -> {
                when (language) {
                    "en" -> context.getString(R.string.nothing)
                    else -> context.getString(R.string.nothing)
                }
            }
            FOLLOWING -> {
                when (language) {
                    "en" -> context.getString(R.string.following)
                    else -> context.getString(R.string.following)
                }
            }
            PARTNER -> {
                when (language) {
                    "en" -> context.getString(R.string.partner)
                    else -> context.getString(R.string.partner)
                }
            }
            BLOCKED -> {
                when (language) {
                    "en" -> context.getString(R.string.blocked)
                    else -> context.getString(R.string.blocked)
                }
            }
        }
    }
}

