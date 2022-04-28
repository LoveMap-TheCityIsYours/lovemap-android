package com.smackmap.smackmapandroid.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smackmap.smackmapandroid.data.smackspot.SmackSpot
import com.smackmap.smackmapandroid.data.smackspot.SmackSpotDao

@Database(entities = [SmackSpot::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smackSpotDao(): SmackSpotDao
}