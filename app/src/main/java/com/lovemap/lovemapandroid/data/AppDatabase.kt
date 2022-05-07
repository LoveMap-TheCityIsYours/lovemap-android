package com.lovemap.lovemapandroid.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lovemap.lovemapandroid.data.smack.Smack
import com.lovemap.lovemapandroid.data.smack.SmackDao
import com.lovemap.lovemapandroid.data.smackspot.SmackSpot
import com.lovemap.lovemapandroid.data.smackspot.SmackSpotDao

@Database(entities = [SmackSpot::class, Smack::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smackSpotDao(): SmackSpotDao
    abstract fun smackDao(): SmackDao
}