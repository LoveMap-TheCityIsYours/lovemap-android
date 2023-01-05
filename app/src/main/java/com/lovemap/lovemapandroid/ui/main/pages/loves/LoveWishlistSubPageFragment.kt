package com.lovemap.lovemapandroid.ui.main.pages.loves

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveListFragment
import com.lovemap.lovemapandroid.ui.main.love.wishlist.WishlistFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveWishlistSubPageFragment : Fragment() {

    private lateinit var wishlistSwipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_love_wishlist_sub_page, container, false) as LinearLayout
        setRefreshListener(linearLayout)
        return linearLayout
    }

    private fun setRefreshListener(linearLayout: LinearLayout) {
        wishlistSwipeRefresh = linearLayout.findViewById(R.id.wishlistSwipeRefresh)
        wishlistSwipeRefresh.setOnRefreshListener {
            MainScope().launch { updateData() }
        }
    }

    private suspend fun updateData() {
        wishlistSwipeRefresh.isRefreshing = true
        val fragmentLovesWishlist = childFragmentManager
            .findFragmentById(R.id.fragmentLovesWishlist) as WishlistFragment
        fragmentLovesWishlist.updateData()
        wishlistSwipeRefresh.isRefreshing = false
    }
}