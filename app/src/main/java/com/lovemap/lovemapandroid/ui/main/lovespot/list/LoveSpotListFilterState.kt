package com.lovemap.lovemapandroid.ui.main.lovespot.list

import com.javadocmd.simplelatlng.LatLng
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotSearchRequest
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.ui.events.LoveSpotListFiltersChanged
import org.greenrobot.eventbus.EventBus

object LoveSpotListFilterState {
    private const val defaultLimit = 20

    var listOrdering = ListOrdering.POPULAR
        set(value) {
            field = value
            EventBus.getDefault().post(LoveSpotListFiltersChanged())
        }
    var listLocation = ListLocation.COUNTRY
        set(value) {
            field = value
            EventBus.getDefault().post(LoveSpotListFiltersChanged())
        }
    private var locationName: String? = "Hungary"
    private var currentLocation: LatLng? = null
    private var distanceInMeters: Int = 500
    private val selectedTypes = HashSet<LoveSpotType>(LoveSpotType.values().size)

    init {
        selectedTypes.addAll(LoveSpotType.values())
    }

    fun setTypeFilterOn(loveSpotType: LoveSpotType) {
        selectedTypes.add(loveSpotType)
        EventBus.getDefault().post(LoveSpotListFiltersChanged())
    }

    fun setTypeFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.remove(loveSpotType)
        if (selectedTypes.isNotEmpty()) {
            EventBus.getDefault().post(LoveSpotListFiltersChanged())
        }
    }

    fun isTypeFilterOn(loveSpotType: LoveSpotType): Boolean {
        return selectedTypes.contains(loveSpotType)
    }

    fun setAllFilterOn() {
        selectedTypes.addAll(LoveSpotType.values())
        EventBus.getDefault().post(LoveSpotListFiltersChanged())
    }

    fun setAllFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.clear()
        selectedTypes.add(loveSpotType)
        EventBus.getDefault().post(LoveSpotListFiltersChanged())
    }

    fun isAllFilterOn(): Boolean {
        return selectedTypes.size == LoveSpotType.values().size
    }

    fun createSearchRequest(): LoveSpotSearchRequest {
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
