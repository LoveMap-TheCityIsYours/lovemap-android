package com.lovemap.lovemapandroid.ui.events

import com.google.android.gms.maps.model.Marker
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

data class MapInfoWindowShownEvent(val marker: Marker, val loveSpot: LoveSpot?)
