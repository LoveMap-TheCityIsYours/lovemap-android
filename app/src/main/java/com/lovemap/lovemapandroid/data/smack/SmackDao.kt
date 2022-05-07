package com.lovemap.lovemapandroid.data.smack

import androidx.room.*

@Dao
interface SmackDao {

    @Query("SELECT * FROM love WHERE id = :id")
    fun loadSingle(id: Long): Smack?

    @Query("SELECT * FROM love")
    fun getAll(): List<Smack>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg smacks: Smack)

    @Delete
    fun delete(vararg smacks: Smack)
}