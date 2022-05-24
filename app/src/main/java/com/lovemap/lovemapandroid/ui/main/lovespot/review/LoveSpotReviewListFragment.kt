package com.lovemap.lovemapandroid.ui.main.lovespot.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotReviewListFragment : Fragment() {
    private val reviewService = AppContext.INSTANCE.loveSpotReviewService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recyclerView =
            inflater.inflate(R.layout.fragment_review_list, container, false) as RecyclerView

        MainScope().launch {
            with(recyclerView) {
                layoutManager = LinearLayoutManager(context)
                adapter = LoveSpotReviewItemRecyclerViewAdapter(reviewService.getReviewHoldersBySpot())
            }
        }

        return recyclerView
    }
}