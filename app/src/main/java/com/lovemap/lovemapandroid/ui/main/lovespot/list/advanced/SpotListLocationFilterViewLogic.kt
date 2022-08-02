package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils

class SpotListLocationFilterViewLogic(
    private val spotOrderingSpinner: Spinner,
    private val locationSelectorButton: Button,
    private val locationConfigurationView: LinearLayout,
    private val countryFilterButton: ExtendedFloatingActionButton,
    private val countryFilterViewGroup: LinearLayout,
    private val cityFilterButton: ExtendedFloatingActionButton,
    private val cityFilterViewGroup: LinearLayout,
    private val nearbyFilterButton: ExtendedFloatingActionButton,
    private val nearbyFilterViewGroup: LinearLayout,
) {
    private val appContext = AppContext.INSTANCE
    private var locationConfigOpen = false
    private var countryFilterOpen = false
    private var cityFilterOpen = false
    private var nearbyFilterOpen = false

    init {
        setOrderingSpinner()
        setLocationSelectorButton()
        setCountryFilterButton()
        setCityFilterButton()
        setNearbyFilterButton()
        updateSearchButtonText(LoveSpotListFilterState.listLocation)
    }

    fun updateSearchButtonText(listLocation: ListLocation) {
        locationSelectorButton.text = when (listLocation) {
            ListLocation.COORDINATE -> {
                appContext.getString(R.string.nearby_search_button_text) +
                        LoveSpotListFilterState.distanceInMeters + " " + appContext.getString(R.string.meters)
            }
            ListLocation.CITY -> {
                appContext.getString(R.string.city_search_button_text) + LoveSpotListFilterState.locationName
            }
            ListLocation.COUNTRY -> {
                appContext.getString(R.string.country_search_button_text) + LoveSpotListFilterState.locationName
            }
        }
    }

    private fun setOrderingSpinner() {
        spotOrderingSpinner.setSelection(LoveSpotUtils.orderingToPosition(LoveSpotListFilterState.listOrdering))
        spotOrderingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                LoveSpotListFilterState.listOrdering = LoveSpotUtils.positionToOrdering(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                LoveSpotListFilterState.listOrdering = LoveSpotUtils.positionToOrdering(0)
            }
        }
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

    fun closeLocationConfig() {
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
        countryFilterViewGroup.visibility = View.VISIBLE
    }

    private fun closeCountryFilter() {
        countryFilterOpen = false
        countryFilterViewGroup.visibility = View.GONE
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
        cityFilterViewGroup.visibility = View.VISIBLE
    }

    private fun closeCityFilter() {
        cityFilterOpen = false
        cityFilterViewGroup.visibility = View.GONE
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