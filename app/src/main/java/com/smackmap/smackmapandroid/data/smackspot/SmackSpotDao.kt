package com.smackmap.smackmapandroid.data.smackspot

import androidx.room.*

@Dao
interface SmackSpotDao {

    @Query("SELECT * FROM smack_spot WHERE id = :id")
    fun loadSingle(id: Long): SmackSpot?

    @Query("SELECT * FROM smack_spot")
    fun getAll(): List<SmackSpot>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg smackSpots: SmackSpot)

    @Query(
        "SELECT * FROM smack_spot WHERE " +
                "longitude >= :longFrom AND longitude <= :longTo AND " +
                "latitude >= :latFrom AND latitude <= :latTo"
    )
    fun search(
        longFrom: Double,
        longTo: Double,
        latFrom: Double,
        latTo: Double,
    ): List<SmackSpot>

    @Delete
    fun delete(vararg smackSpots: SmackSpot)
}