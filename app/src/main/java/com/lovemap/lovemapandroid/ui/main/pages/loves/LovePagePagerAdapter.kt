package com.lovemap.lovemapandroid.ui.main.pages.loves

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class LovePagePagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
//class LovePagePagerAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle) :
//    FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoveWishlistSubPageFragment()
            1 -> LoveHistorySubPageFragment()
            else -> throw IllegalArgumentException("Position $position is impossible.")
        }
    }
}