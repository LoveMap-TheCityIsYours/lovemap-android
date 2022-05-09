package com.lovemap.lovemapandroid.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao

@Database(entities = [Love::class, LoveSpot::class, LoveSpotReview::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loveDao(): LoveDao
    abstract fun loveSpotDao(): LoveSpotDao
    abstract fun loveSpotReviewDao(): LoveSpotReviewDao
}