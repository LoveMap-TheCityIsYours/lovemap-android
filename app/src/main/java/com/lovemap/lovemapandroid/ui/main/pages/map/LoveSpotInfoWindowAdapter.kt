package com.lovemap.lovemapandroid.ui.main.pages.map

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.service.lovespot.LoveSpotService
import com.lovemap.lovemapandroid.ui.events.MapInfoWindowShownEvent
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

class LoveSpotInfoWindowAdapter(
    private val loveSpotService: LoveSpotService,
    private val activity: Activity,
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        val view = activity.layoutInflater.inflate(
            R.layout.marker_info_window_layout,
            null
        )
        runBlocking {
            val loveSpot =
                loveSpotService.findLocally(marker.snippet?.toLong() ?: -1)
            EventBus.getDefault().post(MapInfoWindowShownEvent(marker, loveSpot))
            if (loveSpot != null) {
                setTexts(view, loveSpot)
                LoveSpotUtils.setRating(
                    loveSpot.averageRating,
                    view.findViewById(R.id.marker_review_rating_bar)
                )
                LoveSpotUtils.setAvailability(
                    loveSpot.availability,
                    view.findViewById(R.id.marker_availability)
                )
                LoveSpotUtils.setType(
                    loveSpot.type,
                    view.findViewById(R.id.marker_type)
                )
                LoveSpotUtils.setRisk(
                    loveSpot.averageDanger,
                    view.findViewById(R.id.marker_risk)
                )
                LoveSpotUtils.setTypeImage(
                    loveSpot.type,
                    view.findViewById(R.id.markerTypeImage)
                )
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

    override fun getInfoContents(p0: Marker): View? {
        // Only overriding content
        return null
    }
}