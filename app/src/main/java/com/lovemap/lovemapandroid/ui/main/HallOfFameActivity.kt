package com.lovemap.lovemapandroid.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityHallOfFameBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HallOfFameActivity : AppCompatActivity() {

    private val tag = "HallOfFameActivity"
    private val loverService = AppContext.INSTANCE.loverService

    private lateinit var binding: ActivityHallOfFameBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var hallOfFameRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HallOfFameRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHallOfFameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.hallOfFameProgressBar
        hallOfFameRefresh = binding.hallOfFameRefresh
        recyclerView = binding.hallOfFameRecyclerView
        setRefreshListener()
        initializeRecyclerView()
        initializeData()
    }

    private fun setRefreshListener() {
        hallOfFameRefresh.setOnRefreshListener {
            MainScope().launch { updateData() }
        }
    }

    private suspend fun updateData() {
        hallOfFameRefresh.isRefreshing = true
        doUpdate()
        hallOfFameRefresh.isRefreshing = false
    }

    private fun initializeRecyclerView() {
        adapter = HallOfFameRecyclerViewAdapter(this, ArrayList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
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

    private suspend fun doUpdate() {
        val hallOfFame = loverService.getHallOfFame()
        adapter.lastPosition = -1
        adapter.updateData(hallOfFame)
        adapter.notifyDataSetChanged()
        recyclerView.invalidate()
    }
}