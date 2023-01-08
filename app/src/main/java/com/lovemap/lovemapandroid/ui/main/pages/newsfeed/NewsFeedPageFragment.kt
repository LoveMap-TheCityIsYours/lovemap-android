package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NewsFeedPageFragment : Fragment() {
    companion object {
        private val TAG = "NewsFeedPageFragment"
    }

    private val newsFeedService = AppContext.INSTANCE.newsFeedService

    private val newsFeedItems = ArrayList<NewsFeedItemResponse?>()
    private var isLoading = false
    private val size = 15
    private var page = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsFeedRecyclerAdapter: NewsFeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_feed_page, container, false)
        recyclerView = view.findViewById(R.id.newsFeedRecyclerView)
        newsFeedRecyclerAdapter = NewsFeedRecyclerAdapter(newsFeedItems)
        recyclerView.adapter = newsFeedRecyclerAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoading) {
                    if (layoutManager.findLastVisibleItemPosition() == newsFeedItems.size - 1) {
                        isLoading = true
                        page++
                        fetchPage()
                    }
                }
            }
        })
        fetchPage()

        return view
    }

    private fun fetchPage() {
        Log.i(TAG, "Fetching page '$page' size '$size'}")
        newsFeedItems.add(null)
        newsFeedRecyclerAdapter.notifyItemInserted(newsFeedItems.size - 1)

        MainScope().launch {
            runCatching {
                newsFeedService.getPage(page, size)
            }.onSuccess { pageResponse ->
                removeLastNull()
                Log.i(TAG, "Adding newsFeedItems to adapter list")
                pageResponse.forEach { newsFeedItems.add(it) }
                newsFeedRecyclerAdapter.notifyDataSetChanged()
                isLoading = false
            }.onFailure {
                Log.i(TAG, "Failed to get newsFeedItems")
                removeLastNull()
            }
        }
    }

    private fun removeLastNull() {
        newsFeedItems.removeAt(newsFeedItems.size - 1)
        val scrollPosition = newsFeedItems.size
        newsFeedRecyclerAdapter.notifyItemRemoved(scrollPosition)
    }
}