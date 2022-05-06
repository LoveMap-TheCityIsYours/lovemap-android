package com.smackmap.smackmapandroid.data.smack

import androidx.room.*

@Dao
interface SmackDao {

    @Query("SELECT * FROM smack WHERE id = :id")
    fun loadSingle(id: Long): Smack?

    @Query("SELECT * FROM smack")
    fun getAll(): List<Smack>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg smacks: Smack)

    @Delete
    fun delete(vararg smacks: Smack)
}