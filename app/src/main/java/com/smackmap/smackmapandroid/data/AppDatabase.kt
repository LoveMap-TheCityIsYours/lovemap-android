package com.smackmap.smackmapandroid.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smackmap.smackmapandroid.data.smack.Smack
import com.smackmap.smackmapandroid.data.smack.SmackDao
import com.smackmap.smackmapandroid.data.smackspot.SmackSpot
import com.smackmap.smackmapandroid.data.smackspot.SmackSpotDao

@Database(entities = [SmackSpot::class, Smack::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smackSpotDao(): SmackSpotDao
    abstract fun smackDao(): SmackDao
}