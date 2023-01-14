package com.lovemap.lovemapandroid.data.partnership

import androidx.room.*

@Dao
interface PartnershipDao {

    @Query("SELECT * FROM partnership WHERE id = :id")
    fun loadSingle(id: Long): Partnership?

    @Query("SELECT * FROM partnership")
    fun getAll(): List<Partnership>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg partnership: Partnership)

    @Delete
    fun delete(vararg partnership: Partnership)

    @Query("DELETE FROM partnership")
    fun deleteAll()
}