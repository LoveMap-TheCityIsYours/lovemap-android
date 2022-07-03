package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType

object AdvSpotListFilterState {

    private val selectedTypes = HashSet<LoveSpotType>(LoveSpotType.values().size)
    
    init {
        selectedTypes.addAll(LoveSpotType.values())
    }

    fun setTypeFilterOn(loveSpotType: LoveSpotType) {
        selectedTypes.add(loveSpotType)
    }

    fun setTypeFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.remove(loveSpotType)
    }

    fun isTypeFilterOn(loveSpotType: LoveSpotType): Boolean {
        return selectedTypes.contains(loveSpotType)
    }

    fun setAllFilterOn() {
        selectedTypes.addAll(LoveSpotType.values())
    }

    fun setAllFilterOff(loveSpotType: LoveSpotType) {
        selectedTypes.clear()
        selectedTypes.add(loveSpotType)
    }

    fun isAllFilterOn(): Boolean {
        return selectedTypes.size == LoveSpotType.values().size
    }

}