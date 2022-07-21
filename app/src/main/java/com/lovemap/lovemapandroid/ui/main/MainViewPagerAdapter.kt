package com.lovemap.lovemapandroid.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lovemap.lovemapandroid.ui.main.pages.DiscoverPageFragment2
import com.lovemap.lovemapandroid.ui.main.pages.LoveMapPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.LovesPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.ProfilePageFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LovesPageFragment()
            1 -> DiscoverPageFragment2()
            2 -> LoveMapPageFragment()
            3 -> ProfilePageFragment()
            else -> throw IllegalArgumentException("Position $position is impossible.")
        }
    }
}