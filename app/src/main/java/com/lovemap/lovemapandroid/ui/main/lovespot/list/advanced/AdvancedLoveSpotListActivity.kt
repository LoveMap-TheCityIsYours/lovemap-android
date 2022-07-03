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

    lateinit var locationFilterLogic: AdvSpotListLocationFilterLogic
    lateinit var typeFilterLogic: AdvSpotListTypeFilterLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedLoveSpotListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutTransition: LayoutTransition = binding.root.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        locationFilterLogic = AdvSpotListLocationFilterLogic(
            locationSelectorButton = binding.locationSelectorButton,
            locationConfigurationView = binding.locationConfigurationView,
            countryFilterButton = binding.countryFilterButton,
            countryAutocompleteText = binding.countryAutocompleteText,
            cityFilterButton = binding.cityFilterButton,
            cityAutocompleteText = binding.cityAutocompleteText,
            nearbyFilterButton = binding.nearbyFilterButton,
            nearbyFilterViewGroup = binding.nearbyFilterViewGroup
        )

        typeFilterLogic = AdvSpotListTypeFilterLogic(
            typeFilterScrollView = binding.typeFilterScrollView,
            typeFilterAll = binding.typeFilterAll,
            typeFilterPublicSpace = binding.typeFilterPublicSpace,
            typeFilterSwingerClub = binding.typeFilterSwingerClub,
            typeFilterCruisingSpot = binding.typeFilterCruisingSpot,
            typeFilterSexBooth = binding.typeFilterSexBooth,
            typeFilterNightClub = binding.typeFilterNightClub,
            typeFilterOtherVenue = binding.typeFilterOtherVenue,
        )
    }
}