package com.lovemap.lovemapandroid.ui.data

import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItem
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

data class WishlistItemHolder(
    val wishlistItem: WishlistItem,
    val loveSpot: LoveSpot,
    val number: Int
) : Comparable<WishlistItemHolder> {
    var expanded = false

    override fun compareTo(other: WishlistItemHolder): Int {
        return (other.wishlistItem.addedAt().epochSecond - wishlistItem.addedAt().epochSecond).toInt()
    }
}