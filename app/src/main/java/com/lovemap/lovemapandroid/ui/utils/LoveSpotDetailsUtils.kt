package com.lovemap.lovemapandroid.ui.utils

import android.content.Context
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.service.LoveSpotService

object LoveSpotDetailsUtils {

    fun setRating(
        loveSpot: LoveSpot,
        ratingBar: RatingBar
    ) {
        ratingBar.rating = loveSpot.averageRating?.toFloat() ?: 0f
    }

    fun setAvailability(
        loveSpot: LoveSpot,
        context: Context,
        availability: TextView
    ) {
        availability.text =
            if (loveSpot.availability == LoveSpotAvailabilityApiStatus.ALL_DAY) {
                context.getString(R.string.available_all_day)
            } else {
                context.getString(R.string.available_night_only)
            }
    }

    suspend fun setRisk(
        loveSpot: LoveSpot,
        loveSpotService: LoveSpotService,
        context: Context,
        risk: TextView
    ) {
        val loveSpotRisks = loveSpotService.getRisks()
        if (loveSpotRisks != null && loveSpot.averageDanger != null) {
            val riskValue = loveSpot.averageDanger!!
            val riskList = loveSpotRisks.riskList
            val loveSpotRisk = when {
                riskValue < 1.5 -> {
                    riskList[0]
                }
                riskValue < 2.5 -> {
                    riskList[1]
                }
                else -> {
                    riskList[2]
                }
            }

            risk.text = loveSpotRisk.nameEN
        } else if (loveSpot.averageDanger != null) {
            loveSpotService.getRisks()
            risk.text = loveSpot.averageDanger.toString()
        } else {
            risk.text = context.getString(R.string.risk_unknown)
        }
    }

    fun setCustomAvailability(
        loveSpot: LoveSpot,
        context: Context,
        customAvText: TextView,
        customAv: TextView
    ) {
        if (loveSpot.customAvailability != null) {
            customAv.text = loveSpot.customAvailability
            customAvText.visibility = View.VISIBLE
            customAv.visibility = View.VISIBLE
        } else {
            customAvText.visibility = View.GONE
            customAv.visibility = View.GONE
        }
        customAvText.text = context.getString(R.string.custom_availability_text)
    }
}

