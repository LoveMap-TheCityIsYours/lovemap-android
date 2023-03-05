package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.ui.lover.ViewOtherLoverActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.main.newsfeed.BaseViewHolder

interface NewsFeedItemCreator<out BaseViewHolder> {
    fun viewTypeId(): Int
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
}

interface NewsFeedItemBinder {
    fun viewHolderType(): Class<out BaseViewHolder>

    fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    )
}

fun openOtherLoverView(loverId: Long, context: Context) {
    LoverService.otherLoverId = loverId
    context.startActivity(
        Intent(context, ViewOtherLoverActivity::class.java)
    )
}

fun setLoveSpotOnClickListener(
    viewHolder: RecyclerView.ViewHolder,
    loveSpotId: Long
) {
    viewHolder.itemView.setOnClickListener {
        AppContext.INSTANCE.selectedLoveSpotId = loveSpotId
        MapContext.selectedMarker = null
        val context = viewHolder.itemView.context
        context.startActivity(
            Intent(context, LoveSpotDetailsActivity::class.java)
        )
    }
}


