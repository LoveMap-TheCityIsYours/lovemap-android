package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.LoverNewsFeedResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoverViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_LOVER
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PublicLoverViewHolderCreator : NewsFeedItemCreator<LoverViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_LOVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoverViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_feed_item_lover, parent, false)
        return LoverViewHolder(view)
    }
}


class PublicLoverViewHolderBinder : NewsFeedItemBinder {
    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService
    private val metadataStore = appContext.metadataStore

    override fun viewHolderType(): Class<LoverViewHolder> {
        return LoverViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val lover = response.lover!!
        setLoverView(viewHolder as LoverViewHolder, response, lover)
    }

    private fun setLoverView(
        viewHolder: LoverViewHolder,
        item: NewsFeedItemResponse,
        lover: LoverNewsFeedResponse
    ) {
        viewHolder.itemView.setOnClickListener {
            openOtherLoverView(lover.id, viewHolder.context)
        }
        viewHolder.setTexts(
            publicLover = null,
            unknownActorText = R.string.new_lover_joined,
            publicActorText = R.string.new_lover_joined,
            happenedAt = item.happenedAt,
            country = item.country
        )
        viewHolder.newsFeedLoverName.text = lover.displayName
        viewHolder.newsFeedLoverPoints.text = lover.points.toString()
        ProfileUtils.setRanks(lover.points, viewHolder.newsFeedLoverRank)
        MainScope().launch {
            if (lover.id == appContext.userId) {
                viewHolder.newsFeedLoverName.text = metadataStore.getLover().displayName
            }
            loverService.getOtherByIdWithoutRelation(lover.id)?.let {
                LoverService.otherLoverId = it.id
                if (lover.id != appContext.userId) {
                    viewHolder.newsFeedLoverName.text = it.displayName
                }
                viewHolder.newsFeedLoverPoints.text = it.points.toString()
                ProfileUtils.setRanks(it.points, viewHolder.newsFeedLoverRank)
            }
        }
    }
}

