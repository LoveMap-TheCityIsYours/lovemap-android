package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemType
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation
import com.lovemap.lovemapandroid.utils.instantOfApiString
import com.lovemap.lovemapandroid.utils.toFormattedString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NewsFeedRecyclerAdapter(
    private val context: Context,
    private val newsFeedItems: MutableList<NewsFeedItemResponse?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService

    var lastPosition: Int = -1

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOVE_SPOT_PHOTO = 1
        private const val VIEW_TYPE_PHOTO_LIKE = 2
        private const val VIEW_TYPE_LOADING = 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOVE_SPOT_PHOTO -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_lovespot_photo, parent, false)
                LoveSpotPhotoViewHolder(view)
            }
            VIEW_TYPE_PHOTO_LIKE -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_photo_like, parent, false)
                PhotoLikeViewHolder(view)
            }
            VIEW_TYPE_ITEM -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item, parent, false)
                ItemViewHolder(view)
            }
            else -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {
            setListItemAnimation(viewHolder.itemView, position, lastPosition)
            if (position > lastPosition) {
                lastPosition = position
            }
            newsFeedItems[position]?.let { item ->
                viewHolder.newsFeedItemType.text = item.type.name
                viewHolder.newsFeedItemHappenedAt.text = item.happenedAtFormatted
            }
        } else if (viewHolder is LoveSpotPhotoViewHolder) {
            setListItemAnimation(viewHolder.itemView, position, lastPosition)
            if (position > lastPosition) {
                lastPosition = position
            }
            newsFeedItems[position]?.let { item ->
                val loveSpotPhoto = item.loveSpotPhoto!!
                viewHolder.itemView.setOnClickListener {
                    appContext.selectedLoveSpotId = loveSpotPhoto.loveSpotId
                    MapContext.selectedMarker = null
                    context.startActivity(
                        Intent(context, LoveSpotDetailsActivity::class.java)
                    )
                }
                viewHolder.newsFeedText.text =
                    context.getString(R.string.somebody_uploaded_a_photo_to)
                viewHolder.newsFeedHappenedAt.text =
                    instantOfApiString(item.happenedAt).toFormattedString()

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
        } else if (viewHolder is PhotoLikeViewHolder) {
            setListItemAnimation(viewHolder.itemView, position, lastPosition)
            if (position > lastPosition) {
                lastPosition = position
            }
            newsFeedItems[position]?.let { item ->
                val photoLike = item.photoLike!!
                viewHolder.itemView.setOnClickListener {
                    appContext.selectedLoveSpotId = photoLike.loveSpotId
                    MapContext.selectedMarker = null
                    context.startActivity(
                        Intent(context, LoveSpotDetailsActivity::class.java)
                    )
                }
                if (photoLike.likeOrDislike > 0) {
                    viewHolder.newsFeedText.text =
                        context.getString(R.string.somebody_liked_photo_at)
                } else {
                    viewHolder.newsFeedText.text =
                        context.getString(R.string.somebody_disliked_photo_at)
                }

                viewHolder.newsFeedHappenedAt.text =
                    instantOfApiString(item.happenedAt).toFormattedString()

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
        } else if (viewHolder is LoadingViewHolder) {

        }
    }

    override fun getItemCount(): Int {
        return newsFeedItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (newsFeedItems[position]?.type) {
            NewsFeedItemType.LOVE_SPOT_PHOTO -> VIEW_TYPE_LOVE_SPOT_PHOTO
            NewsFeedItemType.LOVE_SPOT_PHOTO_LIKE -> VIEW_TYPE_PHOTO_LIKE
            null -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedItemType: TextView = itemView.findViewById(R.id.newsFeedItemType)
        val newsFeedItemHappenedAt: TextView = itemView.findViewById(R.id.newsFeedItemHappenedAt)
    }

    inner class LoveSpotPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
        val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoItemPhoto)
    }

    inner class PhotoLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
        val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoLikePhoto)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.itemLoadingProgressBar)
        }
    }
}
