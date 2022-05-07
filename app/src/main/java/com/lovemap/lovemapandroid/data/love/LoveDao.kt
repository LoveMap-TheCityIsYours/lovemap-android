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
}