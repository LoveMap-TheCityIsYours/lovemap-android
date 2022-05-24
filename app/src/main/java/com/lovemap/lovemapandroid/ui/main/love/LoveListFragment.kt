package com.lovemap.lovemapandroid.ui.main.love

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
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
    private var isLoveSpotBased: Boolean = false
    private var isPartnerBased: Boolean = false
    private var isClickable: Boolean = true

    private lateinit var recycleView: RecyclerView

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
        recycleView =
            inflater.inflate(R.layout.fragment_love_list, container, false) as RecyclerView

        recycleView.isClickable = isClickable

        with(recycleView) {
            layoutManager = LinearLayoutManager(context)
            MainScope().launch {
                adapter = if (isLoveSpotBased) {
                    LoveRecyclerViewAdapter(loveService.getLoveHolderListForSpot(), isClickable)
                } else if (isPartnerBased) {
                    LoveRecyclerViewAdapter(loveService.getLoveHolderListForPartner(), isClickable)
                } else {
                    LoveRecyclerViewAdapter(loveService.getLoveHolderList(), isClickable)
                }
            }
        }
        return recycleView
    }

    override fun onResume() {
        super.onResume()
        if (loveService.lastUpdatedIndex != -1) {
            recycleView.adapter?.notifyDataSetChanged()
            loveService.lastUpdatedIndex = -1
        }
    }
}