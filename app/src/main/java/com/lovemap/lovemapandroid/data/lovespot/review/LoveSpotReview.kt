package com.lovemap.lovemapandroid.data.lovespot.review

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "love_spot_review", indices = [
        Index(value = ["reviewerId", "loveSpotId"]),
        Index(value = ["loveId", "reviewerId"])
    ]
)
data class LoveSpotReview(
    @PrimaryKey val id: Long,
    @ColumnInfo val loveId: Long,
    @ColumnInfo val reviewerId: Long,
    @ColumnInfo(index = true) val loveSpotId: Long,
    @ColumnInfo val reviewText: String,
    @ColumnInfo val reviewStars: Int,
    @ColumnInfo val riskLevel: Int,
)
