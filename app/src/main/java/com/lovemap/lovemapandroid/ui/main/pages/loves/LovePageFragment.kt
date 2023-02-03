package com.lovemap.lovemapandroid.ui.main.pages.loves

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext

class LovePageFragment : Fragment() {
    private val appContext = AppContext.INSTANCE

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_love_page, container, false)
        viewPager2 = view.findViewById(R.id.lovePageViewPager)
        tabLayout = view.findViewById(R.id.lovePageTabLayout)

//        viewPager2.adapter = LovePagePagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager2.adapter = LovePagePagerAdapter(requireActivity())

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.wishlist_title)
                1 -> getString(R.string.loves_title)
                else -> "Impossible"
            }
        }.attach()

        viewPager2.setCurrentItem(1, false)

        Log.i(TAG, "onCreateView called")

        return view
    }

    override fun onResume() {
        super.onResume()

        Log.i(TAG, "onResume called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        viewPager2.adapter = null
        Log.i(TAG, "onDestroyView called")
    }

    companion object {
        private const val TAG = "LovePageFragment"
    }
}
