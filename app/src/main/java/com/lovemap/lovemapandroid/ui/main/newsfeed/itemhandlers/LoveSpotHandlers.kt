package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.LoveSpotNewsFeedResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.BaseLoveSpotViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoveSpotMultiViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoveSpotViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_LOVE_SPOT
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotViewHolderCreator : NewsFeedItemCreator<LoveSpotViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_LOVE_SPOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoveSpotViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_feed_item_lovespot, parent, false)
        return LoveSpotViewHolder(view)
    }
}


class LoveSpotViewHolderBinder : NewsFeedItemBinder {

    override fun viewHolderType(): Class<LoveSpotViewHolder> {
        return LoveSpotViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val loveSpot = response.loveSpot!!
        setLoveSpotView(viewHolder as LoveSpotViewHolder, response, loveSpot)
    }

    private fun setLoveSpotView(
        viewHolder: LoveSpotViewHolder,
        item: NewsFeedItemResponse,
        loveSpot: LoveSpotNewsFeedResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, loveSpot.id)
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = R.string.somebody_added_a_new_lovespot,
            publicActorText = R.string.public_actor_added_a_new_lovespot,
            happenedAt = item.happenedAt,
            country = item.country
        )

        viewHolder.newsFeedLoveSpotName.text = loveSpot.name
        viewHolder.newsFeedSpotDescription.text = loveSpot.description
        LoveSpotUtils.setTypeImage(loveSpot.type, viewHolder.newsFeedSpotTypeImage)
        LoveSpotUtils.setType(loveSpot.type, viewHolder.newsFeedSpotType)
        setLoveSpotViews(loveSpot.id, viewHolder)
    }
}

fun setLoveSpotViews(
    loveSpotId: Long,
    viewHolder: BaseLoveSpotViewHolder
) {
    MainScope().launch {
        AppContext.INSTANCE.loveSpotService.findLocallyOrFetch(loveSpotId)
            ?.let { loveSpot ->
                viewHolder.newsFeedLoveSpotName.text = loveSpot.name
                LoveSpotUtils.setTypeImage(loveSpot.type, viewHolder.newsFeedSpotTypeImage)
                LoveSpotUtils.setType(loveSpot.type, viewHolder.newsFeedSpotType)
                LoveSpotUtils.setRating(loveSpot.averageRating, viewHolder.newsFeedSpotRating)
                LoveSpotUtils.setAvailability(
                    loveSpot.availability,
                    viewHolder.newsFeedSpotAvailability
                )
                LoveSpotUtils.setRisk(loveSpot.averageDanger, viewHolder.newsFeedSpotRisk)
                LoveSpotUtils.setDistance(loveSpot, viewHolder.newsFeedSpotDistance)
                if (viewHolder is LoveSpotViewHolder) {
                    viewHolder.newsFeedSpotDescription.text = loveSpot.description
                }
                if (viewHolder is LoveSpotMultiViewHolder) {
                    viewHolder.loveSpotDescription.text = loveSpot.description
                }
            }
    }
}

