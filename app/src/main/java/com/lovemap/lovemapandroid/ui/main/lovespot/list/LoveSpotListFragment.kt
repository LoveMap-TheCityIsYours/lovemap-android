package com.lovemap.lovemapandroid.ui.main.lovespot.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.ListLocation
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotSearchRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.events.LoveSpotListFiltersChanged
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.AddLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotRecyclerViewAdapter.Companion.EDIT_LOVE_SPOT_MENU_ID
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotRecyclerViewAdapter.Companion.MAKE_LOVE_LOVE_SPOT_MENU_ID
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotRecyclerViewAdapter.Companion.REPORT_LOVE_SPOT_MENU_ID
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotRecyclerViewAdapter.Companion.WISHLIST_LOVE_SPOT_MENU_ID
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoveSpotListFragment : Fragment() {
    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService

    private lateinit var progressBar: ProgressBar
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: LoveSpotRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_love_spot_list, container, false) as LinearLayout
        recycleView = linearLayout.findViewById(R.id.loveSpotList)
        progressBar = linearLayout.findViewById(R.id.loveSpotListProgressBar)
        recycleView.isClickable = true
        adapter = LoveSpotRecyclerViewAdapter(ArrayList())
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = adapter
        return linearLayout
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = try {
            adapter.position
        } catch (e: Exception) {
            return super.onContextItemSelected(item)
        }
        val loveSpotHolder = adapter.values[position]
        when (item.itemId) {
            EDIT_LOVE_SPOT_MENU_ID -> {
                val intent = Intent(requireContext(), AddLoveSpotActivity::class.java)
                intent.putExtra(AddLoveSpotActivity.EDIT, loveSpotHolder.id)
                startActivity(intent)
            }
            WISHLIST_LOVE_SPOT_MENU_ID -> {
                appContext.toaster.showToast(R.string.not_yet_implemented)
            }
            MAKE_LOVE_LOVE_SPOT_MENU_ID -> {
                appContext.selectedLoveSpotId = loveSpotHolder.id
                val intent = Intent(requireContext(), RecordLoveActivity::class.java)
                startActivity(intent)
            }
            REPORT_LOVE_SPOT_MENU_ID -> {
                appContext.selectedLoveSpotId = loveSpotHolder.id
                val intent = Intent(requireContext(), ReportLoveSpotActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onContextItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoveSpotListFiltersChanged(event: LoveSpotListFiltersChanged) {
        updateData(event.request, event.listOrdering, event.listLocation)
    }

    private fun updateData(
        request: LoveSpotSearchRequest,
        listOrdering: ListOrdering,
        listLocation: ListLocation
    ) {
        MainScope().launch {
            recycleView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            val loveSpots = loveSpotService.getLoveSpotHolderList(
                listOrdering = listOrdering,
                listLocation = listLocation,
                loveSpotSearchRequest = request
            )
            adapter.updateData(loveSpots)
            adapter.notifyDataSetChanged()
            recycleView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}