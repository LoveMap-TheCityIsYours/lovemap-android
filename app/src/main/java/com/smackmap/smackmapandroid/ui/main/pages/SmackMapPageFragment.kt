package com.smackmap.smackmapandroid.ui.main.pages

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotAvailabilityApiStatus
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotAvailabilityApiStatus.ALL_DAY
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.service.smack.location.SmackSpotService
import kotlinx.coroutines.*

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
                    val dayBitmap = getIconBitmap(R.drawable.ic_marker_sun)
                    val nightBitmap = getIconBitmap(R.drawable.ic_marker_moon)
                    val markers = smackSpots.map {
                        val icon = if (it.availability == ALL_DAY) {
                            dayBitmap
                        } else {
                            nightBitmap
                        }
                        MarkerOptions()
                            .icon(icon)
                            .position(LatLng(it.latitude, it.longitude))
                            .snippet(
                                it.id.toString()
                            )
                            .title(it.name)
                    }
                    googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(marker: Marker): View? {
                            val view = requireActivity().layoutInflater.inflate(
                                R.layout.marker_info_window_layout,
                                null
                            )
                            runBlocking {
                                val smackSpot =
                                    smackSpotService.findLocally(marker.snippet?.toLong() ?: -1)
                                if (smackSpot != null) {
                                    val title: TextView = view.findViewById(R.id.marker_title)
                                    title.text = smackSpot.name
                                    val ratingBar: RatingBar = view.findViewById(R.id.marker_rating)
                                    smackSpot.averageRating?.let {
                                        ratingBar.rating = it.toFloat()
                                    } ?: run {
                                        ratingBar.rating = 0f
                                    }
                                    val description: TextView = view.findViewById(R.id.marker_description)
                                    description.text = smackSpot.description
                                    val availability: TextView = view.findViewById(R.id.marker_availability)
                                    availability.text = if (smackSpot.availability == ALL_DAY) {
                                        getString(R.string.available_all_day)
                                    } else {
                                        getString(R.string.available_night_only)
                                    }
                                    val risk: TextView = view.findViewById(R.id.marker_risk)
                                    smackSpot.averageDanger?.let {
                                        // TODO: convert to funny text
                                        risk.text = it.toFloat().toString()
                                    } ?: run {
                                        risk.text = getString(R.string.risk_unknown)
                                    }
                                    val customAvText: TextView = view.findViewById(R.id.marker_custom_availability_text)
                                    val customAv: TextView = view.findViewById(R.id.marker_custom_availability)
                                    if (smackSpot.customAvailability != null) {
                                        customAv.text = smackSpot.customAvailability
                                    } else {
                                        customAvText.visibility = View.GONE
                                        customAv.visibility = View.GONE
                                    }

                                    customAvText.text = getString(R.string.custom_availability_text)
                                }
                            }
                            return view
                        }


                        override fun getInfoWindow(p0: Marker): View? {
                            // Only overriding content
                            return null
                        }
                    })
                    markers.forEach { googleMap.addMarker(it) }
                }
            }
        }
    }

    private fun getIconBitmap(drawableId: Int): BitmapDescriptor {
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), drawableId)!!
        val width = drawable.intrinsicWidth / 10
        val height = drawable.intrinsicHeight / 10
        drawable.setBounds(
            0,
            0,
            width,
            height
        )
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
