package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.api.lover.*
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoverService(
    private val loverApi: LoverApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private var ranksQueried = false

    suspend fun getById(): LoverRelationsDto? {
        return withContext(Dispatchers.IO) {
            val localLover: LoverRelationsDto? = if (metadataStore.isLoverStored()) {
                metadataStore.getLover()
            } else {
                null
            }
            val loggedInUser = metadataStore.getUser()
            val call = loverApi.getById(loggedInUser.id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localLover
            }
            if (response.isSuccessful) {
                val result: LoverRelationsDto = response.body()!!
                metadataStore.saveLover(result)
            } else {
                toaster.showNoServerToast()
                localLover
            }
        }
    }

    suspend fun generateLink(): LoverDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = metadataStore.getUser()
            val call = loverApi.generateLink(loggedInUser.id)
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

    suspend fun deleteLink(): LoverDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = metadataStore.getUser()
            val call = loverApi.deleteLink(loggedInUser.id)
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

    suspend fun getByLink(loverLink: String): LoverViewDto? {
        return withContext(Dispatchers.IO) {
            val call = loverApi.getByLink(loverLink)
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

    suspend fun getRanks(): LoverRanks? {
        return withContext(Dispatchers.IO) {
            val localRanks: LoverRanks? = if (metadataStore.isRanksStored()) {
                metadataStore.getRanks()
            } else {
                null
            }
            if (ranksQueried) {
                return@withContext localRanks
            } else {
                if (localRanks != null) {
                    return@withContext localRanks
                }
                val call = loverApi.getRanks()
                try {
                    val response = call.execute()
                    if (response.isSuccessful) {
                        ranksQueried = true
                        val ranks = response.body()!!
                        metadataStore.saveRanks(ranks)
                    } else {
                        toaster.showNoServerToast()
                        localRanks
                    }
                } catch (e: Exception) {
                    toaster.showNoServerToast()
                    localRanks
                }
            }
        }
    }
}