package com.lovemap.lovemapandroid.api.lovespot.review

data class LoveSpotReviewRequest(
    val loveId: Long,
    val reviewerId: Long,
    val loveSpotId: Long,
    val reviewText: String,
    val reviewStars: Int,
    val riskLevel: Int,
)