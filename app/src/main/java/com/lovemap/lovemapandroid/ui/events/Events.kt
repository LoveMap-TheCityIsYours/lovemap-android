package com.lovemap.lovemapandroid.ui.events

import com.google.android.gms.maps.model.Marker
import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotSearchRequest
import com.lovemap.lovemapandroid.api.lovespot.RecommendationsResponse
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItem
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.data.LoveHolder
import com.lovemap.lovemapandroid.ui.data.WishlistItemDto
import com.lovemap.lovemapandroid.ui.data.WishlistItemHolder
import java.util.*

data class MapInfoWindowShownEvent(val marker: Marker, val loveSpot: LoveSpot?)

data class LoveSpotWidgetMoreClicked(val ordering: ListOrdering)

data class LoveSpotListFiltersChanged(
    val request: LoveSpotSearchRequest,
    val listOrdering: ListOrdering,
    val listLocation: ListLocation,
)

data class LocationUpdated(val value: LatLng)

data class RecommendationsUpdated(val recommendations: RecommendationsResponse)

data class ShowOnMapClickedEvent(val loveSpotId: Long)

data class LoveSpotPhotoDeleted(val remainingPhotos: List<LoveSpotPhoto>)

data class WishlistUpdatedEvent(
    val wishlistHolders: List<WishlistItemHolder>?
)

data class LoveListUpdatedEvent(
    val loveHolders: List<LoveHolder>
)
