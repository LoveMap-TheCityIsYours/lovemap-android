package com.lovemap.lovemapandroid.ui.main.lovespot

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

/**
 * A fragment representing a list of Items.
 */
class LoveSpotListFragment : Fragment() {
    private val loveSpotService = AppContext.INSTANCE.loveSpotService

    private lateinit var progressBar: ProgressBar
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: LoveSpotRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_lovespot_list, container, false) as LinearLayout
        recycleView = linearLayout.findViewById(R.id.loveSpotList)
        progressBar = linearLayout.findViewById(R.id.loveSpotListProgressBar)
        recycleView.isClickable = true
        adapter = LoveSpotRecyclerViewAdapter(ArrayList())
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = adapter
        return linearLayout
    }

    override fun onResume() {
        super.onResume()
        MainScope().launch {
            adapter.updateData(loveSpotService.getLoveHolderList())
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }
    }

}