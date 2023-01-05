package com.lovemap.lovemapandroid.ui.main.pages.loves

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveListFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveHistorySubPageFragment : Fragment() {

    private lateinit var loveListSwipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_love_sub_page, container, false) as LinearLayout
        setRefreshListener(linearLayout)
        return linearLayout
    }

    private fun setRefreshListener(linearLayout: LinearLayout) {
        loveListSwipeRefresh = linearLayout.findViewById(R.id.loveListSwipeRefresh)
        loveListSwipeRefresh.setOnRefreshListener {
            MainScope().launch { updateData() }
        }
    }

    private suspend fun updateData() {
        loveListSwipeRefresh.isRefreshing = true
        val fragmentLovesLoveList = childFragmentManager
            .findFragmentById(R.id.fragmentLovesLoveList) as LoveListFragment
        fragmentLovesLoveList.updateData()
        loveListSwipeRefresh.isRefreshing = false
    }
}