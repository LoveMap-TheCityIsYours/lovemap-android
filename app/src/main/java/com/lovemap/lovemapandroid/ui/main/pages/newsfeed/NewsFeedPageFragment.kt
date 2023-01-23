package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.main.newsfeed.NewsFeedFragment

class NewsFeedPageFragment : Fragment() {
    companion object {
        private const val TAG = "NewsFeedPageFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_news_feed_page, container, false) as LinearLayout
        view.layoutTransition.setAnimateParentHierarchy(false)
        val newsFeedFragment =
            NewsFeedFragment.newInstance(NewsFeedFragment.NewsFeedType.GLOBAL, null)
        childFragmentManager.beginTransaction()
            .add(R.id.newsFeedFragmentContainer, newsFeedFragment)
            .commitAllowingStateLoss()
        return view
    }
}
