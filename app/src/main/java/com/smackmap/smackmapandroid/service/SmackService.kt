package com.smackmap.smackmapandroid.service

import com.smackmap.smackmapandroid.api.smack.CreateSmackRequest
import com.smackmap.smackmapandroid.api.smack.SmackApi
import com.smackmap.smackmapandroid.api.smack.SmackListDto
import com.smackmap.smackmapandroid.data.metadata.MetadataStore
import com.smackmap.smackmapandroid.data.smack.Smack
import com.smackmap.smackmapandroid.data.smack.SmackDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmackService(
    private val smackApi: SmackApi,
    private val smackDao: SmackDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {

    suspend fun getComplexList(): SmackListDto? {
        return withContext(Dispatchers.IO) {
            val call = smackApi.list(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val smackListDto = response.body()!!
                smackDao.insert(*smackListDto.smacks.toTypedArray())
                smackListDto
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun list(): List<Smack> {
        return withContext(Dispatchers.IO) {
            val localSpot = smackDao.getAll()
            val call = smackApi.list(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localSpot
            }
            if (response.isSuccessful) {
                val smackListDto = response.body()!!
                smackDao.insert(*smackListDto.smacks.toTypedArray())
                smackListDto.smacks
            } else {
                toaster.showNoServerToast()
                localSpot
            }
        }
    }

    suspend fun create(request: CreateSmackRequest): Smack? {
        return withContext(Dispatchers.IO) {
            val call = smackApi.create(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val smackSpot = response.body()!!
                smackDao.insert(smackSpot)
                smackSpot
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }
}