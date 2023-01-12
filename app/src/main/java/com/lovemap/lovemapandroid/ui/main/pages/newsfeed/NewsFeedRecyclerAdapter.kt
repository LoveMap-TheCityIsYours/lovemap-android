package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.*
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemType.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
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
    private val loveSpotReviewService = appContext.loveSpotReviewService

    var lastPosition: Int = -1

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOVE_SPOT_PHOTO = 1
        private const val VIEW_TYPE_PHOTO_LIKE = 2
        private const val VIEW_TYPE_LOVE_SPOT = 3
        private const val VIEW_TYPE_WISHLIST = 4
        private const val VIEW_TYPE_LOVE_SPOT_REVIEW = 5
        private const val VIEW_TYPE_LOVE = 6
        private const val VIEW_TYPE_LOVER = 7
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
            VIEW_TYPE_LOVER -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_lover, parent, false)
                LoverViewHolder(view)
            }
            VIEW_TYPE_LOVE_SPOT -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_lovespot, parent, false)
                LoveSpotViewHolder(view)
            }
            VIEW_TYPE_WISHLIST -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_wishlist, parent, false)
                LoveSpotViewHolder(view)
            }
            VIEW_TYPE_LOVE -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_love, parent, false)
                LoveSpotViewHolder(view)
            }
            VIEW_TYPE_LOVE_SPOT_REVIEW -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_lovespot_review, parent, false)
                LoveSpotReviewViewHolder(view)
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
        setItemAnimation(viewHolder, position)
        if (viewHolder is LoverViewHolder) {
            newsFeedItems[position]?.let { item ->
                val lover = item.lover!!
                viewHolder.newsFeedText.text = context.getString(R.string.new_lover_joined)
                viewHolder.newsFeedHappenedAt.text = instantOfApiString(item.happenedAt).toFormattedString()
            }
        } else if (viewHolder is LoveSpotPhotoViewHolder) {
            newsFeedItems[position]?.let { item ->
                val loveSpotPhoto = item.loveSpotPhoto!!
                setLoveSpotPhotoView(viewHolder, item, loveSpotPhoto)
            }
        } else if (viewHolder is PhotoLikeViewHolder) {
            newsFeedItems[position]?.let { item ->
                val photoLike = item.photoLike!!
                setPhotoLikeView(viewHolder, item, photoLike)
            }
        } else if (viewHolder is LoveSpotViewHolder) {
            newsFeedItems[position]?.let { item ->
                when (item.type) {
                    LOVE_SPOT -> {
                        val loveSpot = item.loveSpot!!
                        setLoveSpotView(viewHolder, item, loveSpot)
                    }
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
        } else if (viewHolder is LoveSpotReviewViewHolder) {
            newsFeedItems[position]?.let { item ->
                val loveSpotReview = item.loveSpotReview!!
                setLoveSpotReviewView(viewHolder, item, loveSpotReview)
            }

        } else if (viewHolder is LoadingViewHolder) {

        }
    }

    private fun setPhotoLikeView(
        viewHolder: PhotoLikeViewHolder,
        item: NewsFeedItemResponse,
        photoLike: PhotoLikeNewsFeedResponse
    ) {
        setOnClickListener(viewHolder, photoLike.loveSpotId)
        if (photoLike.likeOrDislike > 0) {
            viewHolder.newsFeedText.text = context.getString(R.string.somebody_liked_photo_at)
        } else {
            viewHolder.newsFeedText.text = context.getString(R.string.somebody_disliked_photo_at)
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

    private fun setLoveSpotPhotoView(
        viewHolder: LoveSpotPhotoViewHolder,
        item: NewsFeedItemResponse,
        loveSpotPhoto: LoveSpotPhotoNewsFeedResponse
    ) {
        setOnClickListener(viewHolder, loveSpotPhoto.loveSpotId)
        viewHolder.newsFeedText.text = context.getString(R.string.somebody_uploaded_a_photo_to)
        viewHolder.newsFeedHappenedAt.text = instantOfApiString(item.happenedAt).toFormattedString()

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

    private fun setLoveSpotView(
        viewHolder: LoveSpotViewHolder,
        item: NewsFeedItemResponse,
        loveSpot: LoveSpotNewsFeedResponse
    ) {
        setOnClickListener(viewHolder, loveSpot.id)
        viewHolder.newsFeedText.text = context.getString(R.string.somebody_added_a_new_lovespot)
        viewHolder.newsFeedHappenedAt.text = instantOfApiString(item.happenedAt).toFormattedString()
        viewHolder.newsFeedLoveSpotName.text = loveSpot.name
        LoveSpotUtils.setTypeImage(loveSpot.type, viewHolder.newsFeedSpotTypeImage)
        LoveSpotUtils.setType(loveSpot.type, viewHolder.newsFeedSpotType)
        setLoveSpotViews(loveSpot.id, viewHolder)
    }

    private fun setLoveSpotViews(
        loveSpotId: Long,
        viewHolder: LoveSpotViewHolder
    ) {
        MainScope().launch {
            loveSpotService.findLocallyOrFetch(loveSpotId)
                ?.let { loveSpot ->
                    viewHolder.newsFeedLoveSpotName.text = loveSpot.name
                    LoveSpotUtils.setTypeImage(loveSpot.type, viewHolder.newsFeedSpotTypeImage)
                    LoveSpotUtils.setType(loveSpot.type, viewHolder.newsFeedSpotType)
                    LoveSpotUtils.setRating(loveSpot.averageRating, viewHolder.newsFeedSpotRating)
                    LoveSpotUtils.setAvailability(
                        loveSpot.availability,
                        viewHolder.newsFeedSpotAvailability
                    )
                    LoveSpotUtils.setRisk(loveSpot.averageDanger, viewHolder.newsFeedSpotRisk)
                    LoveSpotUtils.setDistance(loveSpot, viewHolder.newsFeedSpotDistance)
                }
        }
    }

    private fun setWishlistView(
        viewHolder: LoveSpotViewHolder,
        item: NewsFeedItemResponse,
        wishlist: WishlistNewsFeedResponse,
    ) {
        setOnClickListener(viewHolder, wishlist.loveSpotId)
        viewHolder.newsFeedText.text = context.getString(R.string.somebody_wishlisted_spot)
        viewHolder.newsFeedHappenedAt.text = instantOfApiString(item.happenedAt).toFormattedString()
        setLoveSpotViews(wishlist.loveSpotId, viewHolder)
    }

    private fun setLoveView(
        viewHolder: LoveSpotViewHolder,
        item: NewsFeedItemResponse,
        love: LoveNewsFeedResponse,
    ) {
        setOnClickListener(viewHolder, love.loveSpotId)
        viewHolder.newsFeedText.text = context.getString(R.string.somebody_made_love_at)
        viewHolder.newsFeedHappenedAt.text = instantOfApiString(item.happenedAt).toFormattedString()
        setLoveSpotViews(love.loveSpotId, viewHolder)
    }

    private fun setLoveSpotReviewView(
        viewHolder: LoveSpotReviewViewHolder,
        item: NewsFeedItemResponse,
        loveSpotReview: LoveSpotReviewNewsFeedResponse
    ) {
        setOnClickListener(viewHolder, loveSpotReview.loveSpotId)
        viewHolder.newsFeedText.text = context.getString(R.string.somebody_reviewed_spot)
        viewHolder.newsFeedHappenedAt.text = instantOfApiString(item.happenedAt).toFormattedString()
        setLoveSpotReviewViews(loveSpotReview, viewHolder)
    }

    private fun setLoveSpotReviewViews(
        loveSpotReview: LoveSpotReviewNewsFeedResponse,
        viewHolder: LoveSpotReviewViewHolder
    ) {
        LoveSpotUtils.setRating(
            loveSpotReview.reviewStars.toDouble(),
            viewHolder.newsFeedSpotRating
        )
        LoveSpotUtils.setRisk(loveSpotReview.riskLevel.toDouble(), viewHolder.newsFeedSpotRisk)
        viewHolder.newsFeedSpotReviewText.text = loveSpotReview.reviewText
        MainScope().launch {
            loveSpotReviewService.findLocallyOrFetch(loveSpotReview.id)?.let { review ->
                LoveSpotUtils.setRating(
                    review.reviewStars.toDouble(),
                    viewHolder.newsFeedSpotRating
                )
                LoveSpotUtils.setRisk(review.riskLevel.toDouble(), viewHolder.newsFeedSpotRisk)
                viewHolder.newsFeedSpotReviewText.text = review.reviewText
            }
            loveSpotService.findLocallyOrFetch(loveSpotReview.loveSpotId)?.let { loveSpot ->
                viewHolder.newsFeedLoveSpotName.text = loveSpot.name
                LoveSpotUtils.setTypeImage(loveSpot.type, viewHolder.newsFeedSpotTypeImage)
                LoveSpotUtils.setDistance(loveSpot, viewHolder.newsFeedSpotDistance)
            }
        }
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

    private fun setOnClickListener(
        viewHolder: RecyclerView.ViewHolder,
        loveSpotId: Long
    ) {
        viewHolder.itemView.setOnClickListener {
            appContext.selectedLoveSpotId = loveSpotId
            MapContext.selectedMarker = null
            context.startActivity(
                Intent(context, LoveSpotDetailsActivity::class.java)
            )
        }
    }

    override fun getItemCount(): Int {
        return newsFeedItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (newsFeedItems[position]?.type) {
            LOVE_SPOT_PHOTO -> VIEW_TYPE_LOVE_SPOT_PHOTO
            LOVE_SPOT_PHOTO_LIKE -> VIEW_TYPE_PHOTO_LIKE
            LOVE_SPOT -> VIEW_TYPE_LOVE_SPOT
            WISHLIST_ITEM -> VIEW_TYPE_WISHLIST
            LOVE_SPOT_REVIEW -> VIEW_TYPE_LOVE_SPOT_REVIEW
            LOVE -> VIEW_TYPE_LOVE
            LOVER -> VIEW_TYPE_LOVER
            null -> VIEW_TYPE_LOADING
        }
    }

    inner class LoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
        val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
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

    inner class LoveSpotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
        val newsFeedSpotTypeImage: ImageView = itemView.findViewById(R.id.newsFeedSpotTypeImage)
        val newsFeedSpotRating: RatingBar = itemView.findViewById(R.id.newsFeedSpotRating)
        val newsFeedSpotType: TextView = itemView.findViewById(R.id.newsFeedSpotType)
        val newsFeedSpotAvailability: TextView =
            itemView.findViewById(R.id.newsFeedSpotAvailability)
        val newsFeedSpotRisk: TextView = itemView.findViewById(R.id.newsFeedSpotRisk)
        val newsFeedSpotDistance: TextView = itemView.findViewById(R.id.newsFeedSpotDistance)
    }

    inner class LoveSpotReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
        val newsFeedSpotTypeImage: ImageView = itemView.findViewById(R.id.newsFeedSpotTypeImage)
        val newsFeedSpotRating: RatingBar = itemView.findViewById(R.id.newsFeedSpotRating)
        val newsFeedSpotRisk: TextView = itemView.findViewById(R.id.newsFeedSpotRisk)
        val newsFeedSpotDistance: TextView = itemView.findViewById(R.id.newsFeedSpotDistance)
        val newsFeedSpotReviewText: TextView = itemView.findViewById(R.id.newsFeedSpotReviewText)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.itemLoadingProgressBar)
        }
    }
}
