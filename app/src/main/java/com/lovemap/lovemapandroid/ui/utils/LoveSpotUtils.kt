package com.lovemap.lovemapandroid.ui.utils

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
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
        type: LoveSpotType,
        typeView: TextView
    ) {
        val context = AppContext.INSTANCE.applicationContext
        typeView.text = when (type) {
            LoveSpotType.PUBLIC_SPACE -> context.getString(R.string.type_public_space)
            LoveSpotType.SWINGER_CLUB -> context.getString(R.string.type_swinger_club)
            LoveSpotType.CRUISING_SPOT -> context.getString(R.string.type_cruising_spot)
            LoveSpotType.SEX_BOOTH -> context.getString(R.string.type_sex_booth)
            LoveSpotType.NIGHT_CLUB -> context.getString(R.string.type_night_club)
            LoveSpotType.OTHER_VENUE -> context.getString(R.string.type_other_venue)
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

    fun positionToType(position: Int): LoveSpotType = when (position) {
        0 -> LoveSpotType.PUBLIC_SPACE
        1 -> LoveSpotType.SWINGER_CLUB
        2 -> LoveSpotType.CRUISING_SPOT
        3 -> LoveSpotType.SEX_BOOTH
        4 -> LoveSpotType.NIGHT_CLUB
        else -> LoveSpotType.OTHER_VENUE
    }

    fun typeToPosition(type: LoveSpotType): Int = when (type) {
        LoveSpotType.PUBLIC_SPACE -> 0
        LoveSpotType.SWINGER_CLUB -> 1
        LoveSpotType.CRUISING_SPOT -> 2
        LoveSpotType.SEX_BOOTH -> 3
        LoveSpotType.NIGHT_CLUB -> 4
        LoveSpotType.OTHER_VENUE -> 5
    }

    fun positionToOrdering(position: Int): ListOrdering = when (position) {
        0 -> ListOrdering.TOP_RATED
        1 -> ListOrdering.CLOSEST
        2 -> ListOrdering.RECENTLY_ACTIVE
        else -> ListOrdering.POPULAR
    }

    fun orderingToPosition(ordering: ListOrdering): Int = when (ordering) {
        ListOrdering.TOP_RATED -> 0
        ListOrdering.CLOSEST -> 1
        ListOrdering.RECENTLY_ACTIVE -> 2
        ListOrdering.POPULAR -> 3
    }

    fun setTypeImage(type: LoveSpotType, imageView: ImageView) {
        when (type) {
            LoveSpotType.PUBLIC_SPACE -> imageView.setImageResource(R.drawable.ic_public_space_3)
            LoveSpotType.SWINGER_CLUB -> imageView.setImageResource(R.drawable.ic_swinger_club_2)
            LoveSpotType.CRUISING_SPOT -> imageView.setImageResource(R.drawable.ic_cruising_spot_1)
            LoveSpotType.SEX_BOOTH -> imageView.setImageResource(R.drawable.ic_sex_booth_1)
            LoveSpotType.NIGHT_CLUB -> imageView.setImageResource(R.drawable.ic_night_club_2)
            LoveSpotType.OTHER_VENUE -> imageView.setImageResource(R.drawable.ic_other_venue_1)
        }
    }

    fun setDistance(distanceKm: Double?, loveSpotItemDistance: TextView) {
        distanceKm?.let {
            loveSpotItemDistance.visibility = View.VISIBLE
            if (it < 1.0) {
                loveSpotItemDistance.text = (it * 1000).toInt().toString() + " m"
            } else {
                loveSpotItemDistance.text = String.format("%.1f", it) + " km"
            }
        } ?: run {
            loveSpotItemDistance.visibility = View.GONE
        }
    }
}

