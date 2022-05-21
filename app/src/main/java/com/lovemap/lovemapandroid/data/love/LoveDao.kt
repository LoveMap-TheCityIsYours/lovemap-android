package com.lovemap.lovemapandroid.data.love

import androidx.room.*

@Dao
interface LoveDao {

    @Query("SELECT * FROM love WHERE id = :id")
    fun loadSingle(id: Long): Love?

    @Query("SELECT * FROM love")
    fun getAll(): List<Love>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg loves: Love)

    @Delete
    fun delete(vararg loves: Love)

    @Query("SELECT * FROM love WHERE loverId = :loverId AND loveSpotId = :spotId")
    fun findByLoverAndSpotId(loverId: Long, spotId: Long): List<Love>

    @Query("SELECT * FROM love WHERE loverPartnerId = :partnerId AND loveSpotId = :spotId")
    fun findByPartnerAndSpotId(partnerId: Long, spotId: Long): List<Love>
}