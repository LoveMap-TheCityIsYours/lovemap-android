package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.newsfeed.*
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoveSpotMultiViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_LOVE_SPOT_MULTI_EVENTS
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils

class LoveSpotMultiEventsViewHolderCreator : NewsFeedItemCreator<LoveSpotMultiViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_LOVE_SPOT_MULTI_EVENTS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoveSpotMultiViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_feed_item_lovespot_multi_events, parent, false)
        return LoveSpotMultiViewHolder(view)
    }
}


class LoveSpotMultiEventsViewHolderBinder : NewsFeedItemBinder {
    private val tag = "LoveSpotMultiEventsViewHolderBinder"

    override fun viewHolderType(): Class<LoveSpotMultiViewHolder> {
        return LoveSpotMultiViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val loveSpotMultiEvents = response.loveSpotMultiEvents!!
        setLoveSpotMultiView(viewHolder as LoveSpotMultiViewHolder, response, loveSpotMultiEvents)
    }

    private fun setLoveSpotMultiView(
        viewHolder: LoveSpotMultiViewHolder,
        item: NewsFeedItemResponse,
        multiLoveSpot: LoveSpotMultiEventsResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, multiLoveSpot.loveSpot.id)

        val loveSpot: LoveSpotNewsFeedResponse = multiLoveSpot.loveSpot
        val lovers: List<LoverViewWithoutRelationDto> = multiLoveSpot.lovers
        val reviews: List<LoveSpotReviewNewsFeedResponse> = multiLoveSpot.reviews.take(2)
        val loves: List<LoveNewsFeedResponse> = multiLoveSpot.loves.take(2)
        val photos: List<LoveSpotPhotoNewsFeedResponse> = multiLoveSpot.photos.take(3)

        viewHolder.setTitle(multiLoveSpot, lovers, loveSpot, item)
        setLoveSpotViews(loveSpot.id, viewHolder)
        setReviews(reviews, viewHolder, lovers)
        setLoves(loves, viewHolder, lovers)
        setPhotos(photos, viewHolder, lovers, loveSpot)
    }

    private fun setReviews(
        reviews: List<LoveSpotReviewNewsFeedResponse>,
        viewHolder: LoveSpotMultiViewHolder,
        lovers: List<LoverViewWithoutRelationDto>
    ) {
        reviews.forEachIndexed { index, response ->
            Log.i(tag, "Filling review #$index")
            val review = viewHolder.reviews[index]
            review.setTitle(
                lovers.firstOrNull { it.id == response.reviewerId && it.publicProfile },
                R.string.somebody_reviewed_spot_multi,
                R.string.public_actor_reviewed_spot
            )

            review.openLoverImage
            if (response.reviewText.isEmpty()) {
                review.reviewText.visibility = View.GONE
            } else {
                review.reviewText.visibility = View.VISIBLE
                review.reviewText.text = response.reviewText
            }
            LoveSpotUtils.setRating(response.reviewStars.toDouble(), review.ratingBar)
            LoveSpotUtils.setRisk(response.riskLevel.toDouble(), review.risk)
        }
        viewHolder.hideLastRows(reviews.size, viewHolder.reviews)
    }

    private fun setLoves(
        loves: List<LoveNewsFeedResponse>,
        viewHolder: LoveSpotMultiViewHolder,
        lovers: List<LoverViewWithoutRelationDto>
    ) {
        loves.forEachIndexed { index, response ->
            Log.i(tag, "Filling love #$index")
            val love = viewHolder.loves[index]
            love.setTitle(
                lovers.firstOrNull { it.id == response.loverId && it.publicProfile },
                R.string.somebody_made_love_at,
                R.string.public_actor_made_love_at
            )
        }
        viewHolder.hideLastRows(loves.size, viewHolder.loves)
    }

    private fun setPhotos(
        photos: List<LoveSpotPhotoNewsFeedResponse>,
        viewHolder: LoveSpotMultiViewHolder,
        lovers: List<LoverViewWithoutRelationDto>,
        loveSpot: LoveSpotNewsFeedResponse
    ) {
        photos.forEachIndexed { index, response ->
            Log.i(tag, "Filling photo #$index")
            val photo = viewHolder.photos[index]
            photo.setTitle(
                lovers.firstOrNull { it.id == response.uploadedBy && it.publicProfile },
                R.string.somebody_uploaded_a_photo_to,
                R.string.public_actor_uploaded_a_photo_to
            )
            PhotoUtils.loadSimpleImage(
                photo.photo,
                loveSpot.type,
                response.url,
                viewHolder.itemView.width
            )
        }
        viewHolder.hideLastRows(photos.size, viewHolder.photos)
    }

}

