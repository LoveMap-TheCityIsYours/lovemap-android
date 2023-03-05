package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.MultiLoverNewsFeedResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.api.newsfeed.PrivateLoversNewsFeedResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.ui.main.newsfeed.MultiLoverViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.PrivateLoversViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_MULTI_LOVER
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_PRIVATE_LOVERS
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MultiLoverViewHolderCreator : NewsFeedItemCreator<MultiLoverViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_MULTI_LOVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiLoverViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_feed_item_multi_lover, parent, false)
        return MultiLoverViewHolder(view)
    }
}


class MultiLoverViewHolderBinder : NewsFeedItemBinder {
    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService
    private val metadataStore = appContext.metadataStore

    override fun viewHolderType(): Class<MultiLoverViewHolder> {
        return MultiLoverViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val multiLover = response.multiLover!!
        runCatching {
            setMultiLoverView(
                viewHolder as MultiLoverViewHolder,
                response,
                multiLover.copy(lovers = multiLover.lovers.take(viewHolder.maxMultiLovers))
            )
        }.onFailure { e ->
            Log.e("MultiLoverViewHolderBinder", "setMultiLoverView shitted itself", e)
        }
    }

    private fun setMultiLoverView(
        viewHolder: MultiLoverViewHolder,
        item: NewsFeedItemResponse,
        multiLover: MultiLoverNewsFeedResponse
    ) {
        hideLastRows(viewHolder, multiLover)
        multiLover.lovers.forEachIndexed { index, lover ->
            viewHolder.loverNames[index].text = lover.displayName
            viewHolder.layouts[index].visibility = View.VISIBLE
            viewHolder.layouts[index].setOnClickListener {
                openOtherLoverView(lover.id, viewHolder.context)
            }
        }
        viewHolder.setTexts(
            publicLover = null,
            unknownActorText = R.string.new_multi_lover_joined,
            publicActorText = R.string.new_lover_joined,
            happenedAt = item.happenedAt,
            country = item.country
        )
        MainScope().launch {
            multiLover.lovers.forEachIndexed { index, lover ->
                if (lover.id == appContext.userId) {
                    viewHolder.loverNames[index].text = metadataStore.getLover().displayName
                }
                loverService.getOtherByIdWithoutRelation(lover.id)?.let {
                    LoverService.otherLoverId = it.id
                    if (lover.id != appContext.userId) {
                        viewHolder.loverNames[index].text = it.displayName
                    }
                }
            }
        }
    }

    private fun hideLastRows(
        viewHolder: MultiLoverViewHolder,
        multiLover: MultiLoverNewsFeedResponse
    ) {
        val hideLastN = viewHolder.maxMultiLovers - multiLover.lovers.size

        viewHolder.layouts.forEachIndexed { index, relativeLayout ->
            if (hideLastN - index <= hideLastN) {
                relativeLayout.visibility = View.GONE
            }
        }
    }
}

