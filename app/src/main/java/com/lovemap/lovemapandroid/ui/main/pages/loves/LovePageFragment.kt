package com.lovemap.lovemapandroid.ui.main.pages.loves

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.pages.discover.DiscoverViewPagerAdapter

class LovePageFragment : Fragment() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val appContext = AppContext.INSTANCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_love_page, container, false)
        viewPager2 = view.findViewById(R.id.lovePageViewPager)
        tabLayout = view.findViewById(R.id.lovePageTabLayout)
        viewPager2.adapter = LovePagePagerAdapter(requireActivity())
        viewPager2.setCurrentItem(1, false)

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.wishlist_title)
                1 -> getString(R.string.loves_title)
                else -> "Impossible"
            }
        }.attach()

        return view
    }

}
