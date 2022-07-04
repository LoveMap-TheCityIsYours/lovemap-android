package com.lovemap.lovemapandroid.ui.events

import com.google.android.gms.maps.model.Marker
import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

data class MapInfoWindowShownEvent(val marker: Marker, val loveSpot: LoveSpot?)

data class LoveSpotWidgetMoreClicked(val ordering: ListOrdering)

class LoveSpotListFiltersChanged

class LocationUpdated(value: LatLng)
