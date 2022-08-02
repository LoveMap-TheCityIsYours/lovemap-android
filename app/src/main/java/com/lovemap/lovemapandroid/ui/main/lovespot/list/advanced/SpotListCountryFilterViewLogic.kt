package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import com.lovemap.lovemapandroid.ui.utils.hideKeyboard
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@SuppressLint("ClickableViewAccessibility")
class SpotListCountryFilterViewLogic(
    private val countryAutocompleteText: AutoCompleteTextView,
    private val countryGoButton: Button,
    private val locationFilterViewLogic: SpotListLocationFilterViewLogic,
) {
    val appContext = AppContext.INSTANCE
    private val geoLocationService = appContext.geoLocationService

    init {
        countryAutocompleteText.setOnTouchListener { _, _ ->
            countryAutocompleteText.showDropDown()
            countryAutocompleteText.requestFocus()
            false
        }

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
                val countryName = countryAutocompleteText.text.toString().trim()
                LoveSpotListFilterState.locationName = countryName
                locationFilterViewLogic.updateSearchButtonText(ListLocation.COUNTRY)
                locationFilterViewLogic.closeLocationConfig()
                hideKeyboard(countryAutocompleteText)
            }
        }
    }

}