package com.lovemap.lovemapandroid.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lovemap.lovemapandroid.ui.main.pages.*

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LovesPageFragment()
            1 -> DiscoverPageFragment()
            2 -> LoveMapPageFragment()
            3 -> ProfilePageFragment()
            4 -> DiscoverPageFragment2()
            else -> throw IllegalArgumentException("Position $position is impossible.")
        }
    }
}