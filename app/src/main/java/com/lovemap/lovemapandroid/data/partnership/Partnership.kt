package com.lovemap.lovemapandroid.data.partnership

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus

@Entity(tableName = "partnership")
data class Partnership(
    @PrimaryKey val id: Long,
    @ColumnInfo val initiatorId: Long,
    @ColumnInfo val respondentId: Long,
    @ColumnInfo val partnershipStatus: PartnershipStatus,
    @ColumnInfo val initiateDate: String?,
    @ColumnInfo val respondDate: String?
)