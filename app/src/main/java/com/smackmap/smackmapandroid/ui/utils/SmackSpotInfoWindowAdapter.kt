package com.smackmap.smackmapandroid.ui.utils

import android.app.Activity
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotAvailabilityApiStatus
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotRisks
import com.smackmap.smackmapandroid.data.smackspot.SmackSpot
import com.smackmap.smackmapandroid.service.smackspot.SmackSpotService
import com.smackmap.smackmapandroid.ui.events.MapInfoWindowShownEvent
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

class SmackSpotInfoWindowAdapter(
    private val smackSpotService: SmackSpotService,
    private val activity: Activity,
    private var smackSpotRisks: SmackSpotRisks?
) : GoogleMap.InfoWindowAdapter {

    suspend fun initSmackSpotRisks() {
        smackSpotRisks = smackSpotService.getRisks()
    }

    override fun getInfoWindow(marker: Marker): View? {
        val view = activity.layoutInflater.inflate(
            R.layout.marker_info_window_layout,
            null
        )
        EventBus.getDefault().post(MapInfoWindowShownEvent(marker))
        runBlocking {
            val smackSpot =
                smackSpotService.findLocally(marker.snippet?.toLong() ?: -1)
            if (smackSpot != null) {
                setTexts(view, smackSpot)
                setRating(view, smackSpot)
                setAvailability(view, smackSpot)
                setRisk(smackSpot, view)
                setCustomAvailability(view, smackSpot)
            }
        }
        return view
    }

    private fun setTexts(
        view: View,
        smackSpot: SmackSpot
    ) {
        val title: TextView = view.findViewById(R.id.marker_title)
        title.text = smackSpot.name
        val description: TextView = view.findViewById(R.id.marker_description)
        description.text = smackSpot.description
    }

    private fun setRating(
        view: View,
        smackSpot: SmackSpot
    ) {
        val ratingBar: RatingBar = view.findViewById(R.id.marker_rating)
        smackSpot.averageRating?.let {
            ratingBar.rating = it.toFloat()
        } ?: run {
            ratingBar.rating = 0f
        }
    }

    private fun setAvailability(
        view: View,
        smackSpot: SmackSpot
    ) {
        val availability: TextView = view.findViewById(R.id.marker_availability)
        availability.text =
            if (smackSpot.availability == SmackSpotAvailabilityApiStatus.ALL_DAY) {
                activity.getString(R.string.available_all_day)
            } else {
                activity.getString(R.string.available_night_only)
            }
    }

    private suspend fun setRisk(
        smackSpot: SmackSpot,
        view: View
    ) {
        val risk: TextView = view.findViewById(R.id.marker_risk)
        if (smackSpotRisks != null && smackSpot.averageDanger != null) {
            val riskValue = smackSpot.averageDanger!!
            val riskList = smackSpotRisks!!.riskList
            val smackSpotRisk = when {
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

            risk.text = smackSpotRisk.nameEN
        } else if (smackSpot.averageDanger != null) {
            initSmackSpotRisks()
            risk.text = smackSpot.averageDanger.toString()
        } else {
            initSmackSpotRisks()
            risk.text = activity.getString(R.string.risk_unknown)
        }
    }

    private fun setCustomAvailability(
        view: View,
        smackSpot: SmackSpot
    ) {
        val customAvText: TextView = view.findViewById(R.id.marker_custom_availability_text)
        val customAv: TextView = view.findViewById(R.id.marker_custom_availability)
        if (smackSpot.customAvailability != null) {
            customAv.text = smackSpot.customAvailability
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