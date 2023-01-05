package com.lovemap.lovemapandroid.ui.data

import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItem
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

data class WishlistItemDto(
    val wishlistItem: WishlistItem,
    val loveSpot: LoveSpot,
) : Comparable<WishlistItemDto> {

    override fun compareTo(other: WishlistItemDto): Int {
        return (other.wishlistItem.addedAt().epochSecond - wishlistItem.addedAt().epochSecond).toInt()
    }

}
