package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SpotListCityFilterViewLogic(
    private val cityAutocompleteText: AutoCompleteTextView,
    private val cityGoButton: Button,
    private val locationFilterViewLogic: SpotListLocationFilterViewLogic,
) {
    private val geoLocationService = AppContext.INSTANCE.geoLocationService

    init {
        MainScope().launch {
            cityAutocompleteText.setAdapter(
                ArrayAdapter(
                    cityAutocompleteText.context,
                    android.R.layout.simple_dropdown_item_1line,
                    geoLocationService.getCitiesLocally().toTypedArray()
                )
            )
        }

        cityGoButton.setOnClickListener {
            if (cityAutocompleteText.text.isNotEmpty()) {
                LoveSpotListFilterState.listLocation = ListLocation.CITY
                LoveSpotListFilterState.locationName = cityAutocompleteText.text.toString()
                locationFilterViewLogic.closeLocationConfig()
            }
        }
    }

}