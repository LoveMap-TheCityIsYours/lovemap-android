package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState

class AdvancedLoveSpotListFragment : Fragment() {

    lateinit var locationFilterViewLogic: SpotListLocationFilterViewLogic
    lateinit var typeFilterViewLogic: SpotListTypeFilterViewLogic
    lateinit var countryFilterViewLogic: SpotListCountryFilterViewLogic
    lateinit var cityFilterViewLogic: SpotListCityFilterViewLogic
    lateinit var spotListNearbyFilterViewLogic: SpotListNearbyFilterViewLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_advanced_love_spot_list,
            container,
            false
        ) as LinearLayout

        val layoutTransition: LayoutTransition = view.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        locationFilterViewLogic = SpotListLocationFilterViewLogic(
            spotOrderingSpinner = view.findViewById(R.id.spotOrderingSpinner),
            locationSelectorButton = view.findViewById(R.id.locationSelectorButton),
            locationConfigurationView = view.findViewById(R.id.locationConfigurationView),
            countryFilterButton = view.findViewById(R.id.countryFilterButton),
            countryFilterViewGroup = view.findViewById(R.id.countryFilterViewGroup),
            cityFilterButton = view.findViewById(R.id.cityFilterButton),
            cityFilterViewGroup = view.findViewById(R.id.cityFilterViewGroup),
            nearbyFilterButton = view.findViewById(R.id.nearbyFilterButton),
            nearbyFilterViewGroup = view.findViewById(R.id.nearbyFilterViewGroup)
        )

        countryFilterViewLogic = SpotListCountryFilterViewLogic(
            countryAutocompleteText = view.findViewById(R.id.countryAutocompleteText),
            countryGoButton = view.findViewById(R.id.countryGoButton),
            locationFilterViewLogic = locationFilterViewLogic
        )

        cityFilterViewLogic = SpotListCityFilterViewLogic(
            cityAutocompleteText = view.findViewById(R.id.cityAutocompleteText),
            cityGoButton = view.findViewById(R.id.cityGoButton),
            locationFilterViewLogic = locationFilterViewLogic
        )

        typeFilterViewLogic = SpotListTypeFilterViewLogic(
            typeFilterScrollView = view.findViewById(R.id.typeFilterScrollView),
            typeFilterAll = view.findViewById(R.id.typeFilterAll),
            typeFilterPublicSpace = view.findViewById(R.id.typeFilterPublicSpace),
            typeFilterSwingerClub = view.findViewById(R.id.typeFilterSwingerClub),
            typeFilterCruisingSpot = view.findViewById(R.id.typeFilterCruisingSpot),
            typeFilterSexBooth = view.findViewById(R.id.typeFilterSexBooth),
            typeFilterNightClub = view.findViewById(R.id.typeFilterNightClub),
            typeFilterOtherVenue = view.findViewById(R.id.typeFilterOtherVenue),
        )

        spotListNearbyFilterViewLogic = SpotListNearbyFilterViewLogic(
            nearbyFilterEditText = view.findViewById(R.id.nearbyFilterEditText),
            nearbyFilterSlider = view.findViewById(R.id.nearbyFilterSlider),
            nearbyGoButton = view.findViewById(R.id.nearbyGoButton),
            locationFilterViewLogic = locationFilterViewLogic
        )

        locationFilterViewLogic.initSearchButton()

        LoveSpotListFilterState.initialized = true

        return view
    }
}