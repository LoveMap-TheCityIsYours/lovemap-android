package com.lovemap.lovemapandroid.api.lover.wishlist

import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistElement
import com.lovemap.lovemapandroid.utils.instantOfApiString
import java.time.Instant

data class WishlistResponse(
    val wishlistElementId: Long,
    val loverId: Long,
    val addedAt: String,
    val loveSpot: WishlistLoveSpot
) {
    fun addedAt(): Instant {
        return instantOfApiString(addedAt)
    }

    fun toWishlistElement(): WishlistElement {
        return WishlistElement(
            wishlistElementId = wishlistElementId,
            loverId = loverId,
            addedAt = addedAt,
            loveSpotId = loveSpot.id
        )
    }
}

data class WishlistLoveSpot(
    val id: Long
)