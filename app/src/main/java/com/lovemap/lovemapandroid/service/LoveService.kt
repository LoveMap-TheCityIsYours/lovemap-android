package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.api.love.LoveListDto
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.love.LoveDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoveService(
    private val loveApi: LoveApi,
    private val loveDao: LoveDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {

    suspend fun getComplexList(): LoveListDto? {
        return withContext(Dispatchers.IO) {
            val call = loveApi.list(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val loveListDto = response.body()!!
                loveDao.insert(*loveListDto.loves.toTypedArray())
                loveListDto
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun list(): List<Love> {
        return withContext(Dispatchers.IO) {
            val localSpot = loveDao.getAll()
            val call = loveApi.list(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localSpot
            }
            if (response.isSuccessful) {
                val loveListDto = response.body()!!
                loveDao.insert(*loveListDto.loves.toTypedArray())
                loveListDto.loves
            } else {
                toaster.showNoServerToast()
                localSpot
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
}