package com.smackmap.smackmapandroid.api.smack

import com.smackmap.smackmapandroid.api.smackspot.review.SmackSpotReviewDto
import com.smackmap.smackmapandroid.data.smack.Smack
import com.smackmap.smackmapandroid.data.smackspot.SmackSpot

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
