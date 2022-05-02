package com.smackmap.smackmapandroid.ui.main.pages

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.service.smack.location.SmackSpotService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class SmackMapPageFragment : Fragment(), OnMapReadyCallback {
    private val smackSpotService: SmackSpotService = AppContext.INSTANCE.smackSpotService
    private lateinit var mapFragment: SupportMapFragment
    private var cameraMoved = false
    private var locationEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_smack_map_page, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        return view
    }

    override fun onResume() {
        super.onResume()
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        val thisView = requireView()
        val viewPager2 = getViewPager2(thisView)
        val linearLayout = viewPager2.parent as ViewGroup
        val tabLayout = linearLayout.findViewById<TabLayout>(R.id.tab_layout)

        setMyLocation(googleMap)
        putMarkersOnMap(googleMap)
        configureUserInputForViews(googleMap, viewPager2, tabLayout, thisView)
    }

    private fun getViewPager2(thisView: View): ViewPager2 {
        var view = thisView.parent
        while (view !is ViewPager2 && view != null) {
            if (view.parent == null) {
                return (view as ViewGroup).findViewById(R.id.view_pager)
            }
            view = view.parent
        }
        return view as ViewPager2
    }

    private fun setMyLocation(googleMap: GoogleMap) {
        if (locationEnabled) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun putMarkersOnMap(googleMap: GoogleMap) {
        googleMap.setOnCameraIdleListener {
            if (cameraMoved) {
                val visibleRegion = googleMap.projection.visibleRegion
                MainScope().launch {
                    val smackSpots = smackSpotService.search(
                        visibleRegion.latLngBounds
                    )
                    val markers = smackSpots.map {
                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                            .title(it.name)
                    }
                    markers.forEach { googleMap.addMarker(it) }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureUserInputForViews(
        googleMap: GoogleMap,
        viewPager2: ViewPager2,
        tabLayout: TabLayout,
        thisView: View
    ) {
        googleMap.setOnMapClickListener {
            viewPager2.isUserInputEnabled = false
        }
        googleMap.setOnCameraMoveListener {
            if (tabLayout.selectedTabPosition == 2) {
                viewPager2.isUserInputEnabled = false
            }
            cameraMoved = true
        }
        thisView.setOnTouchListener { _, _ ->
            viewPager2.isUserInputEnabled = true
            true
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.isUserInputEnabled = true
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun askForLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    if (this::mapFragment.isInitialized) {
                        mapFragment.getMapAsync {
                            it.isMyLocationEnabled = true
                            it.uiSettings.isMyLocationButtonEnabled = true
                        }
                    }
                    locationEnabled = true
                }
                else -> {
                    // No location access granted.
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        )
    }
}
