package com.lovemap.lovemapandroid.service

import android.content.Context
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.api.love.UpdateLoveRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.ui.data.LoveHolder
import com.lovemap.lovemapandroid.ui.events.LoveListUpdatedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.time.Instant

class LoveService(
    private val loveApi: LoveApi,
    private val loveDao: LoveDao,
    private val loveSpotReviewService: LoveSpotReviewService,
    private val wishlistService: WishlistService,
    private val metadataStore: MetadataStore,
    private val context: Context,
    private val toaster: Toaster,
) {
    @Volatile
    var savedCreationState: SavedCreationState? = null

    class SavedCreationState(
        val note: String,
        val partnerSelection: Int,
        val happenedAt: Instant
    )

    suspend fun list(): List<Love> {
        return withContext(Dispatchers.IO) {
            val localLoves = loveDao.getAllOrderedByDate()
            val call = loveApi.listByLover(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                return@withContext localLoves
            }
            if (response.isSuccessful) {
                val serverLoves = response.body()!!
                val localLoveSet = HashSet(localLoves)
                val serverLoveSet = HashSet(serverLoves)
                val deletedLoves = localLoveSet.subtract(serverLoveSet)
                loveDao.delete(*deletedLoves.toTypedArray())
                loveDao.insert(*serverLoveSet.toTypedArray())
                serverLoves
            } else {
                localLoves
            }
        }
    }

    suspend fun localList(): List<Love> {
        return withContext(Dispatchers.IO) {
            loveDao.getAllOrderedByDate()
        }
    }

    suspend fun create(request: CreateLoveRequest): Love? {
        return withContext(Dispatchers.IO) {
            val call = loveApi.create(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.love_spot_not_available)
                return@withContext null
            }
            if (response.isSuccessful) {
                savedCreationState = null
                val love = response.body()!!
                wishlistService.removeLocallyByLoveSpot(request.loveSpotId)
                loveDao.insert(love)
                EventBus.getDefault().post(LoveListUpdatedEvent(getLoveHolderList()))
                love
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun update(id: Long, request: UpdateLoveRequest): Love? {
        return withContext(Dispatchers.IO) {
            val localLove = loveDao.loadSingle(id)
            localLove?.let {
                if (isPartnerInLove(it)) {
                    request.loverPartnerId = it.loverPartnerId
                }
                val call = loveApi.update(id, request)
                val response = try {
                    call.execute()
                } catch (e: Exception) {
                    toaster.showToast(R.string.love_spot_not_available)
                    return@withContext null
                }
                if (response.isSuccessful) {
                    val love = response.body()!!
                    loveDao.insert(love)
                    toaster.showToast(R.string.love_making_updated)
                    EventBus.getDefault().post(LoveListUpdatedEvent(getLoveHolderList()))
                    love
                } else {
                    toaster.showResponseError(response)
                    null
                }
            }
        }
    }

    suspend fun isPartnerInLove(it: Love) =
        it.loverPartnerId == metadataStore.getUser().id

    suspend fun delete(id: Long): Love? {
        return withContext(Dispatchers.IO) {
            val call = loveApi.delete(id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.love_spot_not_available)
                return@withContext null
            }
            if (response.isSuccessful) {
                val love = response.body()!!
                loveDao.delete(love)
                loveSpotReviewService.loveDeleted(id)
                EventBus.getDefault().post(LoveListUpdatedEvent(getLoveHolderList()))
                toaster.showToast(R.string.love_making_deleted)
                love
            } else {
                toaster.showToast(R.string.love_spot_not_available)
                null
            }
        }
    }

    suspend fun getAnyLoveByLoveSpotId(loveSpotId: Long): Love? {
        return withContext(Dispatchers.IO) {
            val localLoves = loveDao.getAll()
            return@withContext localLoves.find { it.loveSpotId == loveSpotId }
        }
    }

    suspend fun madeLoveAlready(spotId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext getLovesForSpotLocally(spotId).isNotEmpty()
        }
    }

    suspend fun getLoveHolderList(): MutableList<LoveHolder> {
        return withContext(Dispatchers.IO) {
            val loves = list()
            loves.mapIndexed { idx, love -> LoveHolder.of(love, loves.size - idx, context) }
                .toMutableList()
        }
    }

    suspend fun getLoveHolderListForSpot(): MutableList<LoveHolder> {
        val loveSpotId = AppContext.INSTANCE.selectedLoveSpotId
        loveSpotId?.let {
            list()
            val lovesForSpot = getLovesForSpotLocally(loveSpotId)
            return lovesForSpot
                .mapIndexed { idx, love -> LoveHolder.of(love, lovesForSpot.size - idx, context) }
                .sortedByDescending { it.happenedAtLong }
                .toMutableList()
        }
        return ArrayList()
    }

    suspend fun getLoveHolderListForPartner(): MutableList<LoveHolder> {
        return getLoveHolderList()
            .filter { it.partnerId == LoverService.otherLoverId }
            .toMutableList()
    }

    suspend fun getLocally(id: Long): Love? {
        return withContext(Dispatchers.IO) {
            return@withContext loveDao.loadSingle(id)
        }
    }

    suspend fun deleteLocallyBySpotId(loveSpotId: Long) {
        return withContext(Dispatchers.IO) {
            val loves = loveDao.findBySpotId(loveSpotId)
            loveDao.delete(*loves.toTypedArray())
            EventBus.getDefault().post(LoveListUpdatedEvent(getLoveHolderList()))
        }
    }

    private suspend fun getLovesForSpotLocally(spotId: Long): List<Love> {
        return withContext(Dispatchers.IO) {
            val lovesAsLover =
                loveDao.findByLoverAndSpotId(metadataStore.getUser().id, spotId)
            val lovesAsPartner =
                loveDao.findByPartnerAndSpotId(metadataStore.getUser().id, spotId)
            return@withContext ArrayList(lovesAsLover).apply { addAll(lovesAsPartner) }
        }
    }
}