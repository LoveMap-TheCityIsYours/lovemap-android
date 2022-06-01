package com.lovemap.lovemapandroid.service

import android.app.Activity
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
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
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

    @Volatile
    var savedCreationState: CreateLoveSpotRequest? = null

    suspend fun findLocally(id: Long): LoveSpot? {
        return withContext(Dispatchers.IO) {
            loveSpotDao.loadSingle(id)
        }
    }

    suspend fun listSpotsLocally(): List<LoveSpot> {
        return withContext(Dispatchers.IO) {
            loveSpotDao.getAll()
        }
    }

    suspend fun getLoveHolderList(): MutableList<LoveSpotHolder> {
        return withContext(Dispatchers.IO) {
            val loveSpots = loveSpotDao.getAllOrderedByRating()
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

    suspend fun create(request: CreateLoveSpotRequest, activity: Activity): LoveSpot? {
        return withContext(Dispatchers.IO) {
            savedCreationState = request
            val call = loveSpotApi.create(request)
            // TODO: show or not???
//            val loadingBarShower = LoadingBarShower(activity).show()
            val loadingBarShower = LoadingBarShower(activity)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                loadingBarShower.onResponse()
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                savedCreationState = null
                loadingBarShower.onResponse()
                val loveSpot = response.body()!!
                loveSpotDao.insert(loveSpot)
                // TODO: optimize a lot
                loveSpot
            } else {
                loadingBarShower.onResponse()
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
//                toaster.showNoServerToast()
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
                // TODO: optimize a lot
                serverSpots
            } else {
//                toaster.showNoServerToast()
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
//                        toaster.showNoServerToast()
                        localRisks
                    }
                } catch (e: Exception) {
//                    toaster.showNoServerToast()
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
}