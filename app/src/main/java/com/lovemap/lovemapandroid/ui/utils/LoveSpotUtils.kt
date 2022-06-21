package com.lovemap.lovemapandroid.ui.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.api.lovespot.Type
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object LoveSpotUtils {

    fun setRating(
        rating: Double?,
        ratingBar: RatingBar
    ) {
        ratingBar.rating = rating?.toFloat() ?: 0f
    }

    fun setAvailability(
        availability: Availability,
        availabilityView: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        availabilityView.text =
            if (availability == Availability.ALL_DAY) {
                context.getString(R.string.available_all_day)
            } else {
                context.getString(R.string.available_night_only)
            }
    }

    fun setType(
        type: Type,
        typeView: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        typeView.text = when (type) {
                Type.PUBLIC_SPACE -> context.getString(R.string.type_public_space)
                Type.SWINGER_CLUB -> context.getString(R.string.type_swinger_club)
                Type.CRUISING_SPOT -> context.getString(R.string.type_cruising_spot)
                Type.SEX_BOOTH -> context.getString(R.string.type_sex_booth)
                Type.NIGHT_CLUB -> context.getString(R.string.type_night_club)
                Type.OTHER_VENUE -> context.getString(R.string.type_other_venue)
            }
    }

    fun setRisk(
        averageDanger: Double?,
        risk: TextView
    ) {
        val loveSpotService = AppContext.INSTANCE.loveSpotService
        val context = AppContext.INSTANCE.applicationContext
        val loveSpotRisks = AppContext.INSTANCE.loveSpotRisks
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
            MainScope().launch {
                loveSpotService.getRisks()
            }
            risk.text = averageDanger.toString()
        } else {
            risk.text = context.getString(R.string.risk_unknown)
        }
    }

    fun setCustomAvailability(
        loveSpot: LoveSpot,
        customAvText: TextView,
        customAv: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
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

    fun positionToAvailability(position: Int) =
        if (position == 0) {
            Availability.ALL_DAY
        } else {
            Availability.NIGHT_ONLY
        }

    fun availabilityToPosition(availability: Availability): Int {
        return when (availability) {
            Availability.ALL_DAY -> 0
            Availability.NIGHT_ONLY -> 1
        }
    }

    fun positionToType(position: Int) =
        when (position) {
            0 -> Type.PUBLIC_SPACE
            1 -> Type.SWINGER_CLUB
            2 -> Type.CRUISING_SPOT
            3 -> Type.SEX_BOOTH
            4 -> Type.NIGHT_CLUB
            else -> Type.OTHER_VENUE
        }

    fun typeToPosition(type: Type): Int {
        return when (type) {
            Type.PUBLIC_SPACE -> 0
            Type.SWINGER_CLUB -> 1
            Type.CRUISING_SPOT -> 2
            Type.SEX_BOOTH -> 3
            Type.NIGHT_CLUB -> 4
            Type.OTHER_VENUE -> 5
        }
    }

    fun setTypeImage(type: Type, imageView: ImageView) {
        when (type) {
            Type.PUBLIC_SPACE -> imageView.setImageResource(R.drawable.ic_public_space_3)
            Type.SWINGER_CLUB -> imageView.setImageResource(R.drawable.ic_swinger_club_2)
            Type.CRUISING_SPOT -> imageView.setImageResource(R.drawable.ic_cruising_spot_1)
            Type.SEX_BOOTH -> imageView.setImageResource(R.drawable.ic_sex_booth_1)
            Type.NIGHT_CLUB -> imageView.setImageResource(R.drawable.ic_night_club_2)
            Type.OTHER_VENUE -> imageView.setImageResource(R.drawable.ic_other_venue_1)
        }
    }
}

