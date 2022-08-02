package com.lovemap.lovemapandroid.ui.main.lovespot.widget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.RecommendationsRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.events.LocationUpdated
import com.lovemap.lovemapandroid.ui.events.RecommendationsUpdated
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoveSpotRecommendationPageFragment : Fragment() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService
    private var lastUpdateWithLocation: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationUpdated(event: LocationUpdated) {
        val currentTimeMillis = System.currentTimeMillis()
        if (oneMinutePassedSinceLastUpdate(currentTimeMillis)) {
            getRecommendations()
            lastUpdateWithLocation = currentTimeMillis
        }
    }

    private fun oneMinutePassedSinceLastUpdate(currentTimeMillis: Long) =
        currentTimeMillis - lastUpdateWithLocation >= 60 * 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        getRecommendations()
        return inflater.inflate(R.layout.fragment_love_spot_recommendation_page, container, false)
    }

    private fun getRecommendations() {
        MainScope().launch {
            val recommendations = loveSpotService.getRecommendations(
                RecommendationsRequest(
                    appContext.lastLocation?.longitude,
                    appContext.lastLocation?.latitude,
                    appContext.country,
                    LoveSpotListFilterState.getSelectedTypes()
                )
            )
            EventBus.getDefault().post(
                RecommendationsUpdated(
                    recommendations
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}