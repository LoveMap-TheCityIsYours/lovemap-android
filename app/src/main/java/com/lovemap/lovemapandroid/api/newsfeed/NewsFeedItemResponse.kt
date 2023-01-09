package com.lovemap.lovemapandroid.api.newsfeed

import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType

data class NewsFeedItemResponse(
    val type: NewsFeedItemType,
    val generatedAt: String,
    val generatedAtFormatted: String,
    val happenedAt: String,
    val happenedAtFormatted: String,
    val referenceId: Long,
    val loveSpot: LoveSpotNewsFeedResponse? = null,
    val love: LoveNewsFeedResponse? = null,
    val loveSpotReview: LoveSpotReviewNewsFeedResponse? = null,
    val loveSpotPhoto: LoveSpotPhotoNewsFeedResponse? = null,
    val photoLike: PhotoLikeNewsFeedResponse? = null,
    val wishlist: WishlistNewsFeedResponse? = null,
    val lover: LoverNewsFeedResponse? = null
)

enum class NewsFeedItemType {
    LOVE_SPOT,
    LOVE_SPOT_REVIEW,
    LOVE_SPOT_PHOTO,
    LOVE_SPOT_PHOTO_LIKE,
    LOVE,
    WISHLIST_ITEM,
    LOVER
}

data class LoveSpotNewsFeedResponse(
    val id: Long,
    val createdAt: String,
    val addedBy: Long,
    val name: String,
    val description: String,
    val type: LoveSpotType,
    val country: String?
)

data class LoveNewsFeedResponse(
    val id: Long,
    val name: String,
    val loveSpotId: Long,
    val loverId: Long,
    val happenedAt: String,
    val loverPartnerId: Long?,
)

data class LoveSpotReviewNewsFeedResponse(
    val id: Long,
    val loveSpotId: Long,
    val reviewerId: Long,
    val submittedAt: String,
    val reviewText: String,
    val reviewStars: Int,
    val riskLevel: Int
)

data class LoveSpotPhotoNewsFeedResponse(
    val id: Long,
    val loveSpotId: Long,
    val uploadedBy: Long,
    val uploadedAt: String,
    val fileName: String,
    val loveSpotReviewId: Long?,
    val likes: Int,
    val dislikes: Int,
)

data class PhotoLikeNewsFeedResponse(
    val id: Long,
    val loveSpotId: Long,
    val loveSpotPhotoId: Long,
    val happenedAt: String,
    val loverId: Long,
    val likeOrDislike: Int
)

data class WishlistNewsFeedResponse(
    val id: Long,
    val loverId: Long,
    val loveSpotId: Long,
    val addedAt: String
)

data class LoverNewsFeedResponse(
    val id: Long,
    val userName: String,
    val publicProfile: Boolean,
    val joinedAt: String,
    val rank: Int,
    val points: Int,
    val uuid: String?
)