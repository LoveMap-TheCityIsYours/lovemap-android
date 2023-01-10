package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemType
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NewsFeedRecyclerAdapter(
    private val context: Context,
    private val newsFeedItems: MutableList<NewsFeedItemResponse?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val loveSpotService = AppContext.INSTANCE.loveSpotService

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
                val loveSpotPhoto = item.loveSpotPhoto
                viewHolder.newsFeedItemType.text = item.type.name
                viewHolder.newsFeedItemHappenedAt.text = item.happenedAtFormatted

                MainScope().launch {
                    loveSpotPhoto?.let {
                        loveSpotService.findLocallyOrFetch(loveSpotPhoto.loveSpotId)
                            ?.let { loveSpot ->
                                Glide.with(context)
                                    .load(loveSpotPhoto.url)
                                    .placeholder(LoveSpotUtils.getTypeImageResource(loveSpot.type))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(viewHolder.photo)
                            }
                    }
                }
            }
        } else if (viewHolder is PhotoLikeViewHolder) {
            setListItemAnimation(viewHolder.itemView, position, lastPosition)
            if (position > lastPosition) {
                lastPosition = position
            }
            newsFeedItems[position]?.let { item ->
                val photoLike = item.photoLike
                viewHolder.newsFeedItemType.text = item.type.name
                viewHolder.newsFeedItemHappenedAt.text = item.happenedAtFormatted

                MainScope().launch {
                    photoLike?.let {
                        loveSpotService.findLocallyOrFetch(photoLike.loveSpotId)
                            ?.let { loveSpot ->
                                Glide.with(context)
                                    .load(photoLike.url)
                                    .placeholder(LoveSpotUtils.getTypeImageResource(loveSpot.type))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(viewHolder.photo)
                            }
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
        val newsFeedItemType: TextView = itemView.findViewById(R.id.newsFeedItemType)
        val newsFeedItemHappenedAt: TextView = itemView.findViewById(R.id.newsFeedItemHappenedAt)
        val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoItemPhoto)
    }

    inner class PhotoLikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedItemType: TextView = itemView.findViewById(R.id.newsFeedItemType)
        val newsFeedItemHappenedAt: TextView = itemView.findViewById(R.id.newsFeedItemHappenedAt)
        val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoLikePhoto)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.itemLoadingProgressBar)
        }
    }
}
