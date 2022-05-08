package com.lovemap.lovemapandroid.ui.utils

import android.app.Activity
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotRisks
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.service.LoveSpotService
import com.lovemap.lovemapandroid.ui.events.MapInfoWindowShownEvent
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

class LoveSpotInfoWindowAdapter(
    private val loveSpotService: LoveSpotService,
    private val activity: Activity,
    private var loveSpotRisks: LoveSpotRisks?
) : GoogleMap.InfoWindowAdapter {

    suspend fun initLoveSpotRisks() {
        loveSpotRisks = loveSpotService.getRisks()
    }

    override fun getInfoWindow(marker: Marker): View? {
        val view = activity.layoutInflater.inflate(
            R.layout.marker_info_window_layout,
            null
        )
        EventBus.getDefault().post(MapInfoWindowShownEvent(marker))
        runBlocking {
            val loveSpot =
                loveSpotService.findLocally(marker.snippet?.toLong() ?: -1)
            if (loveSpot != null) {
                setTexts(view, loveSpot)
                setRating(view, loveSpot)
                setAvailability(view, loveSpot)
                setRisk(loveSpot, view)
                setCustomAvailability(view, loveSpot)
            }
        }
        return view
    }

    private fun setTexts(
        view: View,
        loveSpot: LoveSpot
    ) {
        val title: TextView = view.findViewById(R.id.marker_title)
        title.text = loveSpot.name
        val description: TextView = view.findViewById(R.id.marker_description)
        description.text = loveSpot.description
    }

    private fun setRating(
        view: View,
        loveSpot: LoveSpot
    ) {
        val ratingBar: RatingBar = view.findViewById(R.id.spotReviewRating)
        loveSpot.averageRating?.let {
            ratingBar.rating = it.toFloat()
        } ?: run {
            ratingBar.rating = 0f
        }
    }

    private fun setAvailability(
        view: View,
        loveSpot: LoveSpot
    ) {
        val availability: TextView = view.findViewById(R.id.marker_availability)
        availability.text =
            if (loveSpot.availability == LoveSpotAvailabilityApiStatus.ALL_DAY) {
                activity.getString(R.string.available_all_day)
            } else {
                activity.getString(R.string.available_night_only)
            }
    }

    private suspend fun setRisk(
        loveSpot: LoveSpot,
        view: View
    ) {
        val risk: TextView = view.findViewById(R.id.marker_risk)
        if (loveSpotRisks != null && loveSpot.averageDanger != null) {
            val riskValue = loveSpot.averageDanger!!
            val riskList = loveSpotRisks!!.riskList
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
            initLoveSpotRisks()
            risk.text = loveSpot.averageDanger.toString()
        } else {
            risk.text = activity.getString(R.string.risk_unknown)
        }
    }

    private fun setCustomAvailability(
        view: View,
        loveSpot: LoveSpot
    ) {
        val customAvText: TextView = view.findViewById(R.id.marker_custom_availability_text)
        val customAv: TextView = view.findViewById(R.id.marker_custom_availability)
        if (loveSpot.customAvailability != null) {
            customAv.text = loveSpot.customAvailability
        } else {
            customAvText.visibility = View.GONE
            customAv.visibility = View.GONE
        }
        customAvText.text = activity.getString(R.string.custom_availability_text)
    }

    override fun getInfoContents(p0: Marker): View? {
        // Only overriding content
        return null
    }
}