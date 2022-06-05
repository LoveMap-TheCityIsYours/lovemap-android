package com.lovemap.lovemapandroid.data.love

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lovemap.lovemapandroid.config.AppContext

@Entity(tableName = "love")
data class Love(
    @PrimaryKey var id: Long,
    @ColumnInfo var name: String,
    @ColumnInfo var loveSpotId: Long,
    @ColumnInfo var loverId: Long,
    @ColumnInfo var happenedAt: String,
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