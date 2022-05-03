package com.smackmap.smackmapandroid.service.smacker

import android.os.Looper
import com.smackmap.smackmapandroid.api.smacker.*
import com.smackmap.smackmapandroid.data.MetadataStore
import com.smackmap.smackmapandroid.service.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmackerService(
    private val smackerApi: SmackerApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private var ranksQueried = false
    private val mainLooper = Looper.getMainLooper()

    suspend fun getById(): SmackerRelationsDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = metadataStore.getUser()
            val call = smackerApi.getById(loggedInUser.id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                response.body()
            } else {
                toaster.showToast(response.errorBody()?.string() ?: response.message())
//                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun generateLink(): SmackerDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = metadataStore.getUser()
            val call = smackerApi.generateLink(loggedInUser.id)
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

    suspend fun deleteLink(): SmackerDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = metadataStore.getUser()
            val call = smackerApi.deleteLink(loggedInUser.id)
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

    suspend fun getByLink(smackerLink: String): SmackerViewDto? {
        return withContext(Dispatchers.IO) {
            val call = smackerApi.getByLink(smackerLink)
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

    suspend fun getRanks(): SmackerRanks? {
        return withContext(Dispatchers.IO) {
            val localRanks: SmackerRanks? = if (metadataStore.isRanksStored()) {
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
                val call = smackerApi.getRanks()
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