package com.lovemap.lovemapandroid.service

import android.content.Context
import androidx.lifecycle.LiveData
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.ui.data.LoveHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class LoveService(
    private val loveApi: LoveApi,
    private val loveDao: LoveDao,
    private val loverService: LoverService,
    private val metadataStore: MetadataStore,
    private val context: Context,
    private val toaster: Toaster,
) {
    var lastUpdatedIndex = -1

    private val loveHolderList: MutableList<LoveHolder> = ArrayList()
    private var lovesQueried = false

    suspend fun list(): List<Love> {
        return withContext(Dispatchers.IO) {
            val localLoves = loveDao.getAll()
            if (!lovesQueried) {
                val call = loveApi.list(metadataStore.getUser().id)
                val response = try {
                    call.execute()
                } catch (e: Exception) {
                    toaster.showNoServerToast()
                    return@withContext localLoves
                }
                if (response.isSuccessful) {
                    lovesQueried = true
                    val loveList = response.body()!!
                    loveDao.insert(*loveList.toTypedArray())
                    loveList
                } else {
                    toaster.showNoServerToast()
                    localLoves
                }
            } else {
                localLoves
            }
        }
    }

    fun getLoveHolderList(): MutableList<LoveHolder> {
        return loveHolderList
    }

    suspend fun initLoveHolderList(): MutableList<LoveHolder> {
        return withContext(Dispatchers.IO) {
            loveHolderList.clear()
            val loves = list()
            loves.map { love -> LoveHolder.of(love, loverService, context) }
                .sortedByDescending { it.happenedAtLong }
                .forEach { loveHolderList.add(it) }
            return@withContext loveHolderList
        }
    }

    suspend fun create(request: CreateLoveRequest): Love? {
        return withContext(Dispatchers.IO) {
            val call = loveApi.create(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val love = response.body()!!
                val loveHolder = LoveHolder.of(love, loverService, context)
                val index = Collections.binarySearch(loveHolderList, loveHolder)
                val insertionIndex = -index - 1
                lastUpdatedIndex = insertionIndex
                loveHolderList.add(insertionIndex, loveHolder)
                loveDao.insert(love)
                love
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun getLoveByLoveSpotId(loveSpotId: Long): Love? {
        return withContext(Dispatchers.IO) {
            val localLoves = loveDao.getAll()
            return@withContext localLoves.find { it.loveSpotId == loveSpotId }
        }
    }

    suspend fun getLovesForSpot(spotId: Long): List<Love> {
        return withContext(Dispatchers.IO) {
            val lovesAsLover =
                loveDao.findByLoverAndSpotId(metadataStore.getUser().id, spotId)
            val lovesAsPartner =
                loveDao.findByPartnerAndSpotId(metadataStore.getUser().id, spotId)
            return@withContext ArrayList(lovesAsLover).apply { addAll(lovesAsPartner) }
        }
    }

    suspend fun madeLoveAlready(spotId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext getLovesForSpot(spotId).isNotEmpty()
        }
    }
}