package com.lovemap.lovemapandroid.ui.main.newsfeed

import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.newsfeed.LoveSpotMultiEventsResponse
import com.lovemap.lovemapandroid.api.newsfeed.LoveSpotNewsFeedResponse
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.main.newsfeed.itemhandlers.openOtherLoverView
import com.lovemap.lovemapandroid.utils.EmojiUtils
import com.lovemap.lovemapandroid.utils.instantOfApiString
import com.lovemap.lovemapandroid.utils.toFormattedDateTime

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val newsFeedText: TextView = itemView.findViewById(R.id.newsFeedText)
    val newsFeedHappenedAt: TextView = itemView.findViewById(R.id.newsFeedHappenedAt)
    val newsFeedCountry: TextView = itemView.findViewById(R.id.newsFeedCountry)
    val newsFeedTextLayout: RelativeLayout = itemView.findViewById(R.id.newsFeedTextLayout)
    val newsFeedOpenLoverImage: ImageView = itemView.findViewById(R.id.newsFeedOpenLoverImage)
    val context = itemView.context

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
                openOtherLoverView(publicLover.id, context)
            }
        } ?: run {
            newsFeedText.text = context.getString(unknownActorText)
            newsFeedText.textSize = 14f
            newsFeedOpenLoverImage.visibility = View.GONE
            newsFeedTextLayout.isClickable = false
            newsFeedTextLayout.focusable = View.NOT_FOCUSABLE
        }
        newsFeedHappenedAt.text = instantOfApiString(happenedAt).toFormattedDateTime()
        val countryText = if (country.equals(AppContext.INSTANCE.countryForGlobal, true)) {
            "Global"
        } else {
            country
        }
        newsFeedCountry.text = "$countryText ${EmojiUtils.getFlagEmoji(country)}"
    }
}

class LoverViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val newsFeedLoverName: TextView = itemView.findViewById(R.id.newsFeedLoverName)
    val newsFeedLoverPoints: TextView = itemView.findViewById(R.id.newsFeedLoverPoints)
    val newsFeedLoverRank: TextView = itemView.findViewById(R.id.newsFeedLoverRank)
}

class PrivateLoversViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val newsFeedPrivateLoverNames: TextView =
        itemView.findViewById(R.id.newsFeedPrivateLoverNames)
}

class MultiLoverViewHolder(itemView: View) : BaseViewHolder(itemView) {
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

class LoveSpotPhotoViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
    val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoItemPhoto)
}

class PhotoLikeViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
    val photo: ImageView = itemView.findViewById(R.id.newsFeedPhotoLikePhoto)
}

open class BaseLoveSpotViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val newsFeedLoveSpotName: TextView = itemView.findViewById(R.id.newsFeedLoveSpotName)
    val newsFeedSpotTypeImage: ImageView = itemView.findViewById(R.id.newsFeedSpotTypeImage)
    val newsFeedSpotRating: RatingBar = itemView.findViewById(R.id.newsFeedSpotRating)
    val newsFeedSpotType: TextView = itemView.findViewById(R.id.newsFeedSpotType)
    val newsFeedSpotAvailability: TextView =
        itemView.findViewById(R.id.newsFeedSpotAvailability)
    val newsFeedSpotRisk: TextView = itemView.findViewById(R.id.newsFeedSpotRisk)
    val newsFeedSpotDistance: TextView = itemView.findViewById(R.id.newsFeedSpotDistance)
}

class LoveSpotViewHolder(itemView: View) : BaseLoveSpotViewHolder(itemView) {
    val newsFeedSpotDescription: TextView = itemView.findViewById(R.id.newsFeedSpotDescription)
}

data class LoveSpotReviewViews(
    val newsFeedLoveSpotName: TextView,
    val newsFeedSpotTypeImage: ImageView,
    val newsFeedSpotRating: RatingBar,
    val newsFeedSpotRisk: TextView,
    val newsFeedSpotDistance: TextView,
    val newsFeedSpotReviewText: TextView
)

