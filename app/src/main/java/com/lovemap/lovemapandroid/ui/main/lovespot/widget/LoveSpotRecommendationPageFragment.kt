package com.lovemap.lovemapandroid.ui.main.lovespot.widget

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.RecommendationsRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.events.LocationUpdated
import com.lovemap.lovemapandroid.ui.events.RecommendationsUpdated
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoveSpotRecommendationPageFragment : Fragment() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService
    private var lastUpdateWithLocation: Long = 0

    private lateinit var recommendationsSwipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_love_spot_recommendation_page, container, false)
        recommendationsSwipeRefresh = view.findViewById(R.id.recommendationsSwipeRefresh)
        recommendationsSwipeRefresh.setOnRefreshListener {
            getRecommendations()
        }
        getRecommendationsWhenLocationAccessIsDenied()
        return view
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationUpdated(event: LocationUpdated) {
        val currentTimeMillis = System.currentTimeMillis()
        if (oneMinutePassedSinceLastUpdate(currentTimeMillis)) {
            lastUpdateWithLocation = currentTimeMillis
            getRecommendations()
        }
    }

    private fun oneMinutePassedSinceLastUpdate(currentTimeMillis: Long) =
        currentTimeMillis - lastUpdateWithLocation >= 60 * 1000

    private fun getRecommendationsWhenLocationAccessIsDenied() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    appContext.requestLocationUpdates()
                }
                else -> {
                    // No location access granted.
                    getRecommendations()
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }

    private fun getRecommendations() {
        MainScope().launch {
            val recommendations = loveSpotService.getRecommendations(
                RecommendationsRequest(
                    appContext.lastLocation?.longitude,
                    appContext.lastLocation?.latitude,
                    appContext.country
                )
            )
            EventBus.getDefault().post(
                RecommendationsUpdated(
                    recommendations
                )
            )
            recommendationsSwipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}