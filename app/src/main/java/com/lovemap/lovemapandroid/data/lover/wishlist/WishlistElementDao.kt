package com.lovemap.lovemapandroid.data.lover.wishlist

import androidx.room.*

@Dao
interface WishlistElementDao {

    @Query("SELECT * FROM wishlist_element WHERE wishlistElementId = :wishlistElementId")
    fun loadSingle(wishlistElementId: Long): WishlistElement?

    @Query("SELECT * FROM wishlist_element WHERE loveSpotId = :loveSpotId")
    fun getByLoveSpotId(loveSpotId: Long): WishlistElement?

    @Query("SELECT * FROM wishlist_element")
    fun getAll(): List<WishlistElement>

    @Query("SELECT * FROM wishlist_element ORDER BY addedAt DESC")
    fun getAllOrderedByDate(): List<WishlistElement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg wishlistElements: WishlistElement)

    @Delete
    fun delete(vararg wishlistElements: WishlistElement)
}