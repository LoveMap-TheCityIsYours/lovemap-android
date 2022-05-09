package com.lovemap.lovemapandroid.service

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.lovemap.lovemapandroid.api.lovespot.CreateLoveSpotRequest
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotApi
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotRisks
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotSearchRequest
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveSpotService(
    private val loveSpotApi: LoveSpotApi,
    private val loveSpotDao: LoveSpotDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private val fullyQueriedAreas = ArrayList<LatLngBounds>()
    private var risksQueried = false

    suspend fun findLocally(id: Long): LoveSpot? {
        return withContext(Dispatchers.IO) {
            loveSpotDao.loadSingle(id)
        }
    }

    suspend fun refresh(id: Long): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val localSpot = loveSpotDao.loadSingle(id)
            val call = loveSpotApi.find(id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localSpot
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()!!
                loveSpotDao.insert(loveSpot)
                loveSpot
            } else {
                toaster.showNoServerToast()
                localSpot
            }
        }
    }

    suspend fun create(request: CreateLoveSpotRequest): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotApi.create(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()!!
                loveSpotDao.insert(loveSpot)
                loveSpot
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun search(latLngBounds: LatLngBounds): List<LoveSpot> {
        return withContext(Dispatchers.IO) {
            val request = loveSpotSearchRequestFromBounds(latLngBounds)
            val localSpots = loveSpotDao.search(
                request.longFrom,
                request.longTo,
                request.latFrom,
                request.latTo
            )

            if (areaFullyQueried(request)) {
                return@withContext localSpots
            }

            val call = loveSpotApi.search(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localSpots
            }
            if (response.isSuccessful) {
                val serverSpots = response.body()!!
                if (serverSpots.size < request.limit) {
                    fullyQueriedAreas.add(latLngBounds)
                }
                val localSpotSet = HashSet(localSpots)
                val serverSpotSet: Set<LoveSpot> = HashSet(serverSpots)
                val deletedSpots = localSpotSet.subtract(serverSpotSet)
                loveSpotDao.delete(*deletedSpots.toTypedArray())
                loveSpotDao.insert(*serverSpotSet.toTypedArray())
                serverSpots
            } else {
                toaster.showNoServerToast()
                localSpots
            }
        }
    }

    suspend fun getRisks(): LoveSpotRisks? {
        return withContext(Dispatchers.IO) {
            val localRisks: LoveSpotRisks? = if (metadataStore.isRisksStored()) {
                metadataStore.getRisks()
            } else {
                null
            }
            if (risksQueried) {
                return@withContext localRisks
            } else {
                val call = loveSpotApi.getRisks()
                try {
                    val response = call.execute()
                    if (response.isSuccessful) {
                        risksQueried = true
                        val ranks = response.body()!!
                        metadataStore.saveRisks(ranks)
                    } else {
                        response
                        toaster.showNoServerToast()
                        localRisks
                    }
                } catch (e: Exception) {
                    toaster.showNoServerToast()
                    localRisks
                }
            }

        }
    }

    private fun loveSpotSearchRequestFromBounds(latLngBounds: LatLngBounds): LoveSpotSearchRequest {
        val northeast = latLngBounds.northeast
        val southwest = latLngBounds.southwest
        return LoveSpotSearchRequest(
            latFrom = if (northeast.latitude < southwest.latitude) northeast.latitude else southwest.latitude,
            longFrom = if (northeast.longitude < southwest.longitude) northeast.longitude else southwest.longitude,
            latTo = if (northeast.latitude >= southwest.latitude) northeast.latitude else southwest.latitude,
            longTo = if (northeast.longitude >= southwest.longitude) northeast.longitude else southwest.longitude,
            limit = 100
        )
    }

    private fun areaFullyQueried(request: LoveSpotSearchRequest) =
        fullyQueriedAreas.any {
            it.contains(LatLng(request.latFrom, request.longFrom)) &&
                    it.contains(LatLng(request.latTo, request.longTo)) &&
                    it.contains(LatLng(request.latFrom, request.longTo)) &&
                    it.contains(LatLng(request.latTo, request.longFrom))
        }

    suspend fun update(loveSpot: LoveSpot) {
        return withContext(Dispatchers.IO) {
            loveSpotDao.insert(loveSpot)
        }
    }
}