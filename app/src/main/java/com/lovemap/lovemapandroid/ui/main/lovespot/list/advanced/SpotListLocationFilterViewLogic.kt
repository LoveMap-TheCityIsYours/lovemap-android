package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class SpotListLocationFilterViewLogic(
    private val locationSelectorButton: Button,
    private val locationConfigurationView: LinearLayout,
    private val countryFilterButton: ExtendedFloatingActionButton,
    private val countryFilterViewGroup: LinearLayout,
    private val cityFilterButton: ExtendedFloatingActionButton,
    private val cityFilterViewGroup: LinearLayout,
    private val nearbyFilterButton: ExtendedFloatingActionButton,
    private val nearbyFilterViewGroup: LinearLayout,
    private val nearbyGoButton: Button
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

    fun setLocationSelectorButton() {
        locationSelectorButton.setOnClickListener {
            if (!locationConfigOpen) {
                openLocationConfig()
            } else {
                closeLocationConfig()
            }
        }
    }

    fun updateSearchButtonText(text: String) {
        locationSelectorButton.text = text
    }

    fun openLocationConfig() {
        locationConfigOpen = true
        locationConfigurationView.visibility = View.VISIBLE
    }

    fun closeLocationConfig() {
        locationConfigOpen = false
        locationConfigurationView.visibility = View.GONE
        closeCountryFilter()
        closeCityFilter()
        closeNearbyFilter()
    }


    fun setCountryFilterButton() {
        countryFilterButton.setOnClickListener {
            if (!countryFilterOpen) {
                openCountryFilter()
            } else {
                closeCountryFilter()
            }
        }
    }

    fun openCountryFilter() {
        closeCityFilter()
        closeNearbyFilter()
        countryFilterOpen = true
        countryFilterViewGroup.visibility = View.VISIBLE
    }

    fun closeCountryFilter() {
        countryFilterOpen = false
        countryFilterViewGroup.visibility = View.GONE
    }


    fun setCityFilterButton() {
        cityFilterButton.setOnClickListener {
            if (!cityFilterOpen) {
                openCityFilter()
            } else {
                closeCityFilter()
            }
        }
    }

    fun openCityFilter() {
        closeCountryFilter()
        closeNearbyFilter()
        cityFilterOpen = true
        cityFilterViewGroup.visibility = View.VISIBLE
    }

    fun closeCityFilter() {
        cityFilterOpen = false
        cityFilterViewGroup.visibility = View.GONE
    }


    fun setNearbyFilterButton() {
        nearbyFilterButton.setOnClickListener {
            if (!nearbyFilterOpen) {
                openNearbyFilter()
            } else {
                closeNearbyFilter()
            }
        }
    }

    fun openNearbyFilter() {
        closeCountryFilter()
        closeCityFilter()
        nearbyFilterOpen = true
        nearbyFilterViewGroup.visibility = View.VISIBLE
    }

    fun closeNearbyFilter() {
        nearbyFilterOpen = false
        nearbyFilterViewGroup.visibility = View.GONE
    }
}