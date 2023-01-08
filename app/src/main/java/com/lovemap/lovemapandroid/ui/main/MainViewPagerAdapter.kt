package com.lovemap.lovemapandroid.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lovemap.lovemapandroid.ui.main.pages.discover.DiscoverPageFragment2
import com.lovemap.lovemapandroid.ui.main.pages.map.LoveMapPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.loves.LoveHistorySubPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.ProfilePageFragment
import com.lovemap.lovemapandroid.ui.main.pages.loves.LovePageFragment
import com.lovemap.lovemapandroid.ui.main.pages.newsfeed.NewsFeedPageFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LovePageFragment()
            1 -> DiscoverPageFragment2()
            2 -> LoveMapPageFragment()
            3 -> NewsFeedPageFragment()
            4 -> ProfilePageFragment()
            else -> throw IllegalArgumentException("Position $position is impossible.")
        }
    }
}