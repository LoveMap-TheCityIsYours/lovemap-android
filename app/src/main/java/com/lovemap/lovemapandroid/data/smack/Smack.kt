package com.lovemap.lovemapandroid.data.smack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smack")
data class Smack(
    @PrimaryKey var id: Long,
    @ColumnInfo var name: String,
    @ColumnInfo var smackSpotId: Long,
    @ColumnInfo var smackerId: Long,
    @ColumnInfo var smackerPartnerId: Long? = null,
    @ColumnInfo var note: String? = null,
)