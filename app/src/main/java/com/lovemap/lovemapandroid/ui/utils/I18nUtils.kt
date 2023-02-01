package com.lovemap.lovemapandroid.ui.utils

import android.content.Context
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.relation.RelationStatus
import com.lovemap.lovemapandroid.api.lover.relation.RelationStatus.*
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus.IN_PARTNERSHIP
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus.PARTNERSHIP_REQUESTED
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
                    "en" -> context.getString(R.string.in_partnership)
                    else -> context.getString(R.string.in_partnership)
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

    fun partnershipStatus(partnershipStatus: PartnershipStatus, context: Context): String {
        val language = Locale.getDefault().language
        return when (partnershipStatus) {
            PARTNERSHIP_REQUESTED -> {
                when (language) {
                    "en" -> context.getString(R.string.partnership_requested)
                    else -> context.getString(R.string.partnership_requested)
                }
            }
            IN_PARTNERSHIP -> {
                when (language) {
                    "en" -> context.getString(R.string.in_partnership)
                    else -> context.getString(R.string.in_partnership)
                }
            }
        }
    }
}