class LoveSpotReviewViewHolder(itemView: View) : BaseViewHolder(itemView) {
    val views = LoveSpotReviewViews(
        newsFeedLoveSpotName = itemView.findViewById(R.id.newsFeedLoveSpotName),
        newsFeedSpotTypeImage = itemView.findViewById(R.id.newsFeedSpotTypeImage),
        newsFeedSpotRating = itemView.findViewById(R.id.newsFeedSpotRating),
        newsFeedSpotRisk = itemView.findViewById(R.id.newsFeedSpotRisk),
        newsFeedSpotDistance = itemView.findViewById(R.id.newsFeedSpotDistance),
        newsFeedSpotReviewText = itemView.findViewById(R.id.newsFeedSpotReviewText),
    )
}

open class LoveSpotMultiBaseViews(
    val partLayout: LinearLayout,
    val titleLayout: RelativeLayout,
    val titleText: TextView,
    val openLoverImage: ImageView
) {
    val context = partLayout.context

    fun setTitle(
        publicLover: LoverViewWithoutRelationDto?,
        unknownActorText: Int,
        publicActorText: Int,
    ) {
        publicLover?.let {
            titleText.text = String.format(
                context.getString(publicActorText),
                it.displayName
            )
            titleText.textSize = 16f
            titleLayout.isClickable = true
            titleLayout.focusable = View.FOCUSABLE
            openLoverImage.visibility = View.VISIBLE
            titleLayout.setOnClickListener {
                openOtherLoverView(publicLover.id, context)
            }
        } ?: run {
            titleText.text = context.getString(unknownActorText)
            titleText.textSize = 14f
            openLoverImage.visibility = View.GONE
            titleLayout.isClickable = false
            titleLayout.focusable = View.NOT_FOCUSABLE
        }
    }
}

class LoveSpotMultiReviewViews(
    partLayout: LinearLayout,
    titleLayout: RelativeLayout,
    titleText: TextView,
    openLoverImage: ImageView,
    val ratingBar: RatingBar,
    val risk: TextView,
    val reviewText: TextView
) : LoveSpotMultiBaseViews(partLayout, titleLayout, titleText, openLoverImage)

class LoveSpotMultiLoveViews(
    partLayout: LinearLayout,
    titleLayout: RelativeLayout,
    titleText: TextView,
    openLoverImage: ImageView,
) : LoveSpotMultiBaseViews(partLayout, titleLayout, titleText, openLoverImage)

class LoveSpotMultiPhotoViews(
    partLayout: LinearLayout,
    titleLayout: RelativeLayout,
    titleText: TextView,
    openLoverImage: ImageView,
    val photo: ImageView,
) : LoveSpotMultiBaseViews(partLayout, titleLayout, titleText, openLoverImage)

class LoveSpotMultiViewHolder(itemView: View) : BaseLoveSpotViewHolder(itemView) {
    private val tag = "LoveSpotMultiViewHolder"

    val loveSpotDescription: TextView = itemView.findViewById(R.id.newsFeedSpotDescription)

    val review1 = LoveSpotMultiReviewViews(
        partLayout = itemView.findViewById(R.id.newsFeedReviewLayout1),
        titleLayout = itemView.findViewById(R.id.newsFeedReviewTextLayout1),
        titleText = itemView.findViewById(R.id.newsFeedReviewTitle1),
        openLoverImage = itemView.findViewById(R.id.newsFeedReviewOpenLoverImage1),
        ratingBar = itemView.findViewById(R.id.newsFeedSpotReviewRating1),
        risk = itemView.findViewById(R.id.newsFeedSpotReviewRisk1),
        reviewText = itemView.findViewById(R.id.newsFeedSpotReviewText1)
    )
    val review2 = LoveSpotMultiReviewViews(
        partLayout = itemView.findViewById(R.id.newsFeedReviewLayout2),
        titleLayout = itemView.findViewById(R.id.newsFeedReviewTextLayout2),
        titleText = itemView.findViewById(R.id.newsFeedReviewTitle2),
        openLoverImage = itemView.findViewById(R.id.newsFeedReviewOpenLoverImage2),
        ratingBar = itemView.findViewById(R.id.newsFeedSpotReviewRating2),
        risk = itemView.findViewById(R.id.newsFeedSpotReviewRisk2),
        reviewText = itemView.findViewById(R.id.newsFeedSpotReviewText2)
    )
    val reviews = listOf(review1, review2)


