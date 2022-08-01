package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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
    private var locationConfigOpen = false
    private var countryFilterOpen = false
    private var cityFilterOpen = false
    private var nearbyFilterOpen = false

    init {
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

    fun updateSearchButtonText(text: String) {
        locationSelectorButton.text = text
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

    fun initSearchButton() {
        // TODO: set button text based on LoveSpotListFilterState
    }
}