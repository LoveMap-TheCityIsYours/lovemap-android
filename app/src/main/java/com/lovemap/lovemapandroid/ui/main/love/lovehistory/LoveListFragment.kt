package com.lovemap.lovemapandroid.ui.main.love.lovehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
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
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.data.LoveHolder
import com.lovemap.lovemapandroid.ui.events.LoveListUpdatedEvent
import com.lovemap.lovemapandroid.ui.events.ShowOnMapClickedEvent
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveRecyclerViewAdapter.Companion.DELETE_LOVE_MENU_ID
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveRecyclerViewAdapter.Companion.EDIT_LOVE_MENU_ID
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveRecyclerViewAdapter.Companion.SHOW_LOVE_ON_MAP_MENU_ID
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoveListFragment : Fragment() {
    var isClickableOverride: Boolean? = null

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService

    private var isLoveSpotBased: Boolean = false
    private var isPartnerBased: Boolean = false
    private var isClickable: Boolean = true

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoveRecyclerViewAdapter

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val attributes =
            requireActivity().obtainStyledAttributes(attrs, R.styleable.LoveListFragment)
        isLoveSpotBased = attributes.getBoolean(R.styleable.LoveListFragment_love_spot_based, false)
        isPartnerBased = attributes.getBoolean(R.styleable.LoveListFragment_partner_based, false)
        if (!isLoveSpotBased && !isPartnerBased) {
            EventBus.getDefault().register(this)
        }
        isClickable = attributes.getBoolean(R.styleable.LoveListFragment_is_clickable, true)
        attributes.recycle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_love_list, container, false) as LinearLayout
        progressBar = linearLayout.findViewById(R.id.loveListProgressBar)
        initializeRecyclerView(linearLayout)
        initializeData()
        return linearLayout
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isLoveSpotBased && !isPartnerBased) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun initializeRecyclerView(linearLayout: LinearLayout) {
        recyclerView = linearLayout.findViewById(R.id.loveList)
        recyclerView.isClickable = isClickable
        adapter = LoveRecyclerViewAdapter(ArrayList(), isClickable)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun initializeData() {
        adapter.isClickable = isClickable()
        MainScope().launch {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            updateData()
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            recyclerView.smoothScrollToPosition(0)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (adapter.values.isEmpty()) {
            return false
        }
        val position = try {
            adapter.position
        } catch (e: Exception) {
            return super.onContextItemSelected(item)
        }
        val loveHolder = adapter.values[position]

        when (item.itemId) {
            SHOW_LOVE_ON_MAP_MENU_ID -> {
                EventBus.getDefault().post(ShowOnMapClickedEvent(loveHolder.loveSpotId))
            }
            EDIT_LOVE_MENU_ID -> {
                val intent = Intent(requireContext(), RecordLoveActivity::class.java)
                intent.putExtra(RecordLoveActivity.EDIT, loveHolder.id)
                startActivity(intent)
            }
            DELETE_LOVE_MENU_ID -> {
                MainScope().launch {
                    loveService.delete(loveHolder.id)
                    updateData()
                }
            }
        }
        return super.onContextItemSelected(item)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWishlistUpdated(event: LoveListUpdatedEvent) {
        doUpdate(event.loveHolders)
    }

    suspend fun updateData() {
        val loveHolders: List<LoveHolder> = when {
            isLoveSpotBased -> {
                loveService.getLoveHolderListForSpot()
            }
            isPartnerBased -> {
                loveService.getLoveHolderListForPartner()
            }
            else -> {
                loveService.getLoveHolderList()
            }
        }
        doUpdate(loveHolders)
    }

    private fun doUpdate(loveHolders: List<LoveHolder>) {
        adapter.lastPosition = -1
        adapter.updateData(loveHolders)
        adapter.notifyDataSetChanged()
        recyclerView.invalidate()
    }

    private fun isClickable(): Boolean {
        return if (isClickableOverride == null) {
            isClickable
        } else {
            isClickableOverride!!
        }
    }
}