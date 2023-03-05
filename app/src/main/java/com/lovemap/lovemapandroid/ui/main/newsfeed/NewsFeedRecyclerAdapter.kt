package com.lovemap.lovemapandroid.ui.main.newsfeed

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.newsfeed.*
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemType.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers.*
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation

const val VIEW_TYPE_LOVE_SPOT_PHOTO = 1
const val VIEW_TYPE_PHOTO_LIKE = 2
const val VIEW_TYPE_LOVE_SPOT = 3
const val VIEW_TYPE_WISHLIST = 4
const val VIEW_TYPE_LOVE_SPOT_REVIEW = 5
const val VIEW_TYPE_LOVE = 6
const val VIEW_TYPE_LOVER = 7
const val VIEW_TYPE_MULTI_LOVER = 8
const val VIEW_TYPE_PRIVATE_LOVERS = 9
const val VIEW_TYPE_LOVE_SPOT_MULTI_EVENTS = 10
const val VIEW_TYPE_UNSUPPORTED = 19
const val VIEW_TYPE_LOADING = 20

class NewsFeedRecyclerAdapter(
    private val context: Context,
    private val newsFeedItems: MutableList<NewsFeedItemResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val tag = "NewsFeedRecyclerAdapter"

    private val creators = HashMap<Int, NewsFeedItemCreator<BaseViewHolder>>()
    private val binders = HashMap<Class<out RecyclerView.ViewHolder>, NewsFeedItemBinder>()

    init {
        with(LoveSpotPhotoViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(PhotoLikeViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(PublicLoverViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(PrivateLoversViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(MultiLoverViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(LoveSpotViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(LoveSpotReviewViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }
        with(LoveSpotMultiEventsViewHolderCreator()) {
            creators[this.viewTypeId()] = this
        }



        with(LoveSpotPhotoViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(PhotoLikeViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(PublicLoverViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(PrivateLoversViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(MultiLoverViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(LoveSpotViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(LoveSpotReviewViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }
        with(LoveSpotMultiEventsViewHolderBinder()) {
            binders[this.viewHolderType()] = this
        }

    }

    var lastPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return creators[viewType]?.onCreateViewHolder(parent, viewType)
            ?: when (viewType) {
                VIEW_TYPE_WISHLIST -> {
                    val view: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.news_feed_item_wishlist, parent, false)
                    BaseLoveSpotViewHolder(view)
                }
                VIEW_TYPE_LOVE -> {
                    val view: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.news_feed_item_love, parent, false)
                    BaseLoveSpotViewHolder(view)
                }
                VIEW_TYPE_LOADING -> {
                    val view: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_loading, parent, false)
                    LoadingViewHolder(view)
                }
                else -> {
                    val view: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.news_feed_item_unsupported, parent, false)
                    UnsupportedViewHolder(view)
                }
            }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        setItemAnimation(viewHolder, position)
        binders[viewHolder::class.java]
            ?.let {
                Log.d(tag, "viewHolder::class.java: ${viewHolder::class.java}")
                Log.d(tag, "binders: $binders")
                Log.d(tag, "newsFeedItems size: ${newsFeedItems.size}")
                Log.d(tag, "Position: $position")
                Log.d(tag, "Response: ${newsFeedItems[position]}")
                it.onBindViewHolder(newsFeedItems[position], viewHolder)
            }
            ?: run {
                 if (viewHolder is BaseLoveSpotViewHolder) {
                    newsFeedItems[position].let { item ->
                        when (item.type) {
                            WISHLIST_ITEM -> {
                                val wishlist = item.wishlist!!
                                setWishlistView(viewHolder, item, wishlist)
                            }
                            LOVE -> {
                                val love = item.love!!
                                setLoveView(viewHolder, item, love)
                            }
                            else -> {
                                // impossible
                            }
                        }

                    }
                } else if (viewHolder is LoadingViewHolder) {

                }
                null
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
                    context.getString(R.string.public_actor_with_partner_made_love_at),
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
                    context.getString(R.string.public_actor_made_love_at),
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


    private fun setItemAnimation(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (viewHolder !is LoadingViewHolder) {
            setListItemAnimation(viewHolder.itemView, position, lastPosition)
            if (position > lastPosition) {
                lastPosition = position
            }
        }
    }

    override fun getItemCount(): Int {
        return newsFeedItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return runCatching {
            when (newsFeedItems[position].type) {
                LOVE_SPOT_PHOTO -> VIEW_TYPE_LOVE_SPOT_PHOTO
                LOVE_SPOT_PHOTO_LIKE -> VIEW_TYPE_PHOTO_LIKE
                LOVE_SPOT -> VIEW_TYPE_LOVE_SPOT
                WISHLIST_ITEM -> VIEW_TYPE_WISHLIST
                LOVE_SPOT_REVIEW -> VIEW_TYPE_LOVE_SPOT_REVIEW
                LOVE -> VIEW_TYPE_LOVE
                LOVER -> VIEW_TYPE_LOVER
                MULTI_LOVER -> VIEW_TYPE_MULTI_LOVER
                LOADING -> VIEW_TYPE_LOADING
                PRIVATE_LOVERS -> VIEW_TYPE_PRIVATE_LOVERS
                LOVE_SPOT_MULTI_EVENTS -> VIEW_TYPE_LOVE_SPOT_MULTI_EVENTS
                else -> VIEW_TYPE_UNSUPPORTED
            }
        }.onFailure { e ->
            Log.e(tag, "getItemViewType shitted itself", e)
        }.getOrNull() ?: VIEW_TYPE_UNSUPPORTED
    }

}
