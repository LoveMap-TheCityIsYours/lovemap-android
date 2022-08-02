package com.lovemap.lovemapandroid.ui.main.pages

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lovemap.lovemapandroid.ui.main.lovespot.list.advanced.AdvancedLoveSpotListFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.widget.LoveSpotRecommendationPageFragment
import com.lovemap.lovemapandroid.ui.main.pages.*

class DiscoverViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

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