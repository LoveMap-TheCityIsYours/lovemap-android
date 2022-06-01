package com.lovemap.lovemapandroid.ui.main.love

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
import com.lovemap.lovemapandroid.ui.main.love.LoveRecyclerViewAdapter.Companion.DELETE_LOVE_MENU_ID
import com.lovemap.lovemapandroid.ui.main.love.LoveRecyclerViewAdapter.Companion.EDIT_LOVE_MENU_ID
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class LoveListFragment : Fragment() {

    private val loveService = AppContext.INSTANCE.loveService
    private var isLoveSpotBased: Boolean = false
    private var isPartnerBased: Boolean = false
    private var isClickable: Boolean = true
    var isClickableOverride: Boolean? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: LoveRecyclerViewAdapter

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val attributes = requireActivity().obtainStyledAttributes(attrs, R.styleable.ListFragment)
        isLoveSpotBased = attributes.getBoolean(R.styleable.ListFragment_love_spot_based, false)
        isPartnerBased = attributes.getBoolean(R.styleable.ListFragment_partner_based, false)
        isClickable = attributes.getBoolean(R.styleable.ListFragment_is_clickable, true)
        attributes.recycle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val linearLayout =
            inflater.inflate(R.layout.fragment_love_list, container, false) as LinearLayout
        recycleView = linearLayout.findViewById(R.id.loveList)
        progressBar = linearLayout.findViewById(R.id.loveListProgressBar)
        recycleView.isClickable = isClickable
        adapter = LoveRecyclerViewAdapter(ArrayList(), isClickable)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(context)
        registerForContextMenu(recycleView)
        return linearLayout
    }

    override fun onStart() {
        super.onStart()
        adapter.isClickable = isClickable()
    }

    override fun onResume() {
        super.onResume()
        MainScope().launch {
            updateData()
            progressBar.visibility = View.GONE
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = try {
            adapter.position
        } catch (e: Exception) {
            return super.onContextItemSelected(item)
        }
        val loveHolder = adapter.values[position]
        when (item.itemId) {
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

    private suspend fun updateData() {
        when {
            isLoveSpotBased -> {
                adapter.updateData(loveService.getLoveHolderListForSpot())
            }
            isPartnerBased -> {
                adapter.updateData(loveService.getLoveHolderListForPartner())
            }
            else -> {
                adapter.updateData(loveService.getLoveHolderList())
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun isClickable(): Boolean {
        return if (isClickableOverride == null) {
            isClickable
        } else {
            isClickableOverride!!
        }
    }
}