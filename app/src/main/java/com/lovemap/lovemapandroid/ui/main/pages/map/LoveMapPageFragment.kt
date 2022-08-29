package com.lovemap.lovemapandroid.ui.main.pages.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.maps.android.clustering.ClusterManager
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotListDto
import com.lovemap.lovemapandroid.service.LoveService
import com.lovemap.lovemapandroid.service.LoveSpotService
import com.lovemap.lovemapandroid.ui.events.MapMarkerEventListener
import com.lovemap.lovemapandroid.ui.main.MAP_PAGE
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.AddLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.pages.map.LoveMapPageFragment.MapMode.LOVE_MAKINGS
import com.lovemap.lovemapandroid.ui.main.pages.map.LoveMapPageFragment.MapMode.LOVE_SPOTS
import com.lovemap.lovemapandroid.utils.pixelToDp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor


@SuppressLint("MissingPermission")
class LoveMapPageFragment : Fragment(), OnMapReadyCallback, MapMarkerEventListener {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService: LoveSpotService = appContext.loveSpotService
    private val loveService: LoveService = appContext.loveService
    private var cameraMoved = false
    private var localSpotsDrawn = false
    private var mapMode = LOVE_SPOTS
    private val drawnSpots = HashSet<Long>()
    private var zoomLevel: Float = 1f

    private var viewPager2: ViewPager2? = null
    private lateinit var loveSpotInfoWindowAdapter: LoveSpotInfoWindowAdapter
    private lateinit var mapFragment: SupportMapFragment

    private lateinit var addLoveText: TextView
    private lateinit var addLoveFab: FloatingActionButton
    private lateinit var toWishlistText: TextView
    private lateinit var addToWishlistFab: FloatingActionButton
    private lateinit var reportSpotText: TextView
    private lateinit var reportLoveSpotFab: FloatingActionButton

    private lateinit var changeMapModeFab: ExtendedFloatingActionButton

