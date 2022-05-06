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
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotAvailabilityApiStatus.ALL_DAY
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.data.smackspot.SmackSpot
import com.smackmap.smackmapandroid.service.smackspot.SmackSpotService
import com.smackmap.smackmapandroid.ui.main.MainActivityEventListener
import com.smackmap.smackmapandroid.ui.utils.SmackSpotInfoWindowAdapter
import com.smackmap.smackmapandroid.ui.utils.pixelToDp
import kotlinx.coroutines.*

@SuppressLint("MissingPermission")
class SmackMapPageFragment : Fragment(), OnMapReadyCallback, MainActivityEventListener {
    private val appContext = AppContext.INSTANCE
    private val smackSpotService: SmackSpotService = appContext.smackSpotService
    private var cameraMoved = false
    private var locationEnabled = false

    private lateinit var viewPager2: ViewPager2
    private lateinit var smackSpotInfoWindowAdapter: SmackSpotInfoWindowAdapter
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var dayBitmap: BitmapDescriptor
    private lateinit var nightBitmap: BitmapDescriptor

    private lateinit var addSmackText: TextView
    private lateinit var addSmackFab: FloatingActionButton
    private lateinit var toWishlistText: TextView
    private lateinit var addToWishlistFab: FloatingActionButton
    private lateinit var reportSpotText: TextView
    private lateinit var reportSmackSpotFab: FloatingActionButton

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
        dayBitmap = getIconBitmap(R.drawable.ic_marker_sun)
        nightBitmap = getIconBitmap(R.drawable.ic_marker_moon)
        addSmackText = view.findViewById(R.id.smackOnSpotText)
        addSmackFab = view.findViewById(R.id.addSmackFab)
        toWishlistText = view.findViewById(R.id.spotWishlistText)
        addToWishlistFab = view.findViewById(R.id.addToWishlistFab)
        reportSpotText = view.findViewById(R.id.spotReportText)
        reportSmackSpotFab = view.findViewById(R.id.reportSmackSpotFab)
        return view
    }

    override fun onResume() {
        super.onResume()
        appContext.mainActivityEventListener = this
        mapFragment.getMapAsync(this)
        viewPager2 = getViewPager2(requireView())
        val crosshair: ImageView = viewPager2.findViewById(R.id.centerCrosshair)
        val addSmackspotText: TextView = viewPager2.findViewById(R.id.mapAddSmackspotText)
        if (appContext.areAddSmackSpotFabsOpen) {
            crosshair.visibility = View.VISIBLE
            addSmackspotText.visibility = View.VISIBLE
        } else {
            crosshair.visibility = View.GONE
            addSmackspotText.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        val thisView = requireView()
        viewPager2 = getViewPager2(thisView)
        val linearLayout = viewPager2.parent as ViewGroup
        val tabLayout = linearLayout.findViewById<TabLayout>(R.id.tab_layout)
        MainScope().launch {
            smackSpotInfoWindowAdapter = SmackSpotInfoWindowAdapter(
                smackSpotService,
                requireActivity(),
                smackSpotService.getRisks()
            )
            googleMap.setInfoWindowAdapter(smackSpotInfoWindowAdapter)
            fetchSmackSpots(googleMap)
        }
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
        googleMap.setOnMarkerClickListener { marker ->
            appContext.mapMarkerEventListener.onMarkerClicked()
            openMarkerFabMenu()
            false
        }
        googleMap.setOnMapClickListener {
            viewPager2.isUserInputEnabled = false
            appContext.mapMarkerEventListener.onMapClicked()
            closeMarkerFabMenu()
        }
        googleMap.setOnCameraIdleListener {
            if (cameraMoved) {
                MainScope().launch {
                    fetchSmackSpots(googleMap)
                }
            }
        }
    }

    private suspend fun fetchSmackSpots(
        googleMap: GoogleMap
    ) {
        val visibleRegion = googleMap.projection.visibleRegion
        appContext.mapCameraTarget = googleMap.cameraPosition.target
        smackSpotService
            .search(visibleRegion.latLngBounds)
            .map { smackSpotToMarkerOptions(it) }
            .forEach { googleMap.addMarker(it) }
    }

    private fun smackSpotToMarkerOptions(smackSpot: SmackSpot): MarkerOptions {
        val icon = if (smackSpot.availability == ALL_DAY) {
            dayBitmap
        } else {
            nightBitmap
        }
        return MarkerOptions()
            .icon(icon)
            .position(LatLng(smackSpot.latitude, smackSpot.longitude))
            .snippet(smackSpot.id.toString())
            .title(smackSpot.name)
    }

    private fun getIconBitmap(drawableId: Int): BitmapDescriptor {
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), drawableId)!!
        val width = drawable.intrinsicWidth / 12
        val height = drawable.intrinsicHeight / 12
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

    private fun openMarkerFabMenu() {
        if (!appContext.areMarkerFabsOpen) {
            appContext.areMarkerFabsOpen = true
            reportSmackSpotFab.visibility = View.VISIBLE
            addToWishlistFab.visibility = View.VISIBLE
            addSmackFab.visibility = View.VISIBLE

            reportSpotText.visibility = View.VISIBLE
            toWishlistText.visibility = View.VISIBLE
            addSmackText.visibility = View.VISIBLE

            reportSmackSpotFab.animate().rotationBy(360f).translationX(resources.getDimension(R.dimen.standard_75))
            addToWishlistFab.animate().rotationBy(360f).translationX(resources.getDimension(R.dimen.standard_150))
            addSmackFab.animate().rotationBy(360f).translationX(resources.getDimension(R.dimen.standard_225))

            MainScope().launch {
                delay(50)
                reportSpotText.animate().translationX(
                    resources.getDimension(R.dimen.standard_75)
                            + resources.getDimension(R.dimen.standard_minus_55)
                            - resources.getDimension(R.dimen.standard_minus_40)
                            - pixelToDp(reportSpotText.width.toFloat() - reportSmackSpotFab.width)
                )
                toWishlistText.animate().translationX(
                    resources.getDimension(R.dimen.standard_150)
                            + resources.getDimension(R.dimen.standard_minus_55)
                            - resources.getDimension(R.dimen.standard_minus_40)
                            - pixelToDp(toWishlistText.width.toFloat() - addToWishlistFab.width)
                )
                addSmackText.animate().translationX(
                    resources.getDimension(R.dimen.standard_225)
                            + resources.getDimension(R.dimen.standard_minus_55)
                            - resources.getDimension(R.dimen.standard_minus_40)
                            - pixelToDp(addSmackText.width.toFloat() - addSmackFab.width)
                )
            }
        }
    }

    private fun closeMarkerFabMenu() {
        if (appContext.areMarkerFabsOpen) {
            appContext.areMarkerFabsOpen = false
            reportSmackSpotFab.animate().rotationBy(360f).translationX(0f)
                .withEndAction { reportSmackSpotFab.visibility = View.GONE }
            addToWishlistFab.animate().rotationBy(360f).translationX(0f)
                .withEndAction { addToWishlistFab.visibility = View.GONE }
            addSmackFab.animate().rotationBy(360f).translationX(0f)
                .withEndAction { addSmackFab.visibility = View.GONE }

            MainScope().launch {
                delay(50)
                reportSpotText.animate().translationX(0f)
                    .withEndAction { reportSpotText.visibility = View.GONE }
                toWishlistText.animate().translationX(0f)
                    .withEndAction { toWishlistText.visibility = View.GONE }
                addSmackText.animate().translationX(0f)
                    .withEndAction { addSmackText.visibility = View.GONE }
            }

        }
    }

    override fun onOpenAddSmackSpotFabs() {
        closeMarkerFabMenu()
    }
}
