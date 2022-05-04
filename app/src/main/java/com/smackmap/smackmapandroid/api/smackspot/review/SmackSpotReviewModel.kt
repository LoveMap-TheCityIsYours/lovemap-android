package com.smackmap.smackmapandroid.api.smackspot.review

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class SmackSpotReviewDto(
    @PrimaryKey val id: Long,
    @ColumnInfo val smackId: Long,
    @ColumnInfo val reviewerId: Long,
    @ColumnInfo val smackLocationId: Long,
    @ColumnInfo val reviewText: String,
    @ColumnInfo val reviewStars: Int,
)

data class SmackSpotReviewRequest(
    val smackId: Long,
    val reviewerId: Long,
    val smackLocationId: Long,
    val reviewText: String,
    val reviewStars: Int,
)