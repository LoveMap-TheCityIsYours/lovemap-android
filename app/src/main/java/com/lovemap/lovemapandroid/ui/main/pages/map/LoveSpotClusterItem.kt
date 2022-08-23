package com.lovemap.lovemapandroid.ui.main.pages.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

class LoveSpotClusterItem(
    val loveSpot: LoveSpot,
    lat: Double,
    lng: Double,
    title: String,
    snippet: String
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
    }

    companion object {
        fun ofLoveSpot(loveSpot: LoveSpot): LoveSpotClusterItem {
            return LoveSpotClusterItem(
                loveSpot = loveSpot,
                lat = loveSpot.latitude,
                lng = loveSpot.longitude,
                title = loveSpot.name,
                snippet = loveSpot.id.toString()
            )
        }
    }
}