package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation

class NewsFeedRecyclerAdapter(
    private val newsFeedItems: MutableList<NewsFeedItemResponse?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastPosition: Int = -1

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.news_feed_item, parent, false)
            ItemViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
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
        } else if (viewHolder is LoadingViewHolder) {

        }
    }

    override fun getItemCount(): Int {
        return newsFeedItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (newsFeedItems[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedItemType: TextView = itemView.findViewById(R.id.newsFeedItemType)
        val newsFeedItemHappenedAt: TextView = itemView.findViewById(R.id.newsFeedItemHappenedAt)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.itemLoadingProgressBar)
        }
    }
}
