package com.lovemap.lovemapandroid.ui.main.pages.discover

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvancedLoveSpotListFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.widget.LoveSpotRecommendationPageFragment

class DiscoverViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoveSpotRecommendationPageFragment()
            1 -> AdvancedLoveSpotListFragment()
            else -> throw IllegalArgumentException("Position $position is impossible.")
        }
    }
}