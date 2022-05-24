package com.lovemap.lovemapandroid.ui.data

import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview

data class ReviewHolder(
    val id: Long,
    val loveId: Long,
    val reviewerId: Long,
    val loveSpotId: Long,
    val reviewText: String,
    val reviewStars: Int,
    val riskLevel: Int,
) {
    companion object {
        fun of(review: LoveSpotReview): ReviewHolder {
            return ReviewHolder(
                id = review.id,
                loveId = review.loveId,
                reviewerId = review.reviewerId,
                loveSpotId = review.loveSpotId,
                reviewText = review.reviewText,
                reviewStars = review.reviewStars,
                riskLevel = review.riskLevel,
            )
        }
    }
}