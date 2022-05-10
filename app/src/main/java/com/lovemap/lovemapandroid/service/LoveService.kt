package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveService(
    private val loveApi: LoveApi,
    private val loveDao: LoveDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {

    suspend fun list(): List<Love> {
        return withContext(Dispatchers.IO) {
            val localLoves = loveDao.getAll()
            val call = loveApi.list(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localLoves
            }
            if (response.isSuccessful) {
                val loveList = response.body()!!
                loveDao.insert(*loveList.toTypedArray())
                loveList
            } else {
                toaster.showNoServerToast()
                localLoves
            }
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
                val loveSpot = response.body()!!
                loveDao.insert(loveSpot)
                loveSpot
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
}