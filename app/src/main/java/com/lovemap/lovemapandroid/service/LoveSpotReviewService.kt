package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.api.lovespot.LoveSpotApi
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveSpotReviewService(
    private val loveSpotApi: LoveSpotApi,
    private val loveSpotReviewDao: LoveSpotReviewDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    suspend fun addReview(request: LoveSpotReviewRequest): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotApi.addReview(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()
                try {
                    val reviewsCall = loveSpotApi.getReviewsByLover(request.reviewerId)
                    val reviewsResponse = reviewsCall.execute()
                    if (reviewsResponse.isSuccessful) {
                        val reviews = reviewsResponse.body()!!
                        loveSpotReviewDao.insert(*reviews.toTypedArray())
                    }
                } catch (e: Exception) {
                    // TODO: find out what to do. nothing is good for now.
                }
                loveSpot
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun hasReviewedAlready(spotId: Long) = getLocalReviewsByLover().any { review ->
        review.loveSpotId == spotId
    }

    suspend fun getLocalReviewsByLover(): List<LoveSpotReview> {
        return withContext(Dispatchers.IO) {
            val loverId = metadataStore.getUser().id
            return@withContext loveSpotReviewDao.getAllByLover(loverId)
        }
    }

    suspend fun getReviewsByLover(): List<LoveSpotReview> {
        return withContext(Dispatchers.IO) {
            val loverId = metadataStore.getUser().id
            val localReviews = loveSpotReviewDao.getAllByLover(loverId)
            val call = loveSpotApi.getReviewsByLover(loverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localReviews
            }
            if (response.isSuccessful) {
                val reviewList = response.body()!!
                loveSpotReviewDao.insert(*reviewList.toTypedArray())
                reviewList
            } else {
                toaster.showNoServerToast()
                localReviews
            }
        }
    }

    suspend fun getReviewsBySpot(spotId: Long): List<LoveSpotReview> {
        return withContext(Dispatchers.IO) {
            val localReviews = loveSpotReviewDao.getAllBySpot(spotId)
            val call = loveSpotApi.getReviewsForSpot(spotId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localReviews
            }
            if (response.isSuccessful) {
                val reviewList = response.body()!!
                loveSpotReviewDao.insert(*reviewList.toTypedArray())
                reviewList
            } else {
                toaster.showNoServerToast()
                localReviews
            }
        }
    }
}