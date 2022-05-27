package com.lovemap.lovemapandroid.data.lovespot

import androidx.room.*

@Dao
interface LoveSpotDao {

    @Query("SELECT * FROM love_spot WHERE id = :id")
    fun loadSingle(id: Long): LoveSpot?

    @Query("SELECT * FROM love_spot")
    fun getAll(): List<LoveSpot>

    @Query("SELECT * FROM love_spot ORDER BY averageRating DESC")
    fun getAllOrderedByRating(): List<LoveSpot>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg loveSpots: LoveSpot)

    @Query(
        "SELECT * FROM love_spot WHERE " +
                "longitude >= :longFrom AND longitude <= :longTo AND " +
                "latitude >= :latFrom AND latitude <= :latTo"
    )
    fun search(
        longFrom: Double,
        longTo: Double,
        latFrom: Double,
        latTo: Double,
    ): List<LoveSpot>

    @Delete
    fun delete(vararg loveSpots: LoveSpot)
}