package com.lovemap.lovemapandroid.api.love

import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview

data class LoveListDto(
    val loves: List<Love>,
    val loveSpots: List<LoveSpot>,
    val loveSpotReviews: List<LoveSpotReview>
)

data class CreateLoveRequest(
    val name: String,
    val loveSpotId: Long,
    val loverId: Long,
    val loverPartnerId: Long? = null,
    val note: String? = null,
)
