package com.smackmap.smackmapandroid.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.smackmap.smackmapandroid.ui.main.smacklist.SmackFragment
import com.smackmap.smackmapandroid.ui.main.smackspotlist.SmackspotFragment

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
            return SmackFragment()
        } else if (position == 1) {
            return SmackspotFragment()
        } else if (position == 3) {
            return SettingsFragment()
        }
        return PlaceholderFragment.newInstance(position + 1)
    }
}