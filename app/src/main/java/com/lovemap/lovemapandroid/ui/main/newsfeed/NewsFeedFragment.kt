package com.lovemap.lovemapandroid.ui.main.newsfeed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.NewsFeedFragment.NewsFeedType.FOLLOWING
import com.lovemap.lovemapandroid.ui.main.newsfeed.NewsFeedFragment.NewsFeedType.GLOBAL
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val NEWS_FEED_TYPE = "newsFeedType"
private const val OTHER_LOVER_ID = "otherLoverId"

class NewsFeedFragment : Fragment() {
    private lateinit var newsFeedType: NewsFeedType
    private var otherLoverId: Long = 0

    private val newsFeedService = AppContext.INSTANCE.newsFeedService
    private val relationService = AppContext.INSTANCE.relationService
    private val loverService = AppContext.INSTANCE.loverService

    private val newsFeedItems = ArrayList<NewsFeedItemResponse>()
    private var isLoading = true
    private var newsFeedEnded = false
    private val size = 15
    private var page = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsFeedRecyclerAdapter: NewsFeedRecyclerAdapter
    private lateinit var newsFeedSwipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsFeedType = arguments?.let {
            NewsFeedType.valueOf(it.getString(NEWS_FEED_TYPE, GLOBAL.name))
        } ?: GLOBAL
        otherLoverId = arguments?.getLong(OTHER_LOVER_ID) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_feed, container, false) as LinearLayout
        view.layoutTransition.setAnimateParentHierarchy(false)
        initViews(view)
        if (newsFeedType == GLOBAL) {
            fetchPage()
            recyclerView.smoothScrollToPosition(0)
            addOnScrollListener()
            setOnRefreshPageListener()
        } else {
            fetchAll()
            setOnRefreshAllListener()
        }
        return view
    }

    private fun initViews(view: View) {
        newsFeedSwipeRefresh = view.findViewById(R.id.newsFeedSwipeRefresh)
        recyclerView = view.findViewById(R.id.newsFeedRecyclerView)
        newsFeedRecyclerAdapter = NewsFeedRecyclerAdapter(requireActivity(), newsFeedItems)
        recyclerView.adapter = newsFeedRecyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchPage() {
        Log.i(TAG, "Fetching page '$page' size '$size'}")
        recyclerView.post {
            newsFeedItems.add(NewsFeedItemResponse.LOADING)
            newsFeedRecyclerAdapter.notifyItemInserted(newsFeedItems.size - 1)

            MainScope().launch {
                runCatching {
                    newsFeedService.getPage(page, size)
                }.onSuccess { pageResponse ->
                    removeLastLoading()
                    if (pageResponse.isEmpty()) {
                        newsFeedEnded = true
                    } else {
                        Log.i(TAG, "Adding newsFeedItems to adapter list: ${pageResponse.size}")
                        pageResponse.forEach { newsFeedItems.add(it) }
                        Log.i(TAG, "newsFeedItems.size: ${newsFeedItems.size}")
                        Log.i(TAG, "newsFeedItems: $newsFeedItems")
                        newsFeedRecyclerAdapter.notifyDataSetChanged()
                        newsFeedSwipeRefresh.isRefreshing = false
                        isLoading = false
                        page++
                    }
                }.onFailure {
                    Log.i(TAG, "Failed to get newsFeedItems")
                    isLoading = false
                    newsFeedSwipeRefresh.isRefreshing = false
                    removeLastLoading()
                }
            }
        }

    }

    private fun removeLastLoading() {
        val index = newsFeedItems.lastIndexOf(NewsFeedItemResponse.LOADING)
        if (index >= 0) {
            val removed = newsFeedItems.removeAt(index)
            Log.i(TAG, "removeLastLoading called. index: '$index', removed: '$removed'")
            newsFeedRecyclerAdapter.notifyItemRemoved(index)
        }
    }

    private fun addOnScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoading && !newsFeedEnded && page > 0) {
                    if (layoutManager.findLastVisibleItemPosition() == newsFeedItems.size - 1) {
                        isLoading = true
                        Log.i(TAG, "onScrolled! fetching page $page")
                        fetchPage()
                    }
                }
            }
        })
    }

    private fun setOnRefreshPageListener() {
        newsFeedSwipeRefresh.setOnRefreshListener {
            isLoading = true
            recyclerView.post {
                page = 0
                newsFeedRecyclerAdapter.lastPosition = -1
                newsFeedItems.clear()
                newsFeedRecyclerAdapter.notifyDataSetChanged()
                newsFeedItems.add(NewsFeedItemResponse.LOADING)
                newsFeedRecyclerAdapter.notifyItemInserted(newsFeedItems.size - 1)

                MainScope().launch {
                    runCatching {
                        newsFeedService.getPage(page, size)
                    }.onSuccess { pageResponse ->
                        Log.i(TAG, "Adding newsFeedItems to adapter list: ${pageResponse.size}")
                        removeLastLoading()
                        pageResponse.forEach { newsFeedItems.add(it) }
                        Log.i(TAG, "newsFeedItems.size: ${newsFeedItems.size}")
                        Log.i(TAG, "newsFeedItems: $newsFeedItems")
                        newsFeedRecyclerAdapter.notifyDataSetChanged()
                        recyclerView.invalidate()
                        newsFeedSwipeRefresh.isRefreshing = false
                        isLoading = false
                        newsFeedEnded = false
                        page++
                    }.onFailure {
                        Log.i(TAG, "Failed to get newsFeedItems")
                        removeLastLoading()
                        isLoading = false
                        newsFeedEnded = false
                        newsFeedSwipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }


    private fun fetchAll() {
        Log.i(TAG, "fetchAll")
        recyclerView.post {
            recyclerView.smoothScrollToPosition(0)
            newsFeedItems.add(NewsFeedItemResponse.LOADING)
            newsFeedRecyclerAdapter.notifyItemInserted(newsFeedItems.size - 1)

            MainScope().launch {
                runCatching {
                    if (newsFeedType == FOLLOWING) {
                        relationService.getFollowingNewsFeed()
                    } else {
                        loverService.getLoverActivities(otherLoverId)
                    }
                }.onSuccess { newsFeedResponse ->
                    removeLastLoading()
                    if (newsFeedResponse.isEmpty()) {
                        newsFeedEnded = true
                    } else {
                        Log.i(TAG, "Adding newsFeedItems to adapter list: ${newsFeedResponse.size}")
                        newsFeedResponse.forEach { newsFeedItems.add(it) }
                        Log.i(TAG, "newsFeedItems.size: ${newsFeedItems.size}")
                        Log.i(TAG, "newsFeedItems: $newsFeedItems")
                        newsFeedRecyclerAdapter.notifyDataSetChanged()
                        newsFeedSwipeRefresh.isRefreshing = false
                    }
                }.onFailure {
                    Log.i(TAG, "Failed to get newsFeedItems")
                    newsFeedSwipeRefresh.isRefreshing = false
                    removeLastLoading()
                }
            }
        }

    }

    private fun setOnRefreshAllListener() {
        newsFeedSwipeRefresh.setOnRefreshListener {
            recyclerView.post {
                newsFeedRecyclerAdapter.lastPosition = -1
                newsFeedItems.clear()
                newsFeedRecyclerAdapter.notifyDataSetChanged()
                fetchAll()
            }
        }
    }

    companion object {
        private const val TAG = "NewsFeedFragment"

        @JvmStatic
        fun newInstance(newsFeedType: NewsFeedType, otherLoverId: Long? = null) =
            NewsFeedFragment().apply {
                arguments = Bundle().apply {
                    putString(NEWS_FEED_TYPE, newsFeedType.name)
                    otherLoverId?.let { putLong(OTHER_LOVER_ID, it) }
                }
            }
    }

    enum class NewsFeedType {
        GLOBAL, FOLLOWING, LOVER_ACTIVITIES
    }
}