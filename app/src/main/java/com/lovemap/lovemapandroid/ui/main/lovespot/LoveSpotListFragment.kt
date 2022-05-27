package com.lovemap.lovemapandroid.ui.main.lovespot

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

/**
 * A fragment representing a list of Items.
 */
class LoveSpotListFragment : Fragment() {
    private val loveSpotService = AppContext.INSTANCE.loveSpotService

    private lateinit var recycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recycleView =
            inflater.inflate(R.layout.fragment_lovespot_list, container, false) as RecyclerView
        recycleView.isClickable = true
        MainScope().launch {
            with(recycleView) {
                layoutManager = LinearLayoutManager(context)
            }
        }
        return recycleView
    }

    override fun onResume() {
        super.onResume()
        MainScope().launch {
            with(recycleView) {
                adapter = LoveSpotRecyclerViewAdapter(loveSpotService.getLoveHolderList())
                adapter?.notifyDataSetChanged()
            }
        }
    }

}