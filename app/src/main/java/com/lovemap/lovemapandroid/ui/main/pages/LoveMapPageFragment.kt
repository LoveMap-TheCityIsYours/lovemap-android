package com.lovemap.lovemapandroid.ui.main.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus.ALL_DAY
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.service.LoveService
import com.lovemap.lovemapandroid.service.LoveSpotService
import com.lovemap.lovemapandroid.ui.events.MainActivityEventListener
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.pages.LoveMapPageFragment.MapMode.LOVE_MAKINGS
import com.lovemap.lovemapandroid.ui.main.pages.LoveMapPageFragment.MapMode.LOVE_SPOTS
import com.lovemap.lovemapandroid.ui.utils.LoveSpotInfoWindowAdapter
import com.lovemap.lovemapandroid.ui.utils.pixelToDp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class LoveMapPageFragment : Fragment(), OnMapReadyCallback, MainActivityEventListener {
    private val appContext = AppContext.INSTANCE
    private val loveSpotService: LoveSpotService = appContext.loveSpotService
    private val loveService: LoveService = appContext.loveService
    private var cameraMoved = false
    private var locationEnabled = false
    private var mapMode = LOVE_SPOTS
    private var mapModeChanged = false
    private val drawnSpots = HashSet<Long>()

    private lateinit var viewPager2: ViewPager2
    private lateinit var loveSpotInfoWindowAdapter: LoveSpotInfoWindowAdapter
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var dayBitmap: BitmapDescriptor
    private lateinit var nightBitmap: BitmapDescriptor
    private lateinit var loveBitmap: BitmapDescriptor

    private lateinit var addLoveText: TextView
    private lateinit var addLoveFab: FloatingActionButton
    private lateinit var toWishlistText: TextView
    private lateinit var addToWishlistFab: FloatingActionButton
    private lateinit var reportSpotText: TextView
    private lateinit var reportLoveSpotFab: FloatingActionButton

    private lateinit var changeMapModeFab: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_love_map_page, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        dayBitmap = getIconBitmap(R.drawable.ic_marker_sun)
        nightBitmap = getIconBitmap(R.drawable.ic_marker_moon)
        loveBitmap = getIconBitmap(R.drawable.ic_marker_heart)
        addLoveText = view.findViewById(R.id.loveOnSpotText)
        addLoveFab = view.findViewById(R.id.addLoveFab)
        toWishlistText = view.findViewById(R.id.spotWishlistText)
        addToWishlistFab = view.findViewById(R.id.addToWishlistFab)
        reportSpotText = view.findViewById(R.id.spotReportText)
        reportLoveSpotFab = view.findViewById(R.id.reportLoveSpotFab)
        changeMapModeFab = view.findViewById(R.id.changeMapModeFab)
        setButtons()
        return view
    }

    private fun setButtons() {
        reportLoveSpotFab.setOnClickListener {
            startActivity(Intent(requireContext(), ReportLoveSpotActivity::class.java))
        }
        addToWishlistFab.setOnClickListener {
            appContext.toaster.showToast(R.string.not_yet_implemented)
        }
        addLoveFab.setOnClickListener {
            startActivity(Intent(requireContext(), RecordLoveActivity::class.java))
        }
        changeMapModeFab.setOnClickListener {
            mapModeChanged = true
            mapMode = if (mapMode == LOVE_SPOTS) {
                appContext.toaster.showToast(R.string.showing_love_makings)
                LOVE_MAKINGS
            } else {
                appContext.toaster.showToast(R.string.showing_love_spots)
                LOVE_SPOTS
            }
            mapFragment.getMapAsync(this)
        }
    }

    override fun onResume() {
        super.onResume()
        appContext.mainActivityEventListener = this
        mapFragment.getMapAsync(this)
        viewPager2 = getViewPager2(requireView())
        val crosshair: ImageView = viewPager2.findViewById(R.id.centerCrosshair)
        val addLovespotText: TextView = viewPager2.findViewById(R.id.mapAddLovespotText)
        if (appContext.shouldCloseFabs) {
            closeMarkerFabMenu()
        }
        if (appContext.areAddLoveSpotFabsOpen) {
            crosshair.visibility = View.VISIBLE
            addLovespotText.visibility = View.VISIBLE
        } else {
            crosshair.visibility = View.GONE
            addLovespotText.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        val thisView = requireView()
        viewPager2 = getViewPager2(thisView)
        val linearLayout = viewPager2.parent as ViewGroup
        val tabLayout = linearLayout.findViewById<TabLayout>(R.id.tab_layout)
        if (appContext.selectedMarker != null && appContext.zoomOnNewLoveSpot == null) {
            if (appContext.shouldMoveMapCamera) {
                appContext.shouldMoveMapCamera = false
                moveCameraTo(appContext.selectedMarker!!.position, googleMap)
            }
        }
        MainScope().launch {
            loveSpotInfoWindowAdapter = LoveSpotInfoWindowAdapter(
                loveSpotService,
                requireActivity(),
            )
            googleMap.setInfoWindowAdapter(loveSpotInfoWindowAdapter)
            putLoveSpotsOrLoveMakings(googleMap)
        }
        setMyLocation(googleMap)
        putMarkersOnMap(googleMap)
        googleMap.setOnInfoWindowClickListener {
            startActivity(Intent(requireContext(), LoveSpotDetailsActivity::class.java))
        }
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
            appContext.selectedMarker = marker
            openMarkerFabMenu()
            false
        }
        googleMap.setOnMapClickListener {
            viewPager2.isUserInputEnabled = false
            appContext.mapMarkerEventListener.onMapClicked()
            appContext.selectedMarker = null
            closeMarkerFabMenu()
        }
        googleMap.setOnCameraIdleListener {
            if (cameraMoved) {
                MainScope().launch {
                    putLoveSpotsOrLoveMakings(googleMap)
                }
            }
        }
    }

    private suspend fun putLoveSpotsOrLoveMakings(
        googleMap: GoogleMap
    ) {
        appContext.mapCameraTarget = googleMap.cameraPosition.target

        if (mapModeChanged) {
            googleMap.clear()
            drawnSpots.clear()
            mapModeChanged = false
        }

        if (appContext.shouldClearMap) {
            googleMap.clear()
            drawnSpots.clear()
            appContext.shouldClearMap = false
        }

        val visibleRegion = googleMap.projection.visibleRegion
        val loveSpotsInArea = loveSpotService
            .search(visibleRegion.latLngBounds)

        if (mapMode == LOVE_MAKINGS) {
            val loves = loveService.list()
            val spotIdsWithLove = loves.map { it.loveSpotId }.toHashSet()
            loveSpotsInArea
                .filter { loveSpot -> spotIdsWithLove.contains(loveSpot.id) }
                .filter { isNotDrawnYet(it) }
                .map { loveMakingToMarkerOptions(it, googleMap) }
                .forEach { googleMap.addMarker(it) }
        } else {
            loveSpotsInArea
                .filter { isNotDrawnYet(it) }
                .map { loveSpotToMarkerOptions(it, googleMap) }
                .forEach { googleMap.addMarker(it) }
        }

        if (appContext.selectedMarker != null) {
            appContext.selectedMarker!!.showInfoWindow()
            openMarkerFabMenu()
        } else {
            closeMarkerFabMenu()
        }
    }

    private fun isNotDrawnYet(it: LoveSpot): Boolean {
        return if (!drawnSpots.contains(it.id)) {
            drawnSpots.add(it.id)
            true
        } else {
            false
        }
    }

    private fun loveSpotToMarkerOptions(loveSpot: LoveSpot, googleMap: GoogleMap): MarkerOptions {
        val icon = if (loveSpot.availability == ALL_DAY) {
            dayBitmap
        } else {
            nightBitmap
        }
        return loveSpotToMarkerOptionsAndZoom(loveSpot, icon, googleMap)
    }

    private fun loveMakingToMarkerOptions(
        loveSpot: LoveSpot,
        googleMap: GoogleMap
    ): MarkerOptions {
        return loveSpotToMarkerOptionsAndZoom(loveSpot, loveBitmap, googleMap)
    }

    private fun loveSpotToMarkerOptionsAndZoom(
        loveSpot: LoveSpot,
        icon: BitmapDescriptor,
        googleMap: GoogleMap
    ): MarkerOptions {
        val position = LatLng(loveSpot.latitude, loveSpot.longitude)
        val marker = MarkerOptions()
            .icon(icon)
            .position(position)
            .snippet(loveSpot.id.toString())
            .title(loveSpot.name)

        if (appContext.zoomOnNewLoveSpot != null && appContext.zoomOnNewLoveSpot!!.id == loveSpot.id) {
            appContext.zoomOnNewLoveSpot = null
            moveCameraTo(position, googleMap)
        }
        return marker
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
            reportLoveSpotFab.visibility = View.VISIBLE
            addToWishlistFab.visibility = View.VISIBLE
            addLoveFab.visibility = View.VISIBLE

            reportSpotText.visibility = View.VISIBLE
            toWishlistText.visibility = View.VISIBLE
            addLoveText.visibility = View.VISIBLE

            reportLoveSpotFab.animate().rotationBy(360f)
                .translationX(resources.getDimension(R.dimen.standard_65))
            addToWishlistFab.animate().rotationBy(360f)
                .translationX(resources.getDimension(R.dimen.standard_130))
            addLoveFab.animate().rotationBy(360f)
                .translationX(resources.getDimension(R.dimen.standard_195))

            MainScope().launch {
                delay(50)
                reportSpotText.animate().translationX(
                    resources.getDimension(R.dimen.standard_65)
                            + resources.getDimension(R.dimen.standard_minus_55)
                            - resources.getDimension(R.dimen.standard_minus_40)
                            - pixelToDp(reportSpotText.width.toFloat() - reportLoveSpotFab.width)
                )
                toWishlistText.animate().translationX(
                    resources.getDimension(R.dimen.standard_130)
                            + resources.getDimension(R.dimen.standard_minus_55)
                            - resources.getDimension(R.dimen.standard_minus_40)
                            - pixelToDp(toWishlistText.width.toFloat() - addToWishlistFab.width)
                )
                addLoveText.animate().translationX(
                    resources.getDimension(R.dimen.standard_195)
                            + resources.getDimension(R.dimen.standard_minus_55)
                            - resources.getDimension(R.dimen.standard_minus_40)
                            - pixelToDp(addLoveText.width.toFloat() - addLoveFab.width)
                )
            }
        }
    }

    private fun closeMarkerFabMenu() {
        if (appContext.areMarkerFabsOpen) {
            appContext.areMarkerFabsOpen = false
            reportLoveSpotFab.animate().rotationBy(360f).translationX(0f)
                .withEndAction { reportLoveSpotFab.visibility = View.GONE }
            addToWishlistFab.animate().rotationBy(360f).translationX(0f)
                .withEndAction { addToWishlistFab.visibility = View.GONE }
            addLoveFab.animate().rotationBy(360f).translationX(0f)
                .withEndAction { addLoveFab.visibility = View.GONE }

            MainScope().launch {
                delay(50)
                reportSpotText.animate().translationX(0f)
                    .withEndAction { reportSpotText.visibility = View.GONE }
                toWishlistText.animate().translationX(0f)
                    .withEndAction { toWishlistText.visibility = View.GONE }
                addLoveText.animate().translationX(0f)
                    .withEndAction { addLoveText.visibility = View.GONE }
            }

        }
    }

    private fun moveCameraTo(position: LatLng, googleMap: GoogleMap) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                position,
                15f
            )
        )
    }

    override fun onOpenAddLoveSpotFabs() {
        closeMarkerFabMenu()
    }

    enum class MapMode {
        LOVE_SPOTS, LOVE_MAKINGS
    }
}
