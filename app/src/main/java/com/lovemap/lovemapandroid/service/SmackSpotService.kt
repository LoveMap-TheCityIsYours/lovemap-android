package com.lovemap.lovemapandroid.service

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.lovemap.lovemapandroid.api.smackspot.CreateSmackSpotRequest
import com.lovemap.lovemapandroid.api.smackspot.SmackSpotApi
import com.lovemap.lovemapandroid.api.smackspot.SmackSpotRisks
import com.lovemap.lovemapandroid.api.smackspot.SmackSpotSearchRequest
import com.lovemap.lovemapandroid.api.smackspot.review.SmackSpotReviewRequest
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.smackspot.SmackSpot
import com.lovemap.lovemapandroid.data.smackspot.SmackSpotDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmackSpotService(
    private val smackSpotApi: SmackSpotApi,
    private val smackSpotDao: SmackSpotDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private val fullyQueriedAreas = ArrayList<LatLngBounds>()
    private var risksQueried = false

    suspend fun findLocally(id: Long): SmackSpot? {
        return withContext(Dispatchers.IO) {
            smackSpotDao.loadSingle(id)
        }
    }

    suspend fun refresh(id: Long): SmackSpot? {
        return withContext(Dispatchers.IO) {
            val localSpot = smackSpotDao.loadSingle(id)
            val call = smackSpotApi.find(id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localSpot
            }
            if (response.isSuccessful) {
                val smackSpot = response.body()!!
                smackSpotDao.insert(smackSpot)
                smackSpot
            } else {
                toaster.showNoServerToast()
                localSpot
            }
        }
    }

    suspend fun create(request: CreateSmackSpotRequest): SmackSpot? {
        return withContext(Dispatchers.IO) {
            val call = smackSpotApi.create(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val smackSpot = response.body()!!
                smackSpotDao.insert(smackSpot)
                smackSpot
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun search(latLngBounds: LatLngBounds): List<SmackSpot> {
        return withContext(Dispatchers.IO) {
            val request = smackSpotSearchRequestFromBounds(latLngBounds)
            val localSpots = smackSpotDao.search(
                request.longFrom,
                request.longTo,
                request.latFrom,
                request.latTo
            )

            if (areaFullyQueried(request)) {
                return@withContext localSpots
            }

            val call = smackSpotApi.search(request)
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
                val serverSpotSet: Set<SmackSpot> = HashSet(serverSpots)
                val deletedSpots = localSpotSet.subtract(serverSpotSet)
                smackSpotDao.delete(*deletedSpots.toTypedArray())
                smackSpotDao.insert(*serverSpotSet.toTypedArray())
                serverSpots
            } else {
                toaster.showNoServerToast()
                localSpots
            }
        }
    }

    suspend fun addReview(request: SmackSpotReviewRequest): SmackSpot? {
        return withContext(Dispatchers.IO) {
            val call = smackSpotApi.addReview(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                response.body()
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun getRisks(): SmackSpotRisks? {
        return withContext(Dispatchers.IO) {
            val localRisks: SmackSpotRisks? = if (metadataStore.isRisksStored()) {
                metadataStore.getRisks()
            } else {
                null
            }
            if (risksQueried) {
                return@withContext localRisks
            } else {
                val call = smackSpotApi.getRisks()
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

    private fun smackSpotSearchRequestFromBounds(latLngBounds: LatLngBounds): SmackSpotSearchRequest {
        val northeast = latLngBounds.northeast
        val southwest = latLngBounds.southwest
        return SmackSpotSearchRequest(
            latFrom = if (northeast.latitude < southwest.latitude) northeast.latitude else southwest.latitude,
            longFrom = if (northeast.longitude < southwest.longitude) northeast.longitude else southwest.longitude,
            latTo = if (northeast.latitude >= southwest.latitude) northeast.latitude else southwest.latitude,
            longTo = if (northeast.longitude >= southwest.longitude) northeast.longitude else southwest.longitude,
            limit = 100
        )
    }

    private fun areaFullyQueried(request: SmackSpotSearchRequest) =
        fullyQueriedAreas.any {
            it.contains(LatLng(request.latFrom, request.longFrom)) &&
                    it.contains(LatLng(request.latTo, request.longTo)) &&
                    it.contains(LatLng(request.latFrom, request.longTo)) &&
                    it.contains(LatLng(request.latTo, request.longFrom))
        }

    suspend fun update(smackSpot: SmackSpot) {
        return withContext(Dispatchers.IO) {
            smackSpotDao.insert(smackSpot)
        }
    }
}