package com.lovemap.lovemapandroid.api.lover.wishlist

import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItem
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.utils.instantOfApiString
import java.time.Instant

data class WishlistResponse(
    val wishlistItemId: Long,
    val loverId: Long,
    val addedAt: String,
    val loveSpot: LoveSpot
) {
    fun addedAt(): Instant {
        return instantOfApiString(addedAt)
    }

    fun toWishlistItem(): WishlistItem {
        return WishlistItem(
            wishlistItemId = wishlistItemId,
            loverId = loverId,
            addedAt = addedAt,
            loveSpotId = loveSpot.id
        )
    }
}
