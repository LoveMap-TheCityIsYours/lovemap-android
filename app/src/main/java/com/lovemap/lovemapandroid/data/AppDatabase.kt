package com.lovemap.lovemapandroid.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao

@Database(
    entities = [Love::class, LoveSpot::class, LoveSpotReview::class, Partnership::class],
    version = 8
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loveDao(): LoveDao
    abstract fun loveSpotDao(): LoveSpotDao
    abstract fun loveSpotReviewDao(): LoveSpotReviewDao
    abstract fun partnershipDao(): PartnershipDao
}