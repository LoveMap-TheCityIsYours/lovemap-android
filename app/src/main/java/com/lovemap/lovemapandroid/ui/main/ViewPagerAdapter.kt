package com.lovemap.lovemapandroid.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lovemap.lovemapandroid.ui.main.pages.DiscoverPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.ProfilePageFragment
import com.lovemap.lovemapandroid.ui.main.pages.LoveMapPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.LovesPageFragment
import java.lang.IllegalArgumentException

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return LovesPageFragment()
        } else if (position == 1) {
            return DiscoverPageFragment()
        } else if (position == 2) {
            return LoveMapPageFragment()
        } else if (position == 3) {
            return ProfilePageFragment()
        }
        throw IllegalArgumentException("Position $position is impossible.")
    }
}