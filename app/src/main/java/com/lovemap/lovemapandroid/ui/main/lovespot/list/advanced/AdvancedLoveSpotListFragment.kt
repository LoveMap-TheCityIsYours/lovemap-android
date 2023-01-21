package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import com.lovemap.lovemapandroid.ui.utils.InfoPopupShower

class AdvancedLoveSpotListFragment : Fragment() {

    private lateinit var locationFilterViewLogic: SpotListLocationFilterViewLogic
    private lateinit var typeFilterViewLogic: SpotListTypeFilterViewLogic
    private lateinit var countryFilterViewLogic: SpotListCountryFilterViewLogic
    private lateinit var cityFilterViewLogic: SpotListCityFilterViewLogic
    private lateinit var spotListNearbyFilterViewLogic: SpotListNearbyFilterViewLogic
    private lateinit var orderingInfoButton: ImageButton

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
        layoutTransition.setAnimateParentHierarchy(false)

        val locationConfigurationView = view.findViewById<LinearLayout>(R.id.locationConfigurationView)
        locationConfigurationView.layoutTransition.setAnimateParentHierarchy(false)

        val countryFilterViewGroup = view.findViewById<LinearLayout>(R.id.countryFilterViewGroup)
        countryFilterViewGroup.layoutTransition.setAnimateParentHierarchy(false)

        val cityFilterViewGroup = view.findViewById<LinearLayout>(R.id.cityFilterViewGroup)
        cityFilterViewGroup.layoutTransition.setAnimateParentHierarchy(false)

        val nearbyFilterViewGroup = view.findViewById<LinearLayout>(R.id.nearbyFilterViewGroup)
        nearbyFilterViewGroup.layoutTransition.setAnimateParentHierarchy(false)

        val typeFilterScrollView = view.findViewById<HorizontalScrollView>(R.id.typeFilterScrollView)
        typeFilterScrollView.layoutTransition.setAnimateParentHierarchy(false)

        locationFilterViewLogic = SpotListLocationFilterViewLogic(
            spotOrderingSpinner = view.findViewById(R.id.spotOrderingSpinner),
            locationSelectorButton = view.findViewById(R.id.locationSelectorButton),
            locationConfigurationView = locationConfigurationView,
            countryFilterButton = view.findViewById(R.id.countryFilterButton),
            countryFilterViewGroup = countryFilterViewGroup,
            cityFilterButton = view.findViewById(R.id.cityFilterButton),
            cityFilterViewGroup = cityFilterViewGroup,
            nearbyFilterButton = view.findViewById(R.id.nearbyFilterButton),
            nearbyFilterViewGroup = nearbyFilterViewGroup
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
            typeFilterScrollView = typeFilterScrollView,
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

        orderingInfoButton = view.findViewById(R.id.orderingInfoButton)
        orderingInfoButton.setOnClickListener {
            val infoPopupShower = InfoPopupShower(R.string.ordering_explanation_popup)
            infoPopupShower.show(view)
        }

        LoveSpotListFilterState.initialized = true

        return view
    }

    override fun onResume() {
        super.onResume()
        locationFilterViewLogic.updateDropdownAndButton()
    }
}