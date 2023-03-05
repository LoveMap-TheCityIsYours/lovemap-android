package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.api.newsfeed.PhotoLikeNewsFeedResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.PhotoLikeViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_PHOTO_LIKE
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PhotoLikeViewHolderCreator : NewsFeedItemCreator<PhotoLikeViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_PHOTO_LIKE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoLikeViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_feed_item_photo_like, parent, false)
        return PhotoLikeViewHolder(view)
    }
}


class PhotoLikeViewHolderBinder : NewsFeedItemBinder {
    private val loveSpotService = AppContext.INSTANCE.loveSpotService

    override fun viewHolderType(): Class<PhotoLikeViewHolder> {
        return PhotoLikeViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val photoLike = response.photoLike!!
        setPhotoLikeView(viewHolder as PhotoLikeViewHolder, response, photoLike)
    }

    private fun setPhotoLikeView(
        viewHolder: PhotoLikeViewHolder,
        item: NewsFeedItemResponse,
        photoLike: PhotoLikeNewsFeedResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, photoLike.loveSpotId)
        val unknownActorText = if (photoLike.likeOrDislike > 0) {
            R.string.somebody_liked_photo_at
        } else {
            R.string.somebody_disliked_photo_at
        }
        val publicActorText = if (photoLike.likeOrDislike > 0) {
            R.string.public_actor_liked_photo_at
        } else {
            R.string.public_actor_disliked_photo_at
        }
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = unknownActorText,
            publicActorText = publicActorText,
            happenedAt = item.happenedAt,
            country = item.country
        )

        MainScope().launch {
            loveSpotService.findLocallyOrFetch(photoLike.loveSpotId)
                ?.let { loveSpot ->
                    viewHolder.newsFeedLoveSpotName.text = loveSpot.name
                    PhotoUtils.loadSimpleImage(
                        viewHolder.photo,
                        loveSpot.type,
                        photoLike.url,
                        viewHolder.itemView.width
                    )
                }

        }
    }
}

