package com.smackmap.smackmapandroid.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var areFabsOpen = false
    private var areAddSmackSpotFabsOpen = false
    private lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var addSmackFab: FloatingActionButton
    private lateinit var addSmackSpotFab: FloatingActionButton
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

        fab.setOnClickListener {
            if (!areFabsOpen) {
                showFabMenu()
            } else {
                closeFabMenu()
            }
        }

        addSmackSpotFab.setOnClickListener {
            if (!areAddSmackSpotFabsOpen) {
                openAddSmackSpotFabs()
            } else {
                closeAddSmackSpotFabs()
            }
        }

        okFab.setOnClickListener {

        }

        cancelFab.setOnClickListener {
            closeAddSmackSpotFabs()
        }

        // Starter page is map for performance reasons
        viewPager2.post {
            viewPager2.setCurrentItem(2, true)
        }
    }

    private fun openAddSmackSpotFabs() {
        viewPager2.post {
            viewPager2.setCurrentItem(2, true)
        }
        okFab.visibility = View.VISIBLE
        cancelFab.visibility = View.VISIBLE

        okFab.animate().rotationBy(720f)
            .translationX(-resources.getDimension(R.dimen.standard_110))
        cancelFab.animate().rotationBy(720f)
            .translationX(-resources.getDimension(R.dimen.standard_185))

        areAddSmackSpotFabsOpen = true
    }

    private fun closeAddSmackSpotFabs() {
        okFab.animate().rotationBy(720f).translationX(0f).withEndAction {
            okFab.visibility = View.GONE
        }
        cancelFab.animate().rotationBy(720f).translationX(0f).withEndAction {
            cancelFab.visibility = View.GONE
        }

        areAddSmackSpotFabsOpen = false
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        icons = initIcons()
        viewPager2 = binding.viewPager
        tabLayout = binding.tabLayout
        viewPager2.adapter = ViewPagerAdapter(this)
        fab = binding.fab
        addSmackFab = binding.addSmackFab
        addSmackSpotFab = binding.addSmackSpotFab
        okFab = binding.okFab
        cancelFab = binding.cancelFab
    }

    private fun showFabMenu() {
        areFabsOpen = true
        addSmackFab.animate().rotationBy(360f)
            .translationY(-resources.getDimension(R.dimen.standard_75))
        addSmackSpotFab.animate().rotationBy(360f)
            .translationY(-resources.getDimension(R.dimen.standard_150))
        fab.animate().rotationBy(180f)
        fab.setImageDrawable(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_remove_24
            )
        )
        fab.animate().rotationBy(180f)
    }

    private fun closeFabMenu() {
        areFabsOpen = false
        addSmackFab.animate().rotationBy(360f).translationY(0f)
        addSmackSpotFab.animate().rotationBy(360f).translationY(0f)
        fab.animate().rotationBy(180f)
        fab.setImageDrawable(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_add_24
            )
        )
        fab.animate().rotationBy(180f)
    }

    private fun initIcons(): Array<Drawable> {
        return arrayOf(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_favorite_border_24
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_search_24)!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_location_on_24
            )!!,
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_person_24)!!,
        )
    }

    override fun onBackPressed() {
        val exit = Intent(Intent.ACTION_MAIN)
        exit.addCategory(Intent.CATEGORY_HOME)
        exit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(exit)
    }
}