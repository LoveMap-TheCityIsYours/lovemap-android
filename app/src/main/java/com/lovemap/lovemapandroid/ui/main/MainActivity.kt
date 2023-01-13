package com.lovemap.lovemapandroid.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.databinding.ActivityMainBinding
import com.lovemap.lovemapandroid.ui.events.ShowOnMapClickedEvent
import com.lovemap.lovemapandroid.ui.utils.AlertDialogUtils
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.system.exitProcess


const val MAP_PAGE = 2

class MainActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService
    private val metadataStore = appContext.metadataStore

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var icons: Array<Drawable>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        initViews()

        MainScope().launch {
            val sanity = metadataStore.checkSanity()
            if (!sanity) {
                val loadingBarShower = LoadingBarShower(this@MainActivity).show()
                if (appContext.fetchMetadata()) {
                    metadataStore.updateMetadataVersion()
                }
                loadingBarShower.onResponse()
            }
        }

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.icon = icons[position]
        }.attach()

        // Starter page is map for performance reasons
        goToMapPage()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabIconColor =
                    ContextCompat.getColor(applicationContext, R.color.tabSelectedIconColor)
                val colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        tabIconColor,
                        BlendModeCompat.SRC_IN
                    )
                tab.icon?.colorFilter = colorFilter
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon?.clearColorFilter()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        MainScope().launch {
            if (!metadataStore.isTouAccepted())
                AlertDialogUtils.newDialog(
                    this@MainActivity,
                    R.string.terms_of_use_title,
                    R.string.terms_of_use_message,
                    {
                        MainScope().launch {
                            metadataStore.setTouAccepted(true)
                        }
                    },
                    {
                        MainScope().launch {
                            metadataStore.setTouAccepted(false)
                            onBackPressed()
                            exitProcess(0)
                        }
                    },
                    true
                )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowOnMapClicked(showOnMapClickedEvent: ShowOnMapClickedEvent) {
        MainScope().launch {
            val loveSpot = loveSpotService.findLocally(showOnMapClickedEvent.loveSpotId)
            if (loveSpot != null) {
                MapContext.zoomOnLoveSpot = loveSpot
                MapContext.selectedMarker = null
                appContext.selectedLoveSpotId = null
                goToMapPage()
            }
        }
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        icons = initIcons()
        viewPager2 = binding.viewPager
        viewPager2.adapter = MainViewPagerAdapter(this)
        tabLayout = binding.tabLayout
    }

    private fun initIcons(): Array<Drawable> {
        return arrayOf(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_two_hearts_com
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_search_24
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                android.R.drawable.ic_dialog_map
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_newspaper_24
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_person_24
            )!!,
        )
    }

    override fun onBackPressed() {
        if (isMapPageOpen()) {
            closeMapElements()
        } else if (isDiscoverPageOpen()) {
            navigateDiscoverPage()
        } else {
            goToMapPage()
        }
    }

    private fun isMapPageOpen() = tabLayout.selectedTabPosition == 2

    private fun closeMapElements() {
        if (MapContext.areAddLoveSpotFabsOpen) {
            MapContext.mapMarkerEventListener.onMapClicked()
        } else if (MapContext.areMarkerFabsOpen) {
            MapContext.mapMarkerEventListener.onMapClicked()
            MapContext.selectedMarker?.hideInfoWindow()
            MapContext.selectedMarker = null
        } else {
            val exit = Intent(Intent.ACTION_MAIN)
            exit.addCategory(Intent.CATEGORY_HOME)
            exit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(exit)
        }
    }

    private fun isDiscoverPageOpen() = tabLayout.selectedTabPosition == 1

    private fun navigateDiscoverPage() {
        val discoverTabLayout: TabLayout = findViewById(R.id.discoverTabLayout)
        val discoverViewPager: ViewPager2 = findViewById(R.id.discoverViewPager)
        if (discoverTabLayout.selectedTabPosition == 1) {
            discoverViewPager.post {
                discoverViewPager.setCurrentItem(0, true)
            }
        } else {
            goToMapPage()
        }
    }

    private fun goToMapPage() {
        viewPager2.post {
            viewPager2.setCurrentItem(MAP_PAGE, true)
        }
    }
}