    private lateinit var addLoveSpotFab: ExtendedFloatingActionButton
    private lateinit var addSpotOkFab: FloatingActionButton
    private lateinit var addSpotCancelFab: FloatingActionButton

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var clusterManager: ClusterManager<LoveSpotClusterItem>
    private lateinit var loveSpotClusterRenderer: LoveSpotClusterRender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        askForLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_love_map_page, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        addLoveText = view.findViewById(R.id.loveOnSpotText)
        addLoveFab = view.findViewById(R.id.addLoveFab)
        toWishlistText = view.findViewById(R.id.spotWishlistText)
        addToWishlistFab = view.findViewById(R.id.addToWishlistFab)
        reportSpotText = view.findViewById(R.id.spotReportText)
        reportLoveSpotFab = view.findViewById(R.id.reportLoveSpotFab)
        changeMapModeFab = view.findViewById(R.id.changeMapModeFab)
        addLoveSpotFab = view.findViewById(R.id.addLoveSpotFab)
        addSpotOkFab = view.findViewById(R.id.addSpotOkFab)
        addSpotCancelFab = view.findViewById(R.id.addSpotCancelFab)
        loveSpotInfoWindowAdapter = LoveSpotInfoWindowAdapter(
            loveSpotService,
            requireActivity(),
        )
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
            appContext.shouldClearMap = true
            mapMode = if (mapMode == LOVE_SPOTS) {
                loveSpotClusterRenderer.drawLoveMakings = true
                appContext.toaster.showToast(R.string.showing_your_love_makings)
                changeMapModeFab.setText(R.string.show_love_spots)
                LOVE_MAKINGS
            } else {
                loveSpotClusterRenderer.drawLoveMakings = false
                appContext.toaster.showToast(R.string.showing_love_spots)
                changeMapModeFab.setText(R.string.show_lovemakings)
                LOVE_SPOTS
            }
            mapFragment.getMapAsync(this)
        }
        addLoveSpotFab.setOnClickListener {
            if (!appContext.areAddLoveSpotFabsOpen) {
                openAddLoveSpotFabs()
            } else {
                closeAddLoveSpotFabs()
            }
        }
        addSpotOkFab.setOnClickListener {
            if (zoomLevel < 17) {
                appContext.toaster.showToast(R.string.zoomInMore)
            } else {
                startActivity(Intent(requireContext(), AddLoveSpotActivity::class.java))
            }
        }
        addSpotCancelFab.setOnClickListener {
            closeAddLoveSpotFabs()
        }
    }

    override fun onResume() {
        super.onResume()
        mapFragment.getMapAsync(this)
        viewPager2 = getViewPager2(requireView())
        appContext.mapMarkerEventListener = this
        viewPager2?.let {
            val crosshair: ImageView = it.findViewById(R.id.centerCrosshair)
            val addLovespotText: TextView = it.findViewById(R.id.mapAddLovespotText)
            if (appContext.shouldCloseFabs) {
                appContext.shouldCloseFabs = false
                onMapClicked()
            }
            if (appContext.areAddLoveSpotFabsOpen) {
                crosshair.visibility = View.VISIBLE
                addLovespotText.visibility = View.VISIBLE
            } else {
                crosshair.visibility = View.GONE
                addLovespotText.visibility = View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(googleMap: GoogleMap) {
        if (!this::clusterManager.isInitialized) {
            clusterManager = ClusterManager(requireContext(), googleMap)
            googleMap.setInfoWindowAdapter(clusterManager.markerManager)
            clusterManager.markerCollection.setInfoWindowAdapter(loveSpotInfoWindowAdapter)
            loveSpotClusterRenderer = LoveSpotClusterRender(requireContext(), googleMap, clusterManager)
            clusterManager.renderer = loveSpotClusterRenderer
            googleMap.setOnMarkerClickListener(clusterManager)
            clusterManager.markerCollection.setOnInfoWindowClickListener {
                if (appContext.selectedLoveSpotId != null) {
                    startActivity(Intent(requireContext(), LoveSpotDetailsActivity::class.java))
                }
            }
        }

        val thisView = requireView()
        viewPager2 = getViewPager2(thisView)
        viewPager2?.let { vp2 ->
            val linearLayout = vp2.parent as ViewGroup
            val tabLayout = linearLayout.findViewById<TabLayout>(R.id.tab_layout)
            setMyLocation(googleMap)
            setMapBehavior(googleMap, tabLayout, thisView)
            MainScope().launch {
                if (appContext.shouldClearMap) {
                    clusterManager.clearItems()
                    drawnSpots.clear()
                    appContext.shouldClearMap = false
                    localSpotsDrawn = false
                }
                putMarkersFromLocalDb()
                putMarkersBasedOnCamera(googleMap)
                appContext.zoomOnLoveSpot?.let {
                    moveCameraTo(LatLng(it.latitude, it.longitude), googleMap)
                    appContext.zoomOnLoveSpot = null
                }
            }
        }
    }

    private fun getViewPager2(thisView: View): ViewPager2? {
        var view = thisView.parent
        while (view !is ViewPager2 && view != null) {
            if (view.parent == null) {
                return (view as ViewGroup).findViewById(R.id.view_pager)
            }
            view = view.parent
        }
        return if (view is ViewPager2) {
            view
        } else {
            null
        }
    }

    private fun setMyLocation(googleMap: GoogleMap) {
        if (appContext.locationEnabled) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private suspend fun putMarkersFromLocalDb() {
        if (!localSpotsDrawn) {
            localSpotsDrawn = true
            val localSpots = loveSpotService.listSpotsLocally()
            putLoveSpotListOnMap(localSpots)
        }
    }

    private suspend fun putMarkersBasedOnCamera(
        googleMap: GoogleMap
    ) {
        appContext.mapCameraTarget = googleMap.cameraPosition.target

        val visibleRegion = googleMap.projection.visibleRegion
        val loveSpotsInArea = loveSpotService
            .list(visibleRegion.latLngBounds)

        putLoveSpotListOnMap(loveSpotsInArea)

        if (appContext.selectedMarker != null) {
            appContext.selectedMarker!!.showInfoWindow()
            openMarkerFabMenu()
        } else {
            closeMarkerFabMenu()
        }
    }

    private suspend fun putLoveSpotListOnMap(
        loveSpotsInArea: LoveSpotListDto
    ) {
        if (loveSpotsInArea.deletedIds.isNotEmpty()) {
            clusterManager.clearItems()
            drawnSpots.clear()
            localSpotsDrawn = false
            putMarkersFromLocalDb()
        }
        if (mapMode == LOVE_MAKINGS) {
            val loves = loveService.list()
            val spotIdsWithLove = loves.map { it.loveSpotId }.toHashSet()
            loveSpotsInArea.loveSpots
                .filter { isNotDrawnYet(it) }
                .filter { loveSpot -> spotIdsWithLove.contains(loveSpot.id) }
                .forEach { clusterManager.addItem(LoveSpotClusterItem.ofLoveSpot(it)) }
        } else {
            loveSpotsInArea.loveSpots
                .filter { isNotDrawnYet(it) }
                .forEach { clusterManager.addItem(LoveSpotClusterItem.ofLoveSpot(it)) }
        }
        clusterManager.cluster()
    }

    private fun isNotDrawnYet(it: LoveSpot): Boolean {
        return if (!drawnSpots.contains(it.id)) {
            drawnSpots.add(it.id)
            true
        } else {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setMapBehavior(
        googleMap: GoogleMap,
        tabLayout: TabLayout,
        thisView: View
    ) {
        clusterManager.setOnClusterItemClickListener { item ->
            onMarkerClicked()
            appContext.selectedLoveSpotId = item.loveSpot.id
            appContext.selectedLoveSpot = item.loveSpot
            openMarkerFabMenu()
            true
        }
        clusterManager.markerCollection.setOnMarkerClickListener { marker ->
            appContext.selectedMarker = marker
            false
        }
        clusterManager.setOnClusterClickListener { cluster ->
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    cluster.position, floor(
                        googleMap.cameraPosition.zoom + 1.0
                    ).toFloat()
                ), 300,
                null
            )
            true
        }
        googleMap.setOnMapClickListener {
            viewPager2?.isUserInputEnabled = false
            onMapClicked()
            appContext.selectedMarker = null
        }
        googleMap.setOnCameraIdleListener {
            if (cameraMoved) {
                MainScope().launch {
                    putMarkersBasedOnCamera(googleMap)
                }
            }
            clusterManager.onCameraIdle()
        }
        googleMap.setOnCameraMoveListener {
            if (tabLayout.selectedTabPosition == 2) {
                viewPager2?.isUserInputEnabled = false
            }
            cameraMoved = true
            zoomLevel = googleMap.cameraPosition.zoom
        }
        thisView.setOnTouchListener { _, _ ->
            viewPager2?.isUserInputEnabled = true
            true
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2?.isUserInputEnabled = true
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
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
                    appContext.requestLocationUpdates()
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
                16f
            )
        )
    }

    override fun onMarkerClicked() {
        closeAddLoveSpotFabs()
    }

    override fun onMapClicked() {
        closeAddLoveSpotFabs()
        closeMarkerFabMenu()
    }

    private fun openAddLoveSpotFabs() {
        if (!appContext.areAddLoveSpotFabsOpen) {
            closeMarkerFabMenu()
            viewPager2?.post {
                viewPager2?.setCurrentItem(MAP_PAGE, true)
            }
            addSpotOkFab.visibility = View.VISIBLE
            addSpotCancelFab.visibility = View.VISIBLE

            addSpotOkFab.animate().rotationBy(720f)
                .translationX(-resources.getDimension(R.dimen.standard_150))
            addSpotCancelFab.animate().rotationBy(720f)
                .translationX(-resources.getDimension(R.dimen.standard_225))

            appContext.selectedMarker?.hideInfoWindow()
            appContext.selectedMarker = null
            appContext.selectedLoveSpot = null
            appContext.selectedLoveSpotId = null

            val crosshair: ImageView? = requireActivity().findViewById(R.id.centerCrosshair)
            if (crosshair != null) {
                crosshair.visibility = View.VISIBLE
                val addLoveSpotText: TextView =
                    requireActivity().findViewById(R.id.mapAddLovespotText)
                addLoveSpotText.visibility = View.VISIBLE
            }

            appContext.areAddLoveSpotFabsOpen = true
        }
    }

    private fun closeAddLoveSpotFabs() {
        if (appContext.areAddLoveSpotFabsOpen) {
            addSpotOkFab.animate().rotationBy(720f).translationX(0f).withEndAction {
                addSpotOkFab.visibility = View.GONE
            }
            addSpotCancelFab.animate().rotationBy(720f).translationX(0f).withEndAction {
                addSpotCancelFab.visibility = View.GONE
            }

            val crosshair: ImageView? = requireActivity().findViewById(R.id.centerCrosshair)
            if (crosshair != null) {
                crosshair.visibility = View.GONE
                val addLovespotText: TextView =
                    requireActivity().findViewById(R.id.mapAddLovespotText)
                addLovespotText.visibility = View.GONE
            }

            appContext.areAddLoveSpotFabsOpen = false
        }
    }

    enum class MapMode {
        LOVE_SPOTS, LOVE_MAKINGS
    }
}