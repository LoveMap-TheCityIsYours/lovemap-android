package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.api.newsfeed.PrivateLoversNewsFeedResponse
import com.lovemap.lovemapandroid.ui.main.newsfeed.PrivateLoversViewHolder
import com.lovemap.lovemapandroid.ui.main.newsfeed.VIEW_TYPE_PRIVATE_LOVERS

class PrivateLoversViewHolderCreator : NewsFeedItemCreator<PrivateLoversViewHolder> {

    override fun viewTypeId(): Int {
        return VIEW_TYPE_PRIVATE_LOVERS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateLoversViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_feed_item_private_lovers, parent, false)
        return PrivateLoversViewHolder(view)
    }
}


class PrivateLoversViewHolderBinder : NewsFeedItemBinder {
    override fun viewHolderType(): Class<PrivateLoversViewHolder> {
        return PrivateLoversViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        val privateLovers = response.privateLovers!!
        runCatching {
            setPrivateLoversView(
                viewHolder as PrivateLoversViewHolder,
                response,
                privateLovers
            )
        }.onFailure { e ->
            Log.e("PrivateLoversViewHolderBinder", "setPrivateLoversView shitted itself", e)
        }
    }

    private fun setPrivateLoversView(
        viewHolder: PrivateLoversViewHolder,
        item: NewsFeedItemResponse,
        privateLovers: PrivateLoversNewsFeedResponse
    ) {
        viewHolder.setTexts(
            publicLover = null,
            unknownActorText = R.string.new_private_lovers_joined,
            publicActorText = R.string.new_lover_joined,
            happenedAt = item.happenedAt,
            country = item.country
        )

        viewHolder.newsFeedPrivateLoverNames.text =
            privateLovers.lovers.joinToString(",    ") { it.displayName }
    }

}

