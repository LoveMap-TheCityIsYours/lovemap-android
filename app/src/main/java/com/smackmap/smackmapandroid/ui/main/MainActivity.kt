package com.smackmap.smackmapandroid.ui.main

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
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.databinding.ActivityMainBinding
import com.smackmap.smackmapandroid.ui.main.smackspotlist.AddSmackSpotActivity

private const val MAP_PAGE = 2

class MainActivity : AppCompatActivity(), MapMarkerClickedListener {

    private val appContext = AppContext.INSTANCE
    private lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding

    private lateinit var changeMapModeFab: FloatingActionButton

    private lateinit var addSmackSpotFab: FloatingActionButton
    private lateinit var okFab: FloatingActionButton
    private lateinit var cancelFab: FloatingActionButton

    private lateinit var addSmackFab: FloatingActionButton
    private lateinit var addToWishlistFab: FloatingActionButton
    private lateinit var reportSmackSpotFab: FloatingActionButton

    private lateinit var tabLayout: TabLayout
    private lateinit var icons: Array<Drawable>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.icon = icons[position]
        }.attach()

        addSmackSpotFab.setOnClickListener {
            if (!appContext.areAddSmackSpotFabsOpen) {
                openAddSmackSpotFabs()
            } else {
                closeAddSmackSpotFabs()
            }
        }

        okFab.setOnClickListener {
            startActivity(Intent(this, AddSmackSpotActivity::class.java))
        }

        cancelFab.setOnClickListener {
            closeAddSmackSpotFabs()
        }

        // Starter page is map for performance reasons
        viewPager2.post {
            viewPager2.setCurrentItem(MAP_PAGE, true)
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != MAP_PAGE) {
                    closeAddSmackSpotFabs()
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
//        viewPager2.setPageTransformer(ZoomOutPageTransformer())
        tabLayout = binding.tabLayout

        changeMapModeFab = binding.changeMapModeFab

        addSmackSpotFab = binding.addSmackSpotFab
        okFab = binding.okFab
        cancelFab = binding.cancelFab

        addSmackFab = binding.addSmackFab
        addToWishlistFab = binding.addToWishlistFab
        reportSmackSpotFab = binding.reportSmackSpotFab
    }

    override fun onResume() {
        super.onResume()
        appContext.mapMarkerClickedListener = this
        if (appContext.shouldCloseFabs) {
            appContext.shouldCloseFabs = false
            closeAddSmackSpotFabs()
            closeMarkerFabMenu()
        }
    }

    override fun onMarkerOpened() {
        openMarkerFabMenu()
    }

    override fun onMarkerClosed() {
        closeMarkerFabMenu()
    }

    private fun openAddSmackSpotFabs() {
        if (!appContext.areAddSmackSpotFabsOpen) {
            closeMarkerFabMenu()
            viewPager2.post {
                viewPager2.setCurrentItem(MAP_PAGE, true)
            }
            okFab.visibility = View.VISIBLE
            cancelFab.visibility = View.VISIBLE

            okFab.animate().rotationBy(360f)
                .translationX(-resources.getDimension(R.dimen.standard_110))
            cancelFab.animate().rotationBy(360f)
                .translationX(-resources.getDimension(R.dimen.standard_185))
            addSmackSpotFab.animate().rotationBy(360f)

            val crosshair: ImageView? = findViewById(R.id.centerCrosshair)
            if (crosshair != null) {
                crosshair.visibility = View.VISIBLE
                val addSmackspotText: TextView = findViewById(R.id.mapAddSmackspotText)
                addSmackspotText.visibility = View.VISIBLE
            }

            appContext.areAddSmackSpotFabsOpen = true
        }
    }

    private fun closeAddSmackSpotFabs() {
        if (appContext.areAddSmackSpotFabsOpen) {
            okFab.animate().rotationBy(720f).translationX(0f).withEndAction {
                okFab.visibility = View.GONE
            }
            cancelFab.animate().rotationBy(720f).translationX(0f).withEndAction {
                cancelFab.visibility = View.GONE
            }
            addSmackSpotFab.animate().rotationBy(360f)

            val crosshair: ImageView? = findViewById(R.id.centerCrosshair)
            if (crosshair != null) {
                crosshair.visibility = View.GONE
                val addSmackspotText: TextView = findViewById(R.id.mapAddSmackspotText)
                addSmackspotText.visibility = View.GONE
            }

            appContext.areAddSmackSpotFabsOpen = false
        }
    }

    private fun openMarkerFabMenu() {
        if (!appContext.areMarkerFabsOpen) {
            closeAddSmackSpotFabs()
            appContext.areMarkerFabsOpen = true
            reportSmackSpotFab.animate().rotationBy(360f)
                .translationX(resources.getDimension(R.dimen.standard_75))
            addToWishlistFab.animate().rotationBy(360f)
                .translationX(resources.getDimension(R.dimen.standard_150))
            addSmackFab.animate().rotationBy(360f)
                .translationX(resources.getDimension(R.dimen.standard_225))
        }
    }

    private fun closeMarkerFabMenu() {
        if (appContext.areMarkerFabsOpen) {
            appContext.areMarkerFabsOpen = false
            reportSmackSpotFab.animate().rotationBy(360f)
                .translationX(0f)
            addToWishlistFab.animate().rotationBy(360f)
                .translationX(0f)
            addSmackFab.animate().rotationBy(360f)
                .translationX(0f)
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