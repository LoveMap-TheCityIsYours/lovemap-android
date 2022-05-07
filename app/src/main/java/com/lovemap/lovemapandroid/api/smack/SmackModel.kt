package com.lovemap.lovemapandroid.api.smack

import com.lovemap.lovemapandroid.api.smackspot.review.SmackSpotReviewDto
import com.lovemap.lovemapandroid.data.smack.Smack
import com.lovemap.lovemapandroid.data.smackspot.SmackSpot

data class SmackListDto(
    val smacks: List<Smack>,
    val smackSpots: List<SmackSpot>,
    val smackSpotReviews: List<SmackSpotReviewDto>
)

data class CreateSmackRequest(
    val name: String,
    val smackSpotId: Long,
    val smackerId: Long,
    val smackerPartnerId: Long? = null,
    val note: String? = null,
)
