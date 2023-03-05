package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.LoveSpotReviewNewsFeedResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoveSpotReviewViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoveSpotReviewViews
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_LOVE_SPOT_REVIEW
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotReviewViewHolderCreator : NewsFeedItemCreator<LoveSpotReviewViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_LOVE_SPOT_REVIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoveSpotReviewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_feed_item_lovespot_review, parent, false)
        return LoveSpotReviewViewHolder(view)
    }
}


class LoveSpotReviewViewHolderBinder : NewsFeedItemBinder {
    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    override fun viewHolderType(): Class<LoveSpotReviewViewHolder> {
        return LoveSpotReviewViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val loveSpotReview = response.loveSpotReview!!
        setLoveSpotReviewView(viewHolder as LoveSpotReviewViewHolder, response, loveSpotReview)
    }

    private fun setLoveSpotReviewView(
        viewHolder: LoveSpotReviewViewHolder,
        item: NewsFeedItemResponse,
        loveSpotReview: LoveSpotReviewNewsFeedResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, loveSpotReview.loveSpotId)
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = R.string.somebody_reviewed_spot,
            publicActorText = R.string.public_actor_reviewed_spot,
            happenedAt = item.happenedAt,
            country = item.country
        )
        setLoveSpotReviewViews(loveSpotReview, viewHolder.views)
    }

    private fun setLoveSpotReviewViews(
        loveSpotReview: LoveSpotReviewNewsFeedResponse,
        views: LoveSpotReviewViews
    ) {
        LoveSpotUtils.setRating(
            loveSpotReview.reviewStars.toDouble(),
            views.newsFeedSpotRating
        )
        LoveSpotUtils.setRisk(loveSpotReview.riskLevel.toDouble(), views.newsFeedSpotRisk)
        if (loveSpotReview.reviewText.isEmpty()) {
            views.newsFeedSpotReviewText.visibility = View.GONE
        } else {
            views.newsFeedSpotReviewText.text = loveSpotReview.reviewText
            views.newsFeedSpotReviewText.visibility = View.VISIBLE
        }
        MainScope().launch {
            loveSpotReviewService.findLocallyOrFetch(loveSpotReview.id)?.let { review ->
                LoveSpotUtils.setRating(
                    review.reviewStars.toDouble(),
                    views.newsFeedSpotRating
                )
                LoveSpotUtils.setRisk(review.riskLevel.toDouble(), views.newsFeedSpotRisk)
                if (review.reviewText.isEmpty()) {
                    views.newsFeedSpotReviewText.visibility = View.GONE
                } else {
                    views.newsFeedSpotReviewText.text = review.reviewText
                    views.newsFeedSpotReviewText.visibility = View.VISIBLE
                }
            }
            loveSpotService.findLocallyOrFetch(loveSpotReview.loveSpotId)?.let { loveSpot ->
                views.newsFeedLoveSpotName.text = loveSpot.name
                LoveSpotUtils.setTypeImage(loveSpot.type, views.newsFeedSpotTypeImage)
                LoveSpotUtils.setDistance(loveSpot, views.newsFeedSpotDistance)
            }
        }
    }


}

