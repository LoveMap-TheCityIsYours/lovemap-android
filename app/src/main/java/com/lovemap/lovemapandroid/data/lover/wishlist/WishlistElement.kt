package com.lovemap.lovemapandroid.data.lover.wishlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lovemap.lovemapandroid.utils.instantOfApiString
import java.time.Instant

@Entity(tableName = "wishlist_element")
data class WishlistElement(
    @PrimaryKey val wishlistElementId: Long,
    @ColumnInfo val loverId: Long,
    @ColumnInfo val addedAt: String,
    @ColumnInfo(index = true) val loveSpotId: Long
){
    fun addedAt(): Instant {
        return instantOfApiString(addedAt)
    }

}
