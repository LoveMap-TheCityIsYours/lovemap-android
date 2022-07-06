package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SpotListCountryFilterViewLogic(
    private val countryAutocompleteText: AutoCompleteTextView,
    private val countryGoButton: Button,
    private val locationFilterViewLogic: SpotListLocationFilterViewLogic,
) {
    private val geoLocationService = AppContext.INSTANCE.geoLocationService

    init {
        MainScope().launch {
            countryAutocompleteText.setAdapter(
                ArrayAdapter(
                    countryAutocompleteText.context,
                    android.R.layout.simple_dropdown_item_1line,
                    geoLocationService.getCountriesLocally().toTypedArray()
                )
            )
        }

        countryGoButton.setOnClickListener {
            if (countryAutocompleteText.text.isNotEmpty()) {
                LoveSpotListFilterState.listLocation = ListLocation.COUNTRY
                LoveSpotListFilterState.locationName = countryAutocompleteText.text.toString()
                locationFilterViewLogic.closeLocationConfig()
            }
        }
    }

}