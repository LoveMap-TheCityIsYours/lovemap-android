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
class SpotListCityFilterViewLogic(
    private val cityAutocompleteText: AutoCompleteTextView,
    private val cityGoButton: Button,
    private val locationFilterViewLogic: SpotListLocationFilterViewLogic,
) {
    val appContext = AppContext.INSTANCE
    private val geoLocationService = appContext.geoLocationService

    init {
        cityAutocompleteText.setOnTouchListener { _, _ ->
            cityAutocompleteText.showDropDown()
            cityAutocompleteText.requestFocus()
//            cityAutocompleteText.setSelection(0, cityAutocompleteText.text.length)
            false
        }

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
                val cityName = cityAutocompleteText.text.toString().substringBefore(",").trim()
                LoveSpotListFilterState.locationName = cityName
                locationFilterViewLogic.updateSearchButtonText(appContext.getString(R.string.city_search_button_text) + cityName)
                locationFilterViewLogic.closeLocationConfig()
                hideKeyboard(cityAutocompleteText)
            }
        }
    }

}