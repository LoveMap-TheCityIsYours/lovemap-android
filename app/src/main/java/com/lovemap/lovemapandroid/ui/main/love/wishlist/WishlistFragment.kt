package com.lovemap.lovemapandroid.ui.main.love.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.data.WishlistItemHolder
import com.lovemap.lovemapandroid.ui.events.WishlistUpdatedEvent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WishlistFragment : Fragment() {

    private val appContext = AppContext.INSTANCE
    private val wishlistService = appContext.wishlistService

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WishlistItemRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val layout =
            inflater.inflate(R.layout.fragment_wishlist, container, false) as RelativeLayout
        layout.layoutTransition.setAnimateParentHierarchy(false)
        progressBar = layout.findViewById(R.id.wishlistProgressBar)
        initializeRecyclerView(layout)
        initializeData()
        return layout
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWishlistUpdated(event: WishlistUpdatedEvent) {
        event.wishlistHolders?.let { doUpdate(it) } ?: run {
            MainScope().launch { updateData() }
        }
    }

    private fun initializeRecyclerView(layout: RelativeLayout) {
        recyclerView = layout.findViewById(R.id.wishlistRecyclerView)
        adapter = WishlistItemRecyclerAdapter(ArrayList(), recyclerView, this.requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
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
        doUpdate(wishlistService.getWishlistHolderList())
    }

    private fun doUpdate(wishlistHolders: List<WishlistItemHolder>) {
        adapter.lastPosition = -1
        adapter.updateData(wishlistHolders)
        adapter.notifyDataSetChanged()
        recyclerView.invalidate()
    }
}