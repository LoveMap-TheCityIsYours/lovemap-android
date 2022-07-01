package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.databinding.ActivityAdvancedLoveSpotListBinding


class AdvancedLoveSpotListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdvancedLoveSpotListBinding

    private lateinit var locationSelectorButton: Button
    private lateinit var locationConfigurationView: LinearLayout
    private lateinit var countryFilterButton: ExtendedFloatingActionButton
    private lateinit var countryAutocompleteText: AutoCompleteTextView
    private lateinit var cityFilterButton: ExtendedFloatingActionButton
    private lateinit var cityAutocompleteText: AutoCompleteTextView
    private lateinit var nearbyFilterButton: ExtendedFloatingActionButton
    private lateinit var nearbyFilterViewGroup: LinearLayout

    private var locationConfigOpen = false
    private var countryFilterOpen = false
    private var cityFilterOpen = false
    private var nearbyFilterOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedLoveSpotListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutTransition: LayoutTransition = binding.root.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        locationSelectorButton = binding.locationSelectorButton
        locationConfigurationView = binding.locationConfigurationView
        countryFilterButton = binding.countryFilterButton
        countryAutocompleteText = binding.countryAutocompleteText
        cityFilterButton = binding.cityFilterButton
        cityAutocompleteText = binding.cityAutocompleteText
        nearbyFilterButton = binding.nearbyFilterButton
        nearbyFilterViewGroup = binding.nearbyFilterViewGroup

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