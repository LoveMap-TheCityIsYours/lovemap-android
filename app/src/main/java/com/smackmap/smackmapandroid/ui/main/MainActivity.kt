package com.smackmap.smackmapandroid.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var isFabOpen = false
    private lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var addSmackFab: FloatingActionButton
    private lateinit var addSmackSpotFab: FloatingActionButton
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
            if (!isFabOpen) {
                showFabMenu()
            } else {
                closeFabMenu()
            }
        }

        // Starter page is map for performance reasons
        viewPager2.post {
            viewPager2.setCurrentItem(2, true)
        }
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
    }

    private fun showFabMenu() {
        isFabOpen = true
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
        isFabOpen = false
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
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}