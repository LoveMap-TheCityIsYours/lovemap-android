package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType.*
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState

class SpotListTypeFilterViewLogic(
    private val typeFilterScrollView: HorizontalScrollView,
    private val typeFilterAll: Button,
    typeFilterPublicSpace: Button,
    typeFilterSwingerClub: Button,
    typeFilterCruisingSpot: Button,
    typeFilterSexBooth: Button,
    typeFilterNightClub: Button,
    typeFilterOtherVenue: Button,
) {
    private val filterOnResource = R.drawable.label_filter_on
    private val filterOffResource = R.drawable.label_filter_off

    private val typeToButtonMap = HashMap<LoveSpotType, Button>(LoveSpotType.values().size)
    private val buttonToTypeMap = HashMap<Button, LoveSpotType>(LoveSpotType.values().size)

    init {
        typeToButtonMap[PUBLIC_SPACE] = typeFilterPublicSpace
        typeToButtonMap[SWINGER_CLUB] = typeFilterSwingerClub
        typeToButtonMap[CRUISING_SPOT] = typeFilterCruisingSpot
        typeToButtonMap[SEX_BOOTH] = typeFilterSexBooth
        typeToButtonMap[NIGHT_CLUB] = typeFilterNightClub
        typeToButtonMap[OTHER_VENUE] = typeFilterOtherVenue

        buttonToTypeMap[typeFilterPublicSpace] = PUBLIC_SPACE
        buttonToTypeMap[typeFilterSwingerClub] = SWINGER_CLUB
        buttonToTypeMap[typeFilterCruisingSpot] = CRUISING_SPOT
        buttonToTypeMap[typeFilterSexBooth] = SEX_BOOTH
        buttonToTypeMap[typeFilterNightClub] = NIGHT_CLUB
        buttonToTypeMap[typeFilterOtherVenue] = OTHER_VENUE

        if (LoveSpotListFilterState.isAllFilterOn()) {
            allButtonClicked()
        } else {
            updateBackground(typeFilterAll, filterOffResource)
            typeToButtonMap.keys.forEach {
                if (LoveSpotListFilterState.isTypeFilterOn(it)) {
                    LoveSpotListFilterState.setTypeFilterOn(it)
                    updateBackground(typeToButtonMap[it]!!, filterOnResource)
                } else {
                    LoveSpotListFilterState.setTypeFilterOff(it)
                    updateBackground(typeToButtonMap[it]!!, filterOffResource)
                }
            }
        }

        typeFilterAll.setOnClickListener {
            allButtonClicked()
        }
        buttonToTypeMap.keys.forEach { button ->
            button.setOnClickListener {
                typeButtonClicked(button)
            }
        }

    }

    private fun typeButtonClicked(button: Button) {
        buttonToTypeMap[button]?.let {
            if (LoveSpotListFilterState.isAllFilterOn()) {
                turnAllFilterOff(button, it)
            } else if (LoveSpotListFilterState.isTypeFilterOn(it)) {
                turnTypeFilterOff(it, button)
            } else {
                turnTypeFilterOn(it, button)
            }
        }
    }

    private fun turnTypeFilterOff(
        it: LoveSpotType,
        button: Button
    ) {
        LoveSpotListFilterState.setTypeFilterOff(it)
        if (LoveSpotListFilterState.getSelectedTypes().isEmpty()) {
            allButtonClicked()
        } else {
            updateBackground(button, filterOffResource)
        }
    }

    private fun turnTypeFilterOn(
        it: LoveSpotType,
        button: Button
    ) {
        LoveSpotListFilterState.setTypeFilterOn(it)
        if (LoveSpotListFilterState.isAllFilterOn()) {
            allButtonClicked()
        } else {
            updateBackground(button, filterOnResource)
        }
    }

    private fun updateBackground(button: Button, resId: Int) {
        button.setBackgroundResource(resId)
        button.invalidate()
    }

    private fun allButtonClicked() {
        LoveSpotListFilterState.setAllFilterOn()
        updateBackground(typeFilterAll, filterOnResource)
        buttonToTypeMap.keys.forEach { updateBackground(it, filterOffResource) }
        typeFilterScrollView.fullScroll(View.FOCUS_LEFT)
    }

    private fun turnAllFilterOff(button: Button, loveSpotType: LoveSpotType) {
        LoveSpotListFilterState.setAllFilterOff(loveSpotType)
        updateBackground(button, filterOnResource)
        updateBackground(typeFilterAll, filterOffResource)
    }

    fun getOnButtons(): List<Button> {
        return if (LoveSpotListFilterState.isAllFilterOn()) {
            listOf(typeFilterAll)
        } else {
            typeToButtonMap
                .filter { LoveSpotListFilterState.isTypeFilterOn(it.key) }
                .map { it.value }
        }
    }

    fun getOffButtons(): List<Button> {
        return if (LoveSpotListFilterState.isAllFilterOn()) {
            typeToButtonMap.map { it.value }
        } else {
            typeToButtonMap
                .filter { !LoveSpotListFilterState.isTypeFilterOn(it.key) }
                .map { it.value }
        }
    }
}
