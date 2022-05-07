package com.lovemap.lovemapandroid.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao

@Database(entities = [LoveSpot::class, Love::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loveSpotDao(): LoveSpotDao
    abstract fun loveDao(): LoveDao
}