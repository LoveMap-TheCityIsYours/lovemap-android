package com.lovemap.lovemapandroid.ui.main.lovespot.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotReviewListFragment : Fragment() {
    private val reviewService = AppContext.INSTANCE.loveSpotReviewService

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoveSpotReviewItemRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_review_list, container, false) as LinearLayout
        recyclerView = linearLayout.findViewById(R.id.reviewList) as RecyclerView
        progressBar = linearLayout.findViewById(R.id.reviewListProgressBar)
        adapter = LoveSpotReviewItemRecyclerViewAdapter(ArrayList())
        MainScope().launch {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }
        return linearLayout
    }

    override fun onResume() {
        super.onResume()
        MainScope().launch {
            adapter.updateData(reviewService.getReviewHoldersBySpot())
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }
    }
}