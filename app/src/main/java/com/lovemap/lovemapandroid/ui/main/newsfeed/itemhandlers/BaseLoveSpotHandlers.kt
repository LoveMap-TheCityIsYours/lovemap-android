package com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.newsfeed.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.*
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WishlistViewHolderCreator : NewsFeedItemCreator<BaseLoveSpotViewHolder> {
    override fun viewTypeId(): Int {
        return VIEW_TYPE_WISHLIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLoveSpotViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_feed_item_wishlist, parent, false)
        return BaseLoveSpotViewHolder(view)
    }
}

class LoveViewHolderCreator : NewsFeedItemCreator<BaseLoveSpotViewHolder> {
    override fun viewTypeId(): Int {
        return VIEW_TYPE_LOVE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLoveSpotViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.news_feed_item_love, parent, false)
        return BaseLoveSpotViewHolder(view)
    }
}


class BaseLoveSpotViewHolderBinder : NewsFeedItemBinder {

    override fun viewHolderType(): Class<BaseLoveSpotViewHolder> {
        return BaseLoveSpotViewHolder::class.java
    }

    override fun onBindViewHolder(
        response: NewsFeedItemResponse,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        when (response.type) {
            NewsFeedItemType.WISHLIST_ITEM -> {
                val wishlist = response.wishlist!!
                setWishlistView(viewHolder as BaseLoveSpotViewHolder, response, wishlist)
            }
            NewsFeedItemType.LOVE -> {
                val love = response.love!!
                setLoveView(viewHolder as BaseLoveSpotViewHolder, response, love)
            }
            else -> {
                // impossible
            }
        }
    }

    private fun setWishlistView(
        viewHolder: BaseLoveSpotViewHolder,
        item: NewsFeedItemResponse,
        wishlist: WishlistNewsFeedResponse,
    ) {
        setLoveSpotOnClickListener(viewHolder, wishlist.loveSpotId)
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = R.string.somebody_wishlisted_spot,
            publicActorText = R.string.public_actor_wishlisted_spot,
            happenedAt = item.happenedAt,
            country = item.country
        )
        setLoveSpotViews(wishlist.loveSpotId, viewHolder)
    }

    private fun setLoveView(
        viewHolder: BaseLoveSpotViewHolder,
        item: NewsFeedItemResponse,
        love: LoveNewsFeedResponse,
    ) {
        setLoveSpotOnClickListener(viewHolder, love.loveSpotId)
        val publicLover: LoverViewWithoutRelationDto? = item.publicLover

        publicLover?.let { lover ->
            love.publicLoverPartner?.let { partner ->
                val publicLoverText = String.format(
                    viewHolder.context.getString(R.string.public_actor_with_partner_made_love_at),
                    lover.displayName,
                    partner.displayName
                )
                viewHolder.setTexts(
                    publicLover = lover,
                    publicLoverText = publicLoverText,
                    unknownActorText = R.string.somebody_made_love_at,
                    happenedAt = item.happenedAt,
                    country = item.country
                )
            } ?: run {
                val publicLoverText = String.format(
                    viewHolder.context.getString(R.string.public_actor_made_love_at),
                    lover.displayName
                )
                viewHolder.setTexts(
                    publicLover = lover,
                    publicLoverText = publicLoverText,
                    unknownActorText = R.string.somebody_made_love_at,
                    happenedAt = item.happenedAt,
                    country = item.country
                )
            }


        } ?: run {
            viewHolder.setTexts(
                publicLover = null,
                unknownActorText = R.string.somebody_made_love_at,
                publicActorText = R.string.public_actor_made_love_at,
                happenedAt = item.happenedAt,
                country = item.country
            )
        }
        setLoveSpotViews(love.loveSpotId, viewHolder)
    }
}

