package com.lovemap.lovemapandroid.data.love

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lovemap.lovemapandroid.config.AppContext

@Entity(
    tableName = "love", indices = [
        Index(value = ["loverId", "loveSpotId"]),
        Index(value = ["loverPartnerId", "loveSpotId"])
    ]
)
data class Love(
    @PrimaryKey var id: Long,
    @ColumnInfo var name: String,
    @ColumnInfo(index = true) var loveSpotId: Long,
    @ColumnInfo var loverId: Long,
    @ColumnInfo(index = true) var happenedAt: String,
    @ColumnInfo var loverPartnerId: Long? = null,
    @ColumnInfo val partnerName: String? = null,
    @ColumnInfo var note: String? = null
) {
    fun getPartnerId(): Long? {
        if (loverId == AppContext.INSTANCE.userId) {
            return loverPartnerId
        }
        return loverId
    }
}