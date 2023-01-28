package com.lovemap.lovemapandroid.ui.main.newsfeed

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.newsfeed.*
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemType.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.lover.ViewOtherLoverActivity
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation
import com.lovemap.lovemapandroid.utils.EmojiUtils
import com.lovemap.lovemapandroid.utils.instantOfApiString
import com.lovemap.lovemapandroid.utils.toFormattedDateTime
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NewsFeedRecyclerAdapter(
    private val context: Context,
    private val newsFeedItems: MutableList<NewsFeedItemResponse>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val tag = "NewsFeedRecyclerAdapter"
    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService
    private val metadataStore = appContext.metadataStore
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
        private const val VIEW_TYPE_MULTI_LOVER = 8
        private const val VIEW_TYPE_PRIVATE_LOVERS = 9
        private const val VIEW_TYPE_UNSUPPORTED = 19
        private const val VIEW_TYPE_LOADING = 20
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
            VIEW_TYPE_PRIVATE_LOVERS -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_private_lovers, parent, false)
                PrivateLoversViewHolder(view)
            }
            VIEW_TYPE_MULTI_LOVER -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_multi_lover, parent, false)
                MultiLoverViewHolder(view)
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
                BaseLoveSpotViewHolder(view)
            }
            VIEW_TYPE_LOVE -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_love, parent, false)
                BaseLoveSpotViewHolder(view)
            }
            VIEW_TYPE_LOVE_SPOT_REVIEW -> {
                val view: View =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_feed_item_lovespot_review, parent, false)
                LoveSpotReviewViewHolder(view)
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
        if (viewHolder is LoverViewHolder) {
            newsFeedItems[position].let { item ->
                val lover = item.lover!!
                setLoverView(viewHolder, item, lover)
            }
        } else if (viewHolder is PrivateLoversViewHolder) {
            newsFeedItems[position].let { item ->
                val privateLovers = item.privateLovers!!
                runCatching {
                    setPrivateLoversView(
                        viewHolder,
                        item,
                        privateLovers
                    )
                }.onFailure { e ->
                    Log.e(tag, "setPrivateLoversView shitted itself", e)
                }
            }
        } else if (viewHolder is MultiLoverViewHolder) {
            newsFeedItems[position].let { item ->
                val multiLover = item.multiLover!!
                runCatching {
                    setMultiLoverView(
                        viewHolder,
                        item,
                        multiLover.copy(lovers = multiLover.lovers.take(viewHolder.maxMultiLovers))
                    )
                }.onFailure { e ->
                    Log.e(tag, "setMultiLoverView shitted itself", e)
                }
            }
        } else if (viewHolder is LoveSpotPhotoViewHolder) {
            newsFeedItems[position].let { item ->
                val loveSpotPhoto = item.loveSpotPhoto!!
                setLoveSpotPhotoView(viewHolder, item, loveSpotPhoto)
            }
        } else if (viewHolder is PhotoLikeViewHolder) {
            newsFeedItems[position].let { item ->
                val photoLike = item.photoLike!!
                setPhotoLikeView(viewHolder, item, photoLike)
            }
        }   else if (viewHolder is LoveSpotViewHolder) {
            newsFeedItems[position].let { item ->
                val loveSpot = item.loveSpot!!
                setLoveSpotView(viewHolder, item, loveSpot)
            }
        } else if (viewHolder is BaseLoveSpotViewHolder) {
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
        } else if (viewHolder is LoveSpotReviewViewHolder) {
            newsFeedItems[position].let { item ->
                val loveSpotReview = item.loveSpotReview!!
                setLoveSpotReviewView(viewHolder, item, loveSpotReview)
            }

        } else if (viewHolder is LoadingViewHolder) {

        }
    }

    private fun setLoverView(
        viewHolder: LoverViewHolder,
        item: NewsFeedItemResponse,
        lover: LoverNewsFeedResponse
    ) {
        viewHolder.itemView.setOnClickListener {
            openOtherLoverView(lover.id)
        }
        viewHolder.setTexts(
            publicLover = null,
            unknownActorText = R.string.new_lover_joined,
            publicActorText = R.string.new_lover_joined,
            happenedAt = item.happenedAt,
            country = item.country
        )
        viewHolder.newsFeedLoverName.text = lover.displayName
        viewHolder.newsFeedLoverPoints.text = lover.points.toString()
        ProfileUtils.setRanks(lover.points, viewHolder.newsFeedLoverRank)
        MainScope().launch {
            if (lover.id == appContext.userId) {
                viewHolder.newsFeedLoverName.text = metadataStore.getLover().displayName
            }
            loverService.getOtherByIdWithoutRelation(lover.id)?.let {
                LoverService.otherLoverId = it.id
                if (lover.id != appContext.userId) {
                    viewHolder.newsFeedLoverName.text = it.displayName
                }
                viewHolder.newsFeedLoverPoints.text = it.points.toString()
                ProfileUtils.setRanks(it.points, viewHolder.newsFeedLoverRank)
            }
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

    private fun setMultiLoverView(
        viewHolder: MultiLoverViewHolder,
        item: NewsFeedItemResponse,
        multiLover: MultiLoverNewsFeedResponse
    ) {
        hideLastRows(viewHolder, multiLover)
        multiLover.lovers.forEachIndexed { index, lover ->
            viewHolder.loverNames[index].text = lover.displayName
            viewHolder.layouts[index].visibility = View.VISIBLE
            viewHolder.layouts[index].setOnClickListener {
                openOtherLoverView(lover.id)
            }
        }
        viewHolder.setTexts(
            publicLover = null,
            unknownActorText = R.string.new_multi_lover_joined,
            publicActorText = R.string.new_lover_joined,
            happenedAt = item.happenedAt,
            country = item.country
        )
        MainScope().launch {
            multiLover.lovers.forEachIndexed { index, lover ->
                if (lover.id == appContext.userId) {
                    viewHolder.loverNames[index].text = metadataStore.getLover().displayName
                }
                loverService.getOtherByIdWithoutRelation(lover.id)?.let {
                    LoverService.otherLoverId = it.id
                    if (lover.id != appContext.userId) {
                        viewHolder.loverNames[index].text = it.displayName
                    }
                }
            }
        }
    }

    private fun hideLastRows(
        viewHolder: MultiLoverViewHolder,
        multiLover: MultiLoverNewsFeedResponse
    ) {
        val hideLastN = viewHolder.maxMultiLovers - multiLover.lovers.size

        viewHolder.layouts.forEachIndexed { index, relativeLayout ->
            if (hideLastN - index <= hideLastN) {
                relativeLayout.visibility = View.GONE
            }
        }
    }

    private fun openOtherLoverView(loverId: Long) {
        LoverService.otherLoverId = loverId
        context.startActivity(
            Intent(context, ViewOtherLoverActivity::class.java)
        )
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

    private fun setLoveSpotView(
        viewHolder: LoveSpotViewHolder,
        item: NewsFeedItemResponse,
        loveSpot: LoveSpotNewsFeedResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, loveSpot.id)
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = R.string.somebody_added_a_new_lovespot,
            publicActorText = R.string.public_actor_added_a_new_lovespot,
            happenedAt = item.happenedAt,
            country = item.country
        )

        viewHolder.newsFeedLoveSpotName.text = loveSpot.name
        viewHolder.newsFeedSpotDescription.text = loveSpot.description
        LoveSpotUtils.setTypeImage(loveSpot.type, viewHolder.newsFeedSpotTypeImage)
        LoveSpotUtils.setType(loveSpot.type, viewHolder.newsFeedSpotType)
        setLoveSpotViews(loveSpot.id, viewHolder)
    }

    private fun setLoveSpotViews(
        loveSpotId: Long,
        viewHolder: BaseLoveSpotViewHolder
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
                    if (viewHolder is LoveSpotViewHolder) {
                        viewHolder.newsFeedSpotDescription.text = loveSpot.description
                    }
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

    private fun setLoveSpotReviewView(
        viewHolder: LoveSpotReviewViewHolder,
        item: NewsFeedItemResponse,
        loveSpotReview: LoveSpotReviewNewsFeedResponse
    ) {
        setLoveSpotOnClickListener(viewHolder, loveSpotReview.loveSpotId)
        viewHolder.setTexts(
            publicLover = item.publicLover,
            unknownActorText = R.string.somebody_reviewed_spot,
            publicActorText = R.string.public_actor_reviewed_spot,
            happenedAt = item.happenedAt,
            country = item.country
        )
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
        if (loveSpotReview.reviewText.isEmpty()) {
            viewHolder.newsFeedSpotReviewText.visibility = View.GONE
        } else {
            viewHolder.newsFeedSpotReviewText.text = loveSpotReview.reviewText
            viewHolder.newsFeedSpotReviewText.visibility = View.VISIBLE
        }
        MainScope().launch {
            loveSpotReviewService.findLocallyOrFetch(loveSpotReview.id)?.let { review ->
                LoveSpotUtils.setRating(
                    review.reviewStars.toDouble(),
                    viewHolder.newsFeedSpotRating
                )
                LoveSpotUtils.setRisk(review.riskLevel.toDouble(), viewHolder.newsFeedSpotRisk)
                if (review.reviewText.isEmpty()) {
                    viewHolder.newsFeedSpotReviewText.visibility = View.GONE
                } else {
                    viewHolder.newsFeedSpotReviewText.text = review.reviewText
                    viewHolder.newsFeedSpotReviewText.visibility = View.VISIBLE
                }
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

    private fun setLoveSpotOnClickListener(
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
                else -> VIEW_TYPE_UNSUPPORTED
            }
        }.onFailure { e ->
            Log.e(tag, "getItemViewType shitted itself", e)
        }.getOrNull() ?: VIEW_TYPE_UNSUPPORTED
    }

    open inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
        val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
        val newsFeedCountry: TextView = itemView.findViewById(R.id.newsFeedCountry)
        val newsFeedTextLayout: RelativeLayout = itemView.findViewById(R.id.newsFeedTextLayout)
        val newsFeedOpenLoverImage: ImageView = itemView.findViewById(R.id.newsFeedOpenLoverImage)

        fun setTexts(
            publicLover: LoverViewWithoutRelationDto?,
            unknownActorText: Int,
            publicActorText: Int,
            happenedAt: String,
            country: String
        ) {
            val publicLoverText = publicLover?.let {
                String.format(
                    context.getString(publicActorText),
                    it.displayName
                )
            } ?: ""
            setTexts(publicLover, publicLoverText, unknownActorText, happenedAt, country)
        }

        fun setTexts(
            publicLover: LoverViewWithoutRelationDto?,
            publicLoverText: String,
            unknownActorText: Int,
            happenedAt: String,
            country: String
        ) {
            publicLover?.let {
                newsFeedText.text = publicLoverText
                newsFeedText.textSize = 16f
                newsFeedTextLayout.isClickable = true
                newsFeedTextLayout.focusable = View.FOCUSABLE
                newsFeedOpenLoverImage.visibility = View.VISIBLE
                newsFeedTextLayout.setOnClickListener {
                    openOtherLoverView(publicLover.id)
                }
            } ?: run {
                newsFeedText.text = context.getString(unknownActorText)
                newsFeedText.textSize = 14f
                newsFeedOpenLoverImage.visibility = View.GONE
                newsFeedTextLayout.isClickable = false
                newsFeedTextLayout.focusable = View.NOT_FOCUSABLE
            }
            newsFeedHappenedAt.text = instantOfApiString(happenedAt).toFormattedDateTime()
            val countryText = if (country.equals(appContext.countryForGlobal, true)) {
                "Global"
            } else {
                country
            }
            newsFeedCountry.text = "$countryText ${EmojiUtils.getFlagEmoji(country)}"
        }
    }

    inner class LoverViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val newsFeedLoverName: TextView = itemView.findViewById(R.id.newsFeedLoverName)
        val newsFeedLoverPoints: TextView = itemView.findViewById(R.id.newsFeedLoverPoints)
        val newsFeedLoverRank: TextView = itemView.findViewById(R.id.newsFeedLoverRank)
    }

    inner class PrivateLoversViewHolder(itemView: View): BaseViewHolder(itemView) {
        val newsFeedPrivateLoverNames: TextView = itemView.findViewById(R.id.newsFeedPrivateLoverNames)
    }

    inner class MultiLoverViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val maxMultiLovers = 6

        val newsFeedLoverLayout1: RelativeLayout = itemView.findViewById(R.id.newsFeedLoverLayout1)
        val newsFeedLoverLayout2: RelativeLayout = itemView.findViewById(R.id.newsFeedLoverLayout2)
        val newsFeedLoverLayout3: RelativeLayout = itemView.findViewById(R.id.newsFeedLoverLayout3)
        val newsFeedLoverLayout4: RelativeLayout = itemView.findViewById(R.id.newsFeedLoverLayout4)
        val newsFeedLoverLayout5: RelativeLayout = itemView.findViewById(R.id.newsFeedLoverLayout5)
        val newsFeedLoverLayout6: RelativeLayout = itemView.findViewById(R.id.newsFeedLoverLayout6)
        val layouts: List<RelativeLayout> = listOf(
            newsFeedLoverLayout1,
            newsFeedLoverLayout2,
            newsFeedLoverLayout3,
            newsFeedLoverLayout4,
            newsFeedLoverLayout5,
            newsFeedLoverLayout6
        )

        val newsFeedLoverName1: TextView = itemView.findViewById(R.id.newsFeedLoverName1)
        val newsFeedLoverName2: TextView = itemView.findViewById(R.id.newsFeedLoverName2)
        val newsFeedLoverName3: TextView = itemView.findViewById(R.id.newsFeedLoverName3)
        val newsFeedLoverName4: TextView = itemView.findViewById(R.id.newsFeedLoverName4)
        val newsFeedLoverName5: TextView = itemView.findViewById(R.id.newsFeedLoverName5)
        val newsFeedLoverName6: TextView = itemView.findViewById(R.id.newsFeedLoverName6)
        val loverNames: List<TextView> = listOf(
            newsFeedLoverName1,
            newsFeedLoverName2,
            newsFeedLoverName3,
            newsFeedLoverName4,
            newsFeedLoverName5,
            newsFeedLoverName6
        )
    }

    inner class LoveSpotPhotoViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoItemPhoto)
    }

    inner class PhotoLikeViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoLikePhoto)
    }

    open inner class BaseLoveSpotViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val newsFeedSpotTypeImage: ImageView = itemView.findViewById(R.id.newsFeedSpotTypeImage)
        val newsFeedSpotRating: RatingBar = itemView.findViewById(R.id.newsFeedSpotRating)
        val newsFeedSpotType: TextView = itemView.findViewById(R.id.newsFeedSpotType)
        val newsFeedSpotAvailability: TextView =
            itemView.findViewById(R.id.newsFeedSpotAvailability)
        val newsFeedSpotRisk: TextView = itemView.findViewById(R.id.newsFeedSpotRisk)
        val newsFeedSpotDistance: TextView = itemView.findViewById(R.id.newsFeedSpotDistance)
    }

    inner class LoveSpotViewHolder(itemView: View): BaseLoveSpotViewHolder(itemView) {
        val newsFeedSpotDescription: TextView = itemView.findViewById(R.id.newsFeedSpotDescription)
    }

    inner class LoveSpotReviewViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
        val newsFeedSpotTypeImage: ImageView = itemView.findViewById(R.id.newsFeedSpotTypeImage)
        val newsFeedSpotRating: RatingBar = itemView.findViewById(R.id.newsFeedSpotRating)
        val newsFeedSpotRisk: TextView = itemView.findViewById(R.id.newsFeedSpotRisk)
        val newsFeedSpotDistance: TextView = itemView.findViewById(R.id.newsFeedSpotDistance)
        val newsFeedSpotReviewText: TextView = itemView.findViewById(R.id.newsFeedSpotReviewText)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.itemLoadingProgressBar)
        }
    }

    inner class UnsupportedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsFeedItemUnsupported: TextView

        init {
            newsFeedItemUnsupported = itemView.findViewById(R.id.newsFeedItemUnsupported)
        }
    }
}
