package com.lovemap.lovemapandroid.ui.main.lovespot.list

import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotSearchRequest
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.events.LoveSpotListFiltersChanged
import org.greenrobot.eventbus.EventBus

object LoveSpotListFilterState {
    private const val defaultLimit = 100
    private val appContext = AppContext.INSTANCE
    var initialized = false

    var listOrdering = ListOrdering.TOP_RATED
        set(value) {
            field = value
            sendChangeEvent()
        }

    var listLocation = ListLocation.COORDINATE

    var locationName: String? = "Hungary"
        set(value) {
            field = value
            sendChangeEvent()
        }

    var distanceInMeters: Int = 5000
        set(value) {
            field = value
            sendChangeEvent()
        }

    private var currentLocation: LatLng? = null
    private val selectedTypes = HashSet<LoveSpotType>(LoveSpotType.values().size)

    init {
        selectedTypes.addAll(LoveSpotType.values())
    }

    fun setTypeFilterOn(loveSpotType: LoveSpotType) {
        selectedTypes.add(loveSpotType)
        sendChangeEvent()
    }

    fun setTypeFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.remove(loveSpotType)
        if (selectedTypes.isNotEmpty()) {
            sendChangeEvent()
        }
    }

    fun isTypeFilterOn(loveSpotType: LoveSpotType): Boolean {
        return selectedTypes.contains(loveSpotType)
    }

    fun setAllFilterOn() {
        selectedTypes.addAll(LoveSpotType.values())
        sendChangeEvent()
    }

    fun setAllFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.clear()
        selectedTypes.add(loveSpotType)
        sendChangeEvent()
    }

    fun isAllFilterOn(): Boolean {
        return selectedTypes.size == LoveSpotType.values().size
    }

    private fun sendChangeEvent() {
        if (initialized) {
            EventBus.getDefault().post(LoveSpotListFiltersChanged(
                request = createSearchRequest(),
                listOrdering = listOrdering,
                listLocation = listLocation
            ))
        }
    }

    fun createSearchRequest(): LoveSpotSearchRequest {
        currentLocation = appContext.lastLocation
        return LoveSpotSearchRequest(
            limit = defaultLimit,
            lat = currentLocation?.latitude,
            long = currentLocation?.longitude,
            distanceInMeters = distanceInMeters,
            locationName = locationName,
            typeFilter = getSelectedTypes()
        )
    }

    fun getSelectedTypes(): List<LoveSpotType> {
        return selectedTypes.toList()
    }
}
