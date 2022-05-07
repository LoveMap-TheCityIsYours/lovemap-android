package com.lovemap.lovemapandroid.api.lovespot.review

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class LoveSpotReviewDto(
    @PrimaryKey val id: Long,
    @ColumnInfo val loveId: Long,
    @ColumnInfo val reviewerId: Long,
    @ColumnInfo val loveLocationId: Long,
    @ColumnInfo val reviewText: String,
    @ColumnInfo val reviewStars: Int,
)

data class LoveSpotReviewRequest(
    val loveId: Long,
    val reviewerId: Long,
    val loveSpotId: Long,
    val reviewText: String,
    val reviewStars: Int,
    val riskLevel: Int,
)