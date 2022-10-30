package com.lovemap.lovemapandroid.config

import com.google.android.gms.maps.model.Marker
import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.events.LocationUpdated
import com.lovemap.lovemapandroid.ui.events.MapMarkerEventListener
import org.greenrobot.eventbus.EventBus

object MapContext {
    lateinit var mapMarkerEventListener: MapMarkerEventListener

    @Volatile
    lateinit var mapCameraTarget: com.google.android.gms.maps.model.LatLng

    @Volatile
    var areMarkerFabsOpen = false

    @Volatile
    var areAddLoveSpotFabsOpen = false

    @Volatile
    var shouldCloseFabs = false

    @Volatile
    var zoomOnLoveSpot: LoveSpot? = null

    @Volatile
    var shouldClearMap: Boolean = false

    @Volatile
    var locationEnabled: Boolean = false

    @Volatile
    var selectedMarker: Marker? = null

    @Volatile
    var lastLocation: LatLng? = null
        set(value) {
            field = value
            if (value != null) {
                EventBus.getDefault().post(LocationUpdated(value))
            }
        }
}