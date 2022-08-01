package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.google.android.material.slider.Slider
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import com.lovemap.lovemapandroid.ui.utils.hideKeyboard

class SpotListNearbyFilterViewLogic(
    private val nearbyFilterEditText: EditText,
    private val nearbyFilterSlider: Slider,
    private val nearbyGoButton: Button,
    private val locationFilterViewLogic: SpotListLocationFilterViewLogic
) {
    private val appContext = AppContext.INSTANCE
    private val maxMeters: Float = 10000f
    private var currentDistance = LoveSpotListFilterState.distanceInMeters

    init {
        nearbyFilterSlider.value = LoveSpotListFilterState.distanceInMeters.toFloat()
        nearbyFilterEditText.setText(LoveSpotListFilterState.distanceInMeters.toString())

        nearbyFilterSlider.addOnChangeListener { slider, value, fromUser ->
            currentDistance = value.toInt()
            if (fromUser) {
                nearbyFilterEditText.setText(value.toInt().toString())
            }
        }

        nearbyFilterEditText.doOnTextChanged { text, start, before, count ->
            text?.toString()?.let { stringText ->
                if (stringText.isNotEmpty()) {
                    var floatValue = stringText.toFloat()
                    floatValue = if (floatValue > maxMeters) {
                        nearbyFilterEditText.setText(maxMeters.toInt().toString())
                        maxMeters
                    } else {
                        floatValue
                    }
                    nearbyFilterSlider.value = floatValue
                    currentDistance = floatValue.toInt()
                }
            }
        }

        nearbyGoButton.setOnClickListener {
            LoveSpotListFilterState.listLocation = ListLocation.COORDINATE
            LoveSpotListFilterState.distanceInMeters = currentDistance
            locationFilterViewLogic.updateSearchButtonText(
                appContext.getString(R.string.nearby_search_button_text)
                        + LoveSpotListFilterState.distanceInMeters
                        + " " + appContext.getString(R.string.meters)
            )
            locationFilterViewLogic.closeLocationConfig()
            hideKeyboard(nearbyFilterEditText)
        }
    }
}