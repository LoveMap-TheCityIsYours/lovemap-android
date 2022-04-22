package com.smackmap.smackmapandroid.ui.main

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val icons = arrayOf(
            getDrawable(R.drawable.ic_baseline_favorite_border_24),
            getDrawable(R.drawable.ic_baseline_search_24),
            getDrawable(R.drawable.ic_baseline_location_on_24),
            getDrawable(R.drawable.ic_baseline_person_24),
        )

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        for (i: Int in 0..3) {
            tabLayout.getTabAt(i)?.icon = icons[i]
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}