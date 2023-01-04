package com.lovemap.lovemapandroid.data.lover.wishlist

import androidx.room.*

@Dao
interface WishlistItemDao {

    @Query("SELECT * FROM wishlist_item WHERE wishlistItemId = :wishlistItemId")
    fun loadSingle(wishlistItemId: Long): WishlistItem?

    @Query("SELECT * FROM wishlist_item WHERE loveSpotId = :loveSpotId")
    fun getByLoveSpotId(loveSpotId: Long): WishlistItem?

    @Query("SELECT * FROM wishlist_item")
    fun getAll(): List<WishlistItem>

    @Query("SELECT * FROM wishlist_item ORDER BY addedAt DESC")
    fun getAllOrderedByDate(): List<WishlistItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg wishlistItems: WishlistItem)

    @Delete
    fun delete(vararg wishlistItems: WishlistItem)
}