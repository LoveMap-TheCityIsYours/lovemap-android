package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.report.LoveSpotReportApi
import com.lovemap.lovemapandroid.api.lovespot.report.LoveSpotReportRequest
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReview
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveSpotReportService(
    private val loveSpotReportApi: LoveSpotReportApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    suspend fun submitReport(request: LoveSpotReportRequest): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotReportApi.submitReport(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()
                toaster.showToast(R.string.loveSpotReported)
                loveSpot
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun getReportsByLover(): List<LoveSpotReview> {
        return withContext(Dispatchers.IO) {
            val loverId = metadataStore.getUser().id
            val call = loveSpotReportApi.getReviewsByLover(loverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                val reviewList = response.body()!!
                reviewList
            } else {
                toaster.showNoServerToast()
                emptyList()
            }
        }
    }

    suspend fun getReportsBySpot(spotId: Long): List<LoveSpotReview> {
        return withContext(Dispatchers.IO) {
            val call = loveSpotReportApi.getReviewsForSpot(spotId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                val reviewList = response.body()!!
                reviewList
            } else {
                toaster.showNoServerToast()
                emptyList()
            }
        }
    }
}