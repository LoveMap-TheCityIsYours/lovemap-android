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
        rating: Double?,
        ratingBar: RatingBar
    ) {
        ratingBar.rating = rating?.toFloat() ?: 0f
    }

    fun setAvailability(
        availability: LoveSpotAvailabilityApiStatus,
        context: Context,
        availabilityView: TextView
    ) {
        availabilityView.text =
            if (availability == LoveSpotAvailabilityApiStatus.ALL_DAY) {
                context.getString(R.string.available_all_day)
            } else {
                context.getString(R.string.available_night_only)
            }
    }

    suspend fun setRisk(
        averageDanger: Double?,
        loveSpotService: LoveSpotService,
        context: Context,
        risk: TextView
    ) {
        val loveSpotRisks = loveSpotService.getRisks()
        if (loveSpotRisks != null && averageDanger != null) {
            val riskList = loveSpotRisks.riskList
            val loveSpotRisk = when {
                averageDanger < 1.5 -> {
                    riskList[0]
                }
                averageDanger < 2.5 -> {
                    riskList[1]
                }
                else -> {
                    riskList[2]
                }
            }

            risk.text = loveSpotRisk.nameEN
        } else if (averageDanger != null) {
            loveSpotService.getRisks()
            risk.text = averageDanger.toString()
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

