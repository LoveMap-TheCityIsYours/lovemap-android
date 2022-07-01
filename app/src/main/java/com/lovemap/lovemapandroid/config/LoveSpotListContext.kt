package com.lovemap.lovemapandroid.config

import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.api.lovespot.ListLocation.COORDINATE
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering.POPULAR

object LoveSpotListContext {
    public var listOrdering: ListOrdering = POPULAR
    public var listLocation: ListLocation = COORDINATE
    public var center: LatLng? = null
    public var distanceInMeters: Int = 1
    public var locationName: String? = null
}