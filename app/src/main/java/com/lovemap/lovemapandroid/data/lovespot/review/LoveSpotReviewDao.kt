package com.lovemap.lovemapandroid.data.lovespot.review

import androidx.room.*

@Dao
interface LoveSpotReviewDao {

    @Query("SELECT * FROM love_spot_review WHERE id = :id")
    fun loadSingle(id: Long): LoveSpotReview?

    @Query("SELECT * FROM love_spot_review WHERE reviewerId = :loverId AND loveSpotId = :spotId")
    fun findByLoverAndSpotId(loverId: Long, spotId: Long): LoveSpotReview?

    @Query("SELECT * FROM love_spot_review WHERE reviewerId = :loverId AND loveId = :loveId")
    fun findByLoverAndLoveId(loverId: Long, loveId: Long): LoveSpotReview?

    @Query("SELECT * FROM love_spot_review WHERE reviewerId = :loverId")
    fun getAllByLover(loverId: Long): List<LoveSpotReview>

    @Query("SELECT * FROM love_spot_review WHERE loveSpotId = :spotId")
    fun getAllBySpot(spotId: Long): List<LoveSpotReview>

    @Query("SELECT * FROM love_spot_review")
    fun getAll(): List<LoveSpotReview>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg reviews: LoveSpotReview)

    @Delete
    fun delete(vararg reviews: LoveSpotReview)
}