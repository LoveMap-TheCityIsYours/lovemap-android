package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.LoveSpotPhotoNewsFeedResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.LoveSpotPhotoViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_LOVE_SPOT_PHOTO
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotPhotoViewHolderCreator : NewsFeedItemCreator<LoveSpotPhotoViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_LOVE_SPOT_PHOTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoveSpotPhotoViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_feed_item_lovespot_photo, parent, false)
        return LoveSpotPhotoViewHolder(view)
    }
}


class LoveSpotPhotoViewHolderBinder : NewsFeedItemBinder {
    private val loveSpotService = AppContext.INSTANCE.loveSpotService

    override fun viewHolderType(): Class<LoveSpotPhotoViewHolder> {
        return LoveSpotPhotoViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val loveSpotPhoto = response.loveSpotPhoto!!
        setLoveSpotPhotoView(viewHolder as LoveSpotPhotoViewHolder, response, loveSpotPhoto)
    }

    private fun setLoveSpotPhotoView(
        viewHolder: LoveSpotPhotoViewHolder,
        item: NewsFeedItemResponse,
        loveSpotPhoto: LoveSpotPhotoNewsFeedResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, loveSpotPhoto.loveSpotId)
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = R.string.somebody_uploaded_a_photo_to,
            publicActorText = R.string.public_actor_uploaded_a_photo_to,
            happenedAt = item.happenedAt,
            country = item.country
        )

        MainScope().launch {
            loveSpotService.findLocallyOrFetch(loveSpotPhoto.loveSpotId)
                ?.let { loveSpot ->
                    Log.i("ImageViewWidth", "Is: " + viewHolder.photo.width)
                    viewHolder.newsFeedLoveSpotName.text = loveSpot.name
                    PhotoUtils.loadSimpleImage(
                        viewHolder.photo,
                        loveSpot.type,
                        loveSpotPhoto.url,
                        viewHolder.itemView.width
                    )
                }
        }
    }
}

