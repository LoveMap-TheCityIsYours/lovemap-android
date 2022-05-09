package com.lovemap.lovemapandroid.data.lovespot.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "love_spot_review")
data class LoveSpotReview(
    @PrimaryKey val id: Long,
    @ColumnInfo val loveId: Long,
    @ColumnInfo val reviewerId: Long,
    @ColumnInfo val loveSpotId: Long,
    @ColumnInfo val reviewText: String,
    @ColumnInfo val reviewStars: Int,
    @ColumnInfo val riskLevel: Int,
)
