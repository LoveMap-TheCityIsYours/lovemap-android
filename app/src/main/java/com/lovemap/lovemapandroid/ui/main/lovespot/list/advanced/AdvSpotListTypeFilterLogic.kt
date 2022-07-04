package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType.*
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvSpotListFilterState.isAllFilterOn
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvSpotListFilterState.isTypeFilterOn
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvSpotListFilterState.setAllFilterOff
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvSpotListFilterState.setAllFilterOn
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvSpotListFilterState.setTypeFilterOff
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvSpotListFilterState.setTypeFilterOn

class AdvSpotListTypeFilterLogic(
    private val typeFilterScrollView: HorizontalScrollView,
    private val typeFilterAll: Button,
    private val typeFilterPublicSpace: Button,
    private val typeFilterSwingerClub: Button,
    private val typeFilterCruisingSpot: Button,
    private val typeFilterSexBooth: Button,
    private val typeFilterNightClub: Button,
    private val typeFilterOtherVenue: Button,
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

        if (isAllFilterOn()) {
            allButtonClicked()
        } else {
            updateBackground(typeFilterAll, filterOffResource)
            typeToButtonMap.keys.forEach {
                if (isTypeFilterOn(it)) {
                    setTypeFilterOn(it)
                    updateBackground(typeToButtonMap[it]!!, filterOnResource)
                } else {
                    setTypeFilterOff(it)
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
            if (isAllFilterOn()) {
                turnAllFilterOff(button, it)
            } else if (isTypeFilterOn(it)) {
                setTypeFilterOff(it)
                updateBackground(button, filterOffResource)
            } else {
                setTypeFilterOn(it)
                if (isAllFilterOn()) {
                    allButtonClicked()
                } else {
                    updateBackground(button, filterOnResource)
                }
            }
        }
    }

    private fun updateBackground(button: Button, resId: Int) {
        button.setBackgroundResource(resId)
        button.invalidate()
    }

    private fun allButtonClicked() {
        setAllFilterOn()
        updateBackground(typeFilterAll, filterOnResource)
        buttonToTypeMap.keys.forEach { updateBackground(it, filterOffResource) }
        typeFilterScrollView.fullScroll(View.FOCUS_LEFT)
    }

    private fun turnAllFilterOff(button: Button, loveSpotType: LoveSpotType) {
        setAllFilterOff(loveSpotType)
        updateBackground(button, filterOnResource)
        updateBackground(typeFilterAll, filterOffResource)
    }

    fun getOnButtons(): List<Button> {
        return if (isAllFilterOn()) {
            listOf(typeFilterAll)
        } else {
            typeToButtonMap
                .filter { isTypeFilterOn(it.key) }
                .map { it.value }
        }
    }

    fun getOffButtons(): List<Button> {
        return if (isAllFilterOn()) {
            typeToButtonMap.map { it.value }
        } else {
            typeToButtonMap
                .filter { !isTypeFilterOn(it.key) }
                .map { it.value }
        }
    }
}
