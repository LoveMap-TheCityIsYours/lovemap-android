package com.lovemap.lovemapandroid.ui.main.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.events.LoveSpotWidgetMoreClicked
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

const val SEARCH_PAGE = 1

class DiscoverPageFragment2 : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_discover_page2, container, false)
        viewPager2 = view.findViewById(R.id.discoverViewPager)
        tabLayout = view.findViewById(R.id.discoverTabLayout)
        viewPager2.adapter = DiscoverViewPagerAdapter(requireActivity())

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.recommendations_title)
                1 -> getString(R.string.search_title)
                else -> "Impossible"
            }
        }.attach()

        return view
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoveSpotWidgetMoreClicked(event: LoveSpotWidgetMoreClicked) {
        LoveSpotListFilterState.listOrdering = event.ordering
        viewPager2.post {
            viewPager2.setCurrentItem(SEARCH_PAGE, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}