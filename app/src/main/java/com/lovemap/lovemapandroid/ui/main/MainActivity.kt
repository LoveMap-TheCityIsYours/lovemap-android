package com.lovemap.lovemapandroid.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityMainBinding
import com.lovemap.lovemapandroid.ui.events.MapMarkerEventListener
import com.lovemap.lovemapandroid.ui.main.lovespot.AddLoveSpotActivity


private const val MAP_PAGE = 2

class MainActivity : AppCompatActivity(), MapMarkerEventListener {

    private val appContext = AppContext.INSTANCE
    private lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding

    private lateinit var changeMapModeFab: FloatingActionButton

    private lateinit var addLoveSpotFab: FloatingActionButton
    private lateinit var okFab: FloatingActionButton
    private lateinit var cancelFab: FloatingActionButton

    private lateinit var tabLayout: TabLayout
    private lateinit var icons: Array<Drawable>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.icon = icons[position]
        }.attach()

        addLoveSpotFab.setOnClickListener {
            if (!appContext.areAddLoveSpotFabsOpen) {
                openAddLoveSpotFabs()
            } else {
                closeAddLoveSpotFabs()
            }
        }
        okFab.setOnClickListener {
            startActivity(Intent(this, AddLoveSpotActivity::class.java))
        }
        cancelFab.setOnClickListener {
            closeAddLoveSpotFabs()
        }

        // Starter page is map for performance reasons
        viewPager2.post {
            viewPager2.setCurrentItem(MAP_PAGE, true)
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != MAP_PAGE) {
                    closeAddLoveSpotFabs()
                }
            }
        })
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        icons = initIcons()
        viewPager2 = binding.viewPager
        viewPager2.adapter = ViewPagerAdapter(this)
        tabLayout = binding.tabLayout

        changeMapModeFab = binding.changeMapModeFab

        addLoveSpotFab = binding.addLoveSpotFab
        okFab = binding.okFab
        cancelFab = binding.cancelFab
    }

    override fun onResume() {
        super.onResume()
        appContext.mapMarkerEventListener = this
        if (appContext.shouldCloseFabs) {
            appContext.shouldCloseFabs = false
            closeAddLoveSpotFabs()
        }
    }

    override fun onMarkerClicked() {
        closeAddLoveSpotFabs()
    }

    override fun onMapClicked() {
        closeAddLoveSpotFabs()
    }

    private fun openAddLoveSpotFabs() {
        if (!appContext.areAddLoveSpotFabsOpen) {
            appContext.mainActivityEventListener.onOpenAddLoveSpotFabs()
            viewPager2.post {
                viewPager2.setCurrentItem(MAP_PAGE, true)
            }
            okFab.visibility = View.VISIBLE
            cancelFab.visibility = View.VISIBLE

            okFab.animate().rotationBy(360f)
                .translationX(-resources.getDimension(R.dimen.standard_75))
            cancelFab.animate().rotationBy(360f)
                .translationX(-resources.getDimension(R.dimen.standard_150))
            addLoveSpotFab.animate().rotationBy(360f)

            appContext.selectedMarker?.hideInfoWindow()
            appContext.selectedMarker = null

            val crosshair: ImageView? = findViewById(R.id.centerCrosshair)
            if (crosshair != null) {
                crosshair.visibility = View.VISIBLE
                val addLovespotText: TextView = findViewById(R.id.mapAddLovespotText)
                addLovespotText.visibility = View.VISIBLE
            }

            appContext.areAddLoveSpotFabsOpen = true
        }
    }

    private fun closeAddLoveSpotFabs() {
        if (appContext.areAddLoveSpotFabsOpen) {
            okFab.animate().rotationBy(360f).translationX(0f).withEndAction {
                okFab.visibility = View.GONE
            }
            cancelFab.animate().rotationBy(360f).translationX(0f).withEndAction {
                cancelFab.visibility = View.GONE
            }
            addLoveSpotFab.animate().rotationBy(360f)

            val crosshair: ImageView? = findViewById(R.id.centerCrosshair)
            if (crosshair != null) {
                crosshair.visibility = View.GONE
                val addLovespotText: TextView = findViewById(R.id.mapAddLovespotText)
                addLovespotText.visibility = View.GONE
            }

            appContext.areAddLoveSpotFabsOpen = false
        }
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
                R.drawable.ic_baseline_location_on_24
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_person_24
            )!!,
        )
    }

    override fun onBackPressed() {
        val exit = Intent(Intent.ACTION_MAIN)
        exit.addCategory(Intent.CATEGORY_HOME)
        exit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(exit)
    }
}