package com.lovemap.lovemapandroid.service

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.ErrorMessage
import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.api.lovespot.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.ui.data.LoveSpotHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max
import kotlin.math.min

class LoveSpotService(
    private val loveSpotApi: LoveSpotApi,
    private val loveSpotDao: LoveSpotDao,
    private val geoLocationService: GeoLocationService,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private val fullyQueriedAreas = CopyOnWriteArrayList<LatLngBounds>()
    private var risksQueried = false

    @Volatile
    var savedCreationState: CreateLoveSpotRequest? = null

    suspend fun findLocally(id: Long): LoveSpot? {
        return withContext(Dispatchers.IO) {
            loveSpotDao.loadSingle(id)
        }
    }

    suspend fun getLoveSpotHolderList(): MutableList<LoveSpotHolder> {
        return withContext(Dispatchers.IO) {
            val loveSpots = loveSpotDao.getAllOrderedByRating()
            loveSpots.map { loveSpot -> LoveSpotHolder.of(loveSpot) }
                .toMutableList()
        }
    }

    suspend fun getLoveSpotHolderList(
        listOrdering: ListOrdering,
        listLocation: ListLocation,
        loveSpotSearchRequest: LoveSpotSearchRequest,
        delay: Long = 200,
    ): MutableList<LoveSpotHolder> {
        return withContext(Dispatchers.IO) {
            val loveSpots = advancedList(listOrdering, listLocation, loveSpotSearchRequest)
            delay(delay)
            loveSpots.map { loveSpot -> LoveSpotHolder.of(loveSpot) }
                .toMutableList()
        }
    }

    suspend fun refresh(id: Long): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val localSpot = loveSpotDao.loadSingle(id)
            val call = loveSpotApi.find(id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.love_spot_not_available)
                return@withContext localSpot
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()!!
                loveSpotDao.insert(loveSpot)
                loveSpot
            } else {
                toaster.showToast(R.string.love_spot_not_available)
                localSpot
            }
        }
    }

    suspend fun create(request: CreateLoveSpotRequest): LoveSpot? {
        return withContext(Dispatchers.IO) {
            savedCreationState = request
            val call = loveSpotApi.create(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                geoLocationService.getCities()
                savedCreationState = null
                val loveSpot = response.body()!!
                loveSpotDao.insert(loveSpot)
                loveSpot
            } else {
                val errorMessage: ErrorMessage = response.getErrorMessages()[0]
                toaster.showToast(errorMessage.translatedString(AppContext.INSTANCE.applicationContext))
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun update(id: Long, request: UpdateLoveSpotRequest): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotApi.update(id, request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.love_spot_not_available)
                return@withContext null
            }
            if (response.isSuccessful) {
                val love = response.body()!!
                loveSpotDao.insert(love)
                toaster.showToast(R.string.love_spot_updated)
                love
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun listSpotsLocally(): List<LoveSpot> {
        return withContext(Dispatchers.IO) {
            loveSpotDao.getAll()
        }
    }

    suspend fun list(latLngBounds: LatLngBounds): Collection<LoveSpot> {
        return withContext(Dispatchers.IO) {
            val request = loveSpotSearchRequestFromBounds(latLngBounds)
            val localSpots = HashSet(
                loveSpotDao.list(
                    request.longFrom,
                    request.longTo,
                    request.latFrom,
                    request.latTo
                )
            )

            if (areaFullyQueried(request)) {
                return@withContext localSpots
            }

            val call = loveSpotApi.list(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                return@withContext localSpots
            }
            if (response.isSuccessful) {
                val serverSpots = HashSet(response.body()!!)
                loveSpotDao.insert(*serverSpots.toTypedArray())
                val loveSpotSet = if (serverSpots.size < request.limit) {
                    fullyQueriedAreas.add(latLngBounds)
                    removeDeletedSpotsFromArea(localSpots, serverSpots)
                } else {
                    serverSpots.plus(localSpots)
                }
                loveSpotSet
            } else {
                localSpots
            }
        }
    }

    private fun removeDeletedSpotsFromArea(
        localSpotSet: Set<LoveSpot>,
        serverSpotSet: Set<LoveSpot>
    ): Set<LoveSpot> {
        val deletedSpots = localSpotSet.subtract(serverSpotSet)
        loveSpotDao.delete(*deletedSpots.toTypedArray())
        loveSpotDao.insert(*serverSpotSet.toTypedArray())
        return serverSpotSet.plus(localSpotSet).minus(deletedSpots)
    }

    private suspend fun advancedList(
        ordering: ListOrdering,
        location: ListLocation,
        request: LoveSpotSearchRequest
    ): List<LoveSpot> {
        return withContext(Dispatchers.IO) {
            val call = loveSpotApi.search(ordering, location, request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                val serverSpots = response.body()!!
                loveSpotDao.insert(*serverSpots.toTypedArray())
                serverSpots
            } else {
                toaster.showResponseError(response)
                emptyList()
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
                        localRisks
                    }
                } catch (e: Exception) {
                    localRisks
                }
            }

        }
    }

    private fun loveSpotSearchRequestFromBounds(latLngBounds: LatLngBounds): LoveSpotListRequest {
        val northeast = latLngBounds.northeast
        val southwest = latLngBounds.southwest
        val latFrom = min(northeast.latitude, southwest.latitude)
        val latTo = max(northeast.latitude, southwest.latitude)
        val latDif = (latTo - latFrom) * 0.2
        val longFrom = min(northeast.longitude, southwest.longitude)
        val longTo = max(northeast.longitude, southwest.longitude)
        val longDif = (longTo - longFrom) * 0.2
        return LoveSpotListRequest(
            latFrom = latFrom - latDif,
            latTo = latTo + latDif,
            longFrom = longFrom - longDif,
            longTo = longTo + longDif,
            limit = 100
        )
    }

    private fun areaFullyQueried(request: LoveSpotListRequest): Boolean {
        return fullyQueriedAreas.any {
            it.contains(LatLng(request.latFrom, request.longFrom)) &&
                    it.contains(LatLng(request.latTo, request.longTo)) &&
                    it.contains(LatLng(request.latFrom, request.longTo)) &&
                    it.contains(LatLng(request.latTo, request.longFrom))
        }
    }

    suspend fun insertIntoDb(loveSpot: LoveSpot) {
        return withContext(Dispatchers.IO) {
            loveSpotDao.insert(loveSpot)
        }
    }

    suspend fun deleteLocally(loveSpotId: Long) {
        return withContext(Dispatchers.IO) {
            loveSpotDao.loadSingle(loveSpotId)?.let {
                loveSpotDao.delete(it)
                AppContext.INSTANCE.shouldClearMap = true
            }
        }
    }

    suspend fun getRecommendations(request: RecommendationsRequest): RecommendationsResponse {
        return withContext(Dispatchers.IO) {
            val call = loveSpotApi.recommendations(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext RecommendationsResponse.empty()
            }
            if (response.isSuccessful) {
                val result = response.body()!!
                loveSpotDao.insert(*result.topRatedSpots.toTypedArray())
                loveSpotDao.insert(*result.closestSpots.toTypedArray())
                loveSpotDao.insert(*result.recentlyActiveSpots.toTypedArray())
                loveSpotDao.insert(*result.popularSpots.toTypedArray())
                result
            } else {
                toaster.showResponseError(response)
                RecommendationsResponse.empty()
            }
        }
    }
}