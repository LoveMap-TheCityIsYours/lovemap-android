package com.smackmap.smackmapandroid.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
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
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var addSmackFab: FloatingActionButton
    private lateinit var addSmackSpotFab: FloatingActionButton

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val icons = initIcons()

        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout
        viewPager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = icons[position]
        }.attach()
        fab = binding.fab
        addSmackFab = binding.addSmackFab
        addSmackSpotFab = binding.addSmackSpotFab

        fab.setOnClickListener {
            if (!isFabOpen) {
                showFabMenu()
            } else {
                closeFabMenu()
            }
        }
    }

    private fun showFabMenu() {
        isFabOpen = true
        addSmackFab.animate().rotationBy(360f)
            .translationY(-resources.getDimension(R.dimen.standard_55))
        addSmackSpotFab.animate().rotationBy(360f)
            .translationY(-resources.getDimension(R.dimen.standard_105))
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

    private fun initIcons(): Array<Drawable?> {
        val icons = arrayOf(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_favorite_border_24
            ),
            AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_search_24),
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_baseline_location_on_24
            ),
            AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_person_24),
        )
        return icons
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}