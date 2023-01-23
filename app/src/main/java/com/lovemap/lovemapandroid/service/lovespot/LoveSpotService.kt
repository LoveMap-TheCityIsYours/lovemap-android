package com.lovemap.lovemapandroid.service.lovespot

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.ErrorMessage
import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.api.lovespot.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.service.GeoLocationService
import com.lovemap.lovemapandroid.service.Toaster
import com.lovemap.lovemapandroid.ui.data.LoveSpotHolder
import kotlinx.coroutines.*
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
    private val tag = "LoveSpotService"

    private val fullyQueriedAreas = CopyOnWriteArrayList<LatLngBounds>()
    private var risksQueried = false

    @Volatile
    var savedCreationState: CreateLoveSpotRequest? = null

    suspend fun findLocally(id: Long): LoveSpot? {
        return withContext(Dispatchers.IO) {
            loveSpotDao.loadSingle(id)
        }
    }

    suspend fun findLocallyOrFetch(loveSpotId: Long): LoveSpot? {
        return findLocally(loveSpotId) ?: refresh(loveSpotId, false)
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
            val loveSpots = search(listOrdering, listLocation, loveSpotSearchRequest)
            delay(delay)
            loveSpots.map { loveSpot -> LoveSpotHolder.of(loveSpot) }
                .toMutableList()
        }
    }

    suspend fun refresh(id: Long, showToast: Boolean = true): LoveSpot? {
        return withContext(Dispatchers.IO) {
            val localSpot = loveSpotDao.loadSingle(id)
            val call = loveSpotApi.find(id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                if (showToast) {
                    toaster.showToast(R.string.love_spot_not_available)
                }
                return@withContext localSpot
            }
            if (response.isSuccessful) {
                val loveSpot = response.body()!!
                loveSpotDao.insert(loveSpot)
                loveSpot
            } else {
                if (showToast) {
                    toaster.showToast(R.string.love_spot_not_available)
                }
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
                geoLocationService.getAndFetchCities()
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

    suspend fun saveAll(loveSpots: List<LoveSpot>): Set<LoveSpot> {
        return withContext(Dispatchers.IO) {
            loveSpotDao.insert(*loveSpots.toTypedArray())
            loveSpots.toSet()
        }
    }

    suspend fun listSpotsLocally(latLngBounds: LatLngBounds): List<LoveSpot> {
        return withContext(Dispatchers.IO) {
            val request = loveSpotSearchRequestFromBounds(latLngBounds)
            loveSpotDao.list(
                request.longFrom,
                request.longTo,
                request.latFrom,
                request.latTo
            )
        }
    }

    suspend fun listSpotsLocallyByIds(ids: Iterable<Long>): List<LoveSpot> {
        return withContext(Dispatchers.IO) {
            loveSpotDao.listByIdIn(ids.toList())
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
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    async { fetchSpotsFromServer(request, localSpots, latLngBounds) }
                }
                localSpots
            }
        }
    }

    private fun fetchSpotsFromServer(
        request: LoveSpotListRequest,
        localSpots: Set<LoveSpot>,
        latLngBounds: LatLngBounds
    ) {
        val call = loveSpotApi.list(request)
        val response = try {
            call.execute()
        } catch (e: Exception) {
            null
        }
        if (response?.isSuccessful == true) {
            val serverSpots = HashSet(response.body()!!)
            loveSpotDao.insert(*serverSpots.toTypedArray())
            if (serverSpots.size < request.limit) {
                fullyQueriedAreas.add(latLngBounds)
                removeDeletedSpotsFromArea(localSpots, serverSpots)
            } else {
                serverSpots.plus(localSpots)
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

    private suspend fun search(
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
                        localRisks
                    }
                } catch (e: Exception) {
                    localRisks
                }
            }

        }
    }

    suspend fun fetchRisks(): LoveSpotRisks? {
        return withContext(Dispatchers.IO) {
            val call = loveSpotApi.getRisks()
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    risksQueried = true
                    val ranks = response.body()!!
                    metadataStore.saveRisks(ranks)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
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
                MapContext.shouldClearMap = true
            }
        }
    }

    suspend fun getRecommendations(request: RecommendationsRequest): RecommendationsResponse {
        return withContext(Dispatchers.IO) {
            Log.i(tag, "Recommendations request: $request")
            val call = loveSpotApi.recommendations(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext RecommendationsResponse.empty()
            }
            if (response.isSuccessful) {
                val result: RecommendationsResponse = response.body()!!
                Log.i(tag, "Recommendations response: $result")
                Log.i(tag, "Recommendations Raw response: ${response.raw().body()?.contentLength()}")
                runCatching { loveSpotDao.insert(*result.topRatedSpots.toTypedArray()) }
                    .onFailure { Log.e(tag, "Error: topRatedSpots: ${result.topRatedSpots}", it) }

                runCatching { loveSpotDao.insert(*result.closestSpots.toTypedArray()) }
                    .onFailure { Log.e(tag, "Error: closestSpots: ${result.closestSpots}", it) }

                runCatching { loveSpotDao.insert(*result.recentlyActiveSpots.toTypedArray()) }
                    .onFailure { Log.e(tag, "Error: recentlyActiveSpots: ${result.recentlyActiveSpots}", it) }

                runCatching { loveSpotDao.insert(*result.popularSpots.toTypedArray()) }
                    .onFailure { Log.e(tag, "Error: popularSpots: ${result.popularSpots}", it) }

                runCatching { loveSpotDao.insert(*result.newestSpots.toTypedArray()) }
                    .onFailure { Log.e(tag, "Error: newestSpots: ${result.newestSpots}", it) }

                runCatching { loveSpotDao.insert(*result.recentPhotoSpots.toTypedArray()) }
                    .onFailure { Log.e(tag, "Error: recentPhotoSpots: ${result.recentPhotoSpots}", it) }

                result
            } else {
                toaster.showResponseError(response)
                RecommendationsResponse.empty()
            }
        }
    }

    suspend fun updatePhotoCount(loveSpotId: Long, photoCount: Int) {
        withContext(Dispatchers.IO) {
            loveSpotDao.loadSingle(loveSpotId)?.let {
                loveSpotDao.insert(it.copy(numberOfPhotos = photoCount))
            }
        }
    }
}