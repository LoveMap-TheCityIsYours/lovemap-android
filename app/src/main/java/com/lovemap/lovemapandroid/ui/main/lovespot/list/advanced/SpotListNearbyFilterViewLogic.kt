package com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced

import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.google.android.material.slider.Slider
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import com.lovemap.lovemapandroid.ui.utils.hideKeyboard
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class SpotListNearbyFilterViewLogic(
    private val nearbyFilterEditText: EditText,
    private val nearbyFilterSlider: Slider,
    private val nearbyGoButton: Button,
    private val locationFilterViewLogic: SpotListLocationFilterViewLogic
) {
    private val appContext = AppContext.INSTANCE
    private val maxMeters: Float = 10000f
    private val minMeters: Float = 0f
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
                    val floatValue = getFloatValue(stringText)
                    nearbyFilterSlider.value = floatValue
                    currentDistance = floatValue.toInt()
                }
            }
        }

        nearbyGoButton.setOnClickListener {
            LoveSpotListFilterState.listLocation = ListLocation.COORDINATE
            LoveSpotListFilterState.distanceInMeters = currentDistance
            LoveSpotListFilterState.sendFiltersChangedEvent()
            locationFilterViewLogic.updateSearchButtonText(ListLocation.COORDINATE)
            locationFilterViewLogic.closeLocationConfig()
            hideKeyboard(nearbyFilterEditText)
        }
    }

    private fun getFloatValue(stringText: String): Float {
        val originalValue = stringText.toFloat()

        var floatValue = min(originalValue, maxMeters)
        floatValue = max(floatValue, minMeters)
        floatValue = ceil(floatValue / 100).toInt() * 100.toFloat()

        if (originalValue < minMeters || originalValue > maxMeters) {
            nearbyFilterEditText.setText(floatValue.toInt().toString())
        }

        return floatValue
    }
}