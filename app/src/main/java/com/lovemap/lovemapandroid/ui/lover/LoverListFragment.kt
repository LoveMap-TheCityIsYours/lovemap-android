package com.lovemap.lovemapandroid.ui.lover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.service.lover.relation.RelationService.Companion.LOVER_LIST_TYPE
import com.lovemap.lovemapandroid.ui.lover.LoverRecyclerViewAdapter.Type.FOLLOWERS
import com.lovemap.lovemapandroid.ui.lover.LoverRecyclerViewAdapter.Type.FOLLOWINGS
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoverListFragment : Fragment() {

    private val appContext = AppContext.INSTANCE
    private val relationService = appContext.relationService
    private val loverItems = ArrayList<LoverViewWithoutRelationDto>()

    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: LoverRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lover_list, container, false) as LinearLayout
        view.layoutTransition.setAnimateParentHierarchy(false)
        initViews(view)
        initializeData()
        return view
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.loverListSwipeRefresh)
        recyclerView = view.findViewById(R.id.loverListRecyclerView)
        progressBar = view.findViewById(R.id.loverListProgressBar)
        recyclerAdapter = LoverRecyclerViewAdapter(requireActivity(), LOVER_LIST_TYPE, loverItems)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setOnRefreshListener()
    }

    private fun initializeData() {
        MainScope().launch {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            updateData()
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            recyclerView.smoothScrollToPosition(0)
        }
    }

    suspend fun updateData() {
        val lovers: List<LoverViewWithoutRelationDto> = when (LOVER_LIST_TYPE) {
            FOLLOWERS -> relationService.getFollowers()
            FOLLOWINGS -> relationService.getFollowings()
        }
        doUpdate(lovers)
    }

    private fun doUpdate(lovers: List<LoverViewWithoutRelationDto>) {
        recyclerAdapter.lastPosition = -1
        recyclerAdapter.updateData(lovers)
        recyclerAdapter.notifyDataSetChanged()
        recyclerView.invalidate()
    }

    private fun setOnRefreshListener() {
        swipeRefresh.setOnRefreshListener {
            recyclerView.post {
                MainScope().launch {
                    updateData()
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }
}
