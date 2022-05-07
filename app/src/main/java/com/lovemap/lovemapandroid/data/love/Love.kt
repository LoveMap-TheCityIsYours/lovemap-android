package com.lovemap.lovemapandroid.data.love

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "love")
data class Love(
    @PrimaryKey var id: Long,
    @ColumnInfo var name: String,
    @ColumnInfo var loveSpotId: Long,
    @ColumnInfo var loverId: Long,
    @ColumnInfo var loverPartnerId: Long? = null,
    @ColumnInfo var note: String? = null,
)