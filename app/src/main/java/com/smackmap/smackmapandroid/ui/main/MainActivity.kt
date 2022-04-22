package com.smackmap.smackmapandroid.ui.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private lateinit var fab3: FloatingActionButton
    private lateinit var fab4: FloatingActionButton

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
        fab1 = binding.fab1
        fab2 = binding.fab2
        fab3 = binding.fab3
        fab4 = binding.fab4

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            if (!isFabOpen) {
                showFabMenu()
            } else {
                closeFabMenu()
            }
        }
    }

    private fun showFabMenu() {
        isFabOpen = true
        fab1.animate().rotationBy(360f).translationY(-resources.getDimension(R.dimen.standard_55))
        fab2.animate().rotationBy(360f).translationY(-resources.getDimension(R.dimen.standard_105))
        fab3.animate().rotationBy(360f).translationY(-resources.getDimension(R.dimen.standard_155))
        fab4.animate().rotationBy(360f).translationY(-resources.getDimension(R.dimen.standard_205))
        fab.animate().rotationBy(180f)
        fab.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_24))
        fab.animate().rotationBy(180f)
    }

    private fun closeFabMenu() {
        isFabOpen = false
        fab1.animate().rotationBy(360f).translationY(0f)
        fab2.animate().rotationBy(360f).translationY(0f)
        fab3.animate().rotationBy(360f).translationY(0f)
        fab4.animate().rotationBy(360f).translationY(0f)
        fab.animate().rotationBy(180f)
        fab.setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24))
        fab.animate().rotationBy(180f)
    }

    private fun initIcons(): Array<Drawable?> {
        val icons = arrayOf(
            getDrawable(R.drawable.ic_baseline_favorite_border_24),
            getDrawable(R.drawable.ic_baseline_search_24),
            getDrawable(R.drawable.ic_baseline_location_on_24),
            getDrawable(R.drawable.ic_baseline_person_24),
        )
        return icons
    }
}