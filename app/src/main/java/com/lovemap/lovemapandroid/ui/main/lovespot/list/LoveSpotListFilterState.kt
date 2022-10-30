package com.lovemap.lovemapandroid.ui.main.lovespot.list

import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotSearchRequest
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.ui.events.LoveSpotListFiltersChanged
import org.greenrobot.eventbus.EventBus

object LoveSpotListFilterState {
    private const val defaultLimit = 100
    private val appContext = AppContext.INSTANCE
    var initialized = false

    var listLocation = ListLocation.COUNTRY
    var listOrdering = ListOrdering.TOP_RATED
    var distanceInMeters: Int = 5000
    var locationName: String? = "Hungary"

    private var currentLocation: LatLng? = null
    private val selectedTypes = HashSet<LoveSpotType>(LoveSpotType.values().size)

    init {
        selectedTypes.addAll(LoveSpotType.values())
    }

    fun setTypeFilterOn(loveSpotType: LoveSpotType) {
        selectedTypes.add(loveSpotType)
        sendFiltersChangedEvent()
    }

    fun setTypeFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.remove(loveSpotType)
        if (selectedTypes.isNotEmpty()) {
            sendFiltersChangedEvent()
        }
    }

    fun isTypeFilterOn(loveSpotType: LoveSpotType): Boolean {
        return selectedTypes.contains(loveSpotType)
    }

    fun setAllFilterOn() {
        selectedTypes.addAll(LoveSpotType.values())
        sendFiltersChangedEvent()
    }

    fun setAllFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.clear()
        selectedTypes.add(loveSpotType)
        sendFiltersChangedEvent()
    }

    fun isAllFilterOn(): Boolean {
        return selectedTypes.size == LoveSpotType.values().size
    }

    fun sendFiltersChangedEvent() {
        if (initialized) {
            EventBus.getDefault().post(LoveSpotListFiltersChanged(
                request = createSearchRequest(),
                listOrdering = listOrdering,
                listLocation = listLocation
            ))
        }
    }

    private fun createSearchRequest(): LoveSpotSearchRequest {
        currentLocation = MapContext.lastLocation
        return LoveSpotSearchRequest(
            limit = defaultLimit,
            latitude = currentLocation?.latitude,
            longitude = currentLocation?.longitude,
            distanceInMeters = distanceInMeters,
            locationName = locationName,
            typeFilter = getSelectedTypes()
        )
    }

    fun getSelectedTypes(): List<LoveSpotType> {
        return selectedTypes.toList()
    }
}