    val love1 = LoveSpotMultiLoveViews(
        partLayout = itemView.findViewById(R.id.newsFeedLoveLayout1),
        titleLayout = itemView.findViewById(R.id.newsFeedLoveTextLayout1),
        titleText = itemView.findViewById(R.id.newsFeedLoveText1),
        openLoverImage = itemView.findViewById(R.id.newsFeedLoveOpenLoverImage1),
    )
    val love2 = LoveSpotMultiLoveViews(
        partLayout = itemView.findViewById(R.id.newsFeedLoveLayout2),
        titleLayout = itemView.findViewById(R.id.newsFeedLoveTextLayout2),
        titleText = itemView.findViewById(R.id.newsFeedLoveText2),
        openLoverImage = itemView.findViewById(R.id.newsFeedLoveOpenLoverImage2),
    )
    val loves = listOf(love1, love2)


    val photo1 = LoveSpotMultiPhotoViews(
        partLayout = itemView.findViewById(R.id.newsFeedPhotoLayout1),
        titleLayout = itemView.findViewById(R.id.newsFeedPhotoTextLayout1),
        titleText = itemView.findViewById(R.id.newsFeedPhotoText1),
        openLoverImage = itemView.findViewById(R.id.newsFeedPhotoOpenLoverImage1),
        photo = itemView.findViewById(R.id.newsFeedPhotoItemPhoto1)
    )
    val photo2 = LoveSpotMultiPhotoViews(
        partLayout = itemView.findViewById(R.id.newsFeedPhotoLayout2),
        titleLayout = itemView.findViewById(R.id.newsFeedPhotoTextLayout2),
        titleText = itemView.findViewById(R.id.newsFeedPhotoText2),
        openLoverImage = itemView.findViewById(R.id.newsFeedPhotoOpenLoverImage2),
        photo = itemView.findViewById(R.id.newsFeedPhotoItemPhoto2)
    )
    val photo3 = LoveSpotMultiPhotoViews(
        partLayout = itemView.findViewById(R.id.newsFeedPhotoLayout3),
        titleLayout = itemView.findViewById(R.id.newsFeedPhotoTextLayout3),
        titleText = itemView.findViewById(R.id.newsFeedPhotoText3),
        openLoverImage = itemView.findViewById(R.id.newsFeedPhotoOpenLoverImage3),
        photo = itemView.findViewById(R.id.newsFeedPhotoItemPhoto3)
    )
    val photos = listOf(photo1, photo2, photo3)

    fun setTitle(
        multiLoveSpot: LoveSpotMultiEventsResponse,
        lovers: List<LoverViewWithoutRelationDto>,
        loveSpot: LoveSpotNewsFeedResponse,
        item: NewsFeedItemResponse
    ) {
        if (multiLoveSpot.loveSpotAddedHere) {
            val loveSpotAdder = lovers.first { it.id == loveSpot.addedBy }
            setTexts(
                publicLover = loveSpotAdder.takeIf { it.publicProfile },
                unknownActorText = R.string.somebody_added_a_new_lovespot,
                publicActorText = R.string.public_actor_added_a_new_lovespot,
                happenedAt = item.happenedAt,
                country = item.country
            )
        } else {
            val latestEventLover = multiLoveSpot.getLatestEventLover()
            setTexts(
                publicLover = latestEventLover.takeIf { it.publicProfile },
                unknownActorText = R.string.somebody_did_new_things_at_a_lovespot,
                publicActorText = R.string.public_actor_did_new_things_at_a_lovespot,
                happenedAt = item.happenedAt,
                country = item.country
            )
        }
    }

    fun showAllViews() {
        reviews.forEach { it.partLayout.visibility = View.VISIBLE }
        photos.forEach { it.partLayout.visibility = View.VISIBLE }
        loves.forEach { it.partLayout.visibility = View.VISIBLE }
    }

    fun hideLastRows(
        filledRows: Int,
        views: List<LoveSpotMultiBaseViews>
    ) {
        Log.i(tag, "filledRows: $filledRows")
        val hideFrom = views.size - (views.size - filledRows)
        Log.i(tag, "hideFrom: $hideFrom")

        views.forEachIndexed { index, view ->
            if (index >= hideFrom) {
                Log.i(tag, "Hiding ${view::class.simpleName} #$index")
                view.partLayout.visibility = View.GONE
            }
        }
    }

}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var progressBar: ProgressBar

    init {
        progressBar = itemView.findViewById(R.id.itemLoadingProgressBar)
    }
}

class UnsupportedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val newsFeedItemUnsupported: TextView

    init {
        newsFeedItemUnsupported = itemView.findViewById(R.id.newsFeedItemUnsupported)
    }
}
