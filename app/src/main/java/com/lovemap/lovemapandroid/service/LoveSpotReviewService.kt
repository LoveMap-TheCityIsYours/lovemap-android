package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewApi
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.ui.data.ReviewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveSpotReviewService(
    private val loveSpotReviewApi: LoveSpotReviewApi,
    private val loveSpotReviewDao: LoveSpotReviewDao,
    private val loveSpotService: LoveSpotService,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    suspend fun submitReview(request: LoveSpotReviewRequest): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotReviewApi.submitReview(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.love_spot_not_available)
                return@withContext null
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()
                try {
                    val reviewsCall = loveSpotReviewApi.getReviewsByLover(request.reviewerId)
                    val reviewsResponse = reviewsCall.execute()
                    if (reviewsResponse.isSuccessful) {
                        val reviews = reviewsResponse.body()!!
                        loveSpotReviewDao.insert(*reviews.toTypedArray())
                        loveSpotService.refresh(request.loveSpotId)
                    }
                } catch (e: Exception) {
                    toaster.showToast(R.string.love_spot_not_available)
                }
                loveSpot
            } else {
                toaster.showToast(R.string.love_spot_not_available)
                null
            }
        }
    }

    suspend fun hasReviewedAlready(spotId: Long): Boolean {
        return findByLoverAndSpotId(spotId) != null
    }

    suspend fun findByLoverAndSpotId(spotId: Long): LoveSpotReview? {
        return withContext(Dispatchers.IO) {
            return@withContext loveSpotReviewDao.findByLoverAndSpotId(
                metadataStore.getUser().id,
                spotId
            )
        }
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
            val call = loveSpotReviewApi.getReviewsByLover(loverId)
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

    suspend fun getReviewsBySpot(): List<LoveSpotReview> {
        return withContext(Dispatchers.IO) {
            val spotId = AppContext.INSTANCE.selectedLoveSpotId!!
            val localReviews = loveSpotReviewDao.getAllBySpot(spotId)
            val call = loveSpotReviewApi.getReviewsForSpot(spotId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.love_spot_not_available)
                return@withContext localReviews
            }
            if (response.isSuccessful) {
                val reviewList = response.body()!!
                loveSpotReviewDao.insert(*reviewList.toTypedArray())
                reviewList
            } else {
                toaster.showToast(R.string.love_spot_not_available)
                localReviews
            }
        }
    }

    suspend fun getReviewHoldersBySpot(): List<ReviewHolder> {
        return withContext(Dispatchers.IO) {
            return@withContext getReviewsBySpot().map { ReviewHolder.of(it) }
        }
    }
}