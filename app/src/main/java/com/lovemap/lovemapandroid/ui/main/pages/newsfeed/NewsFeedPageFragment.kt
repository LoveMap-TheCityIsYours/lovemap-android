package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.*

class NewsFeedPageFragment : Fragment() {
    companion object {
        private val TAG = "NewsFeedPageFragment"
    }

    private val newsFeedService = AppContext.INSTANCE.newsFeedService

    private val newsFeedItems = ArrayList<NewsFeedItemResponse?>()
    private var isLoading = true
    private var newsFeedEnded = false
    private val size = 15
    private var page = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsFeedRecyclerAdapter: NewsFeedRecyclerAdapter
    private lateinit var newsFeedSwipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_feed_page, container, false)
        initViews(view)
        fetchPage()
        addOnScrollListener()
        setOnRefreshListener()
        return view
    }

    private fun initViews(view: View) {
        newsFeedSwipeRefresh = view.findViewById(R.id.newsFeedSwipeRefresh)
        recyclerView = view.findViewById(R.id.newsFeedRecyclerView)
        newsFeedRecyclerAdapter = NewsFeedRecyclerAdapter(newsFeedItems)
        recyclerView.adapter = newsFeedRecyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchPage() {
        Log.i(TAG, "Fetching page '$page' size '$size'}")
        recyclerView.post {
            newsFeedItems.add(null)
            newsFeedRecyclerAdapter.notifyItemInserted(newsFeedItems.size - 1)

            MainScope().launch {
                runCatching {
                    newsFeedService.getPage(page, size)
                }.onSuccess { pageResponse ->
                    removeLastNull()
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
                    removeLastNull()
                }
            }
        }

    }

    private fun removeLastNull() {
        val index = newsFeedItems.lastIndexOf(null)
        if (index >= 0) {
            val removed = newsFeedItems.removeAt(index)
            Log.i(TAG, "removeLastNull called. index: '$index', removed: '$removed'")
            newsFeedRecyclerAdapter.notifyItemRemoved(index)
        }
    }

    private fun addOnScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.i(TAG, "onScrolled! newsFeedItems.size: ${newsFeedItems.size}")
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

    private fun setOnRefreshListener() {
        newsFeedSwipeRefresh.setOnRefreshListener {
            page = 0
            newsFeedRecyclerAdapter.lastPosition = -1
            newsFeedItems.clear()

            MainScope().launch {
                runCatching {
                    newsFeedService.getPage(page, size)
                }.onSuccess { pageResponse ->
                    Log.i(TAG, "Adding newsFeedItems to adapter list: ${pageResponse.size}")
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
                    newsFeedRecyclerAdapter.notifyDataSetChanged()
                    recyclerView.invalidate()
                    isLoading = false
                    newsFeedEnded = false
                    newsFeedSwipeRefresh.isRefreshing = false
                }
            }
        }
    }
}
