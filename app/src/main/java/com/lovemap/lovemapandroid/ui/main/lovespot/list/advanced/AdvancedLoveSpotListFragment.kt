package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R

class AdvancedLoveSpotListFragment : Fragment() {

    lateinit var locationFilterLogic: AdvSpotListLocationFilterLogic
    lateinit var typeFilterLogic: AdvSpotListTypeFilterLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_advanced_love_spot_list, container, false) as LinearLayout

        val layoutTransition: LayoutTransition = view.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        locationFilterLogic = AdvSpotListLocationFilterLogic(
            locationSelectorButton = view.findViewById(R.id.locationSelectorButton),
            locationConfigurationView = view.findViewById(R.id.locationConfigurationView),
            countryFilterButton = view.findViewById(R.id.countryFilterButton),
            countryAutocompleteText = view.findViewById(R.id.countryAutocompleteText),
            cityFilterButton = view.findViewById(R.id.cityFilterButton),
            cityAutocompleteText = view.findViewById(R.id.cityAutocompleteText),
            nearbyFilterButton = view.findViewById(R.id.nearbyFilterButton),
            nearbyFilterViewGroup = view.findViewById(R.id.nearbyFilterViewGroup)
        )

        typeFilterLogic = AdvSpotListTypeFilterLogic(
            typeFilterScrollView = view.findViewById(R.id.typeFilterScrollView),
            typeFilterAll = view.findViewById(R.id.typeFilterAll),
            typeFilterPublicSpace = view.findViewById(R.id.typeFilterPublicSpace),
            typeFilterSwingerClub = view.findViewById(R.id.typeFilterSwingerClub),
            typeFilterCruisingSpot = view.findViewById(R.id.typeFilterCruisingSpot),
            typeFilterSexBooth = view.findViewById(R.id.typeFilterSexBooth),
            typeFilterNightClub = view.findViewById(R.id.typeFilterNightClub),
            typeFilterOtherVenue = view.findViewById(R.id.typeFilterOtherVenue),
        )
        
        return view
    }

}