package com.lovemap.lovemapandroid.ui.main.love

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveListFragment : Fragment() {

    private val loveService = AppContext.INSTANCE.loveService

    private lateinit var recycleView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recycleView = inflater.inflate(R.layout.fragment_lovespot_list, container, false) as RecyclerView

        // Set the adapter
        with(recycleView) {
            layoutManager = LinearLayoutManager(context)
            MainScope().launch {
                adapter = LoveRecyclerViewAdapter(loveService.initLoveHolderTreeSet())
            }
        }
        return recycleView
    }

    override fun onResume() {
        super.onResume()
        if (loveService.lastUpdatedIndex != -1) {
//            recycleView.adapter?.notifyItemInserted(loveService.lastUpdatedIndex)
            loveService.lastUpdatedIndex = -1
        }
    }
}