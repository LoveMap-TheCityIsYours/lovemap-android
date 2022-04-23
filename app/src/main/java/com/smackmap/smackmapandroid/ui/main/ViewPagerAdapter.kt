package com.smackmap.smackmapandroid.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.smackmap.smackmapandroid.ui.main.pages.DiscoverPageFragment
import com.smackmap.smackmapandroid.ui.main.pages.ProfilePageFragment
import com.smackmap.smackmapandroid.ui.main.pages.SmackMapPageFragment
import com.smackmap.smackmapandroid.ui.main.pages.SmacksPageFragment
import java.lang.IllegalArgumentException

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return SmacksPageFragment()
        } else if (position == 1) {
            return DiscoverPageFragment()
        } else if (position == 2) {
            return SmackMapPageFragment()
        } else if (position == 3) {
            return ProfilePageFragment()
        }
        throw IllegalArgumentException("Position $position is impossible.")
    }
}