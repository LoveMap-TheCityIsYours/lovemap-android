package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class AdvSpotListLocationFilterLogic
    (
    private val locationSelectorButton: Button,
    private val locationConfigurationView: LinearLayout,
    private val countryFilterButton: ExtendedFloatingActionButton,
    private val countryAutocompleteText: AutoCompleteTextView,
    private val cityFilterButton: ExtendedFloatingActionButton,
    private val cityAutocompleteText: AutoCompleteTextView,
    private val nearbyFilterButton: ExtendedFloatingActionButton,
    private val nearbyFilterViewGroup: LinearLayout
) {
    private var locationConfigOpen = false
    private var countryFilterOpen = false
    private var cityFilterOpen = false
    private var nearbyFilterOpen = false

    init {
        setLocationSelectorButton()
        setCountryFilterButton()
        setCityFilterButton()
        setNearbyFilterButton()
    }

    private fun setLocationSelectorButton() {
        locationSelectorButton.setOnClickListener {
            if (!locationConfigOpen) {
                openLocationConfig()
            } else {
                closeLocationConfig()
            }
        }
    }

    private fun openLocationConfig() {
        locationConfigOpen = true
        locationConfigurationView.visibility = View.VISIBLE
    }

    private fun closeLocationConfig() {
        locationConfigOpen = false
        locationConfigurationView.visibility = View.GONE
        closeCountryFilter()
        closeCityFilter()
        closeNearbyFilter()
    }


    private fun setCountryFilterButton() {
        countryFilterButton.setOnClickListener {
            if (!countryFilterOpen) {
                openCountryFilter()
            } else {
                closeCountryFilter()
            }
        }
    }

    private fun openCountryFilter() {
        closeCityFilter()
        closeNearbyFilter()
        countryFilterOpen = true
        countryAutocompleteText.visibility = View.VISIBLE
    }

    private fun closeCountryFilter() {
        countryFilterOpen = false
        countryAutocompleteText.visibility = View.GONE
    }


    private fun setCityFilterButton() {
        cityFilterButton.setOnClickListener {
            if (!cityFilterOpen) {
                openCityFilter()
            } else {
                closeCityFilter()
            }
        }
    }

    private fun openCityFilter() {
        closeCountryFilter()
        closeNearbyFilter()
        cityFilterOpen = true
        cityAutocompleteText.visibility = View.VISIBLE
    }

    private fun closeCityFilter() {
        cityFilterOpen = false
        cityAutocompleteText.visibility = View.GONE
    }


    private fun setNearbyFilterButton() {
        nearbyFilterButton.setOnClickListener {
            if (!nearbyFilterOpen) {
                openNearbyFilter()
            } else {
                closeNearbyFilter()
            }
        }
    }

    private fun openNearbyFilter() {
        closeCountryFilter()
        closeCityFilter()
        nearbyFilterOpen = true
        nearbyFilterViewGroup.visibility = View.VISIBLE
    }

    private fun closeNearbyFilter() {
        nearbyFilterOpen = false
        nearbyFilterViewGroup.visibility = View.GONE
    }
}