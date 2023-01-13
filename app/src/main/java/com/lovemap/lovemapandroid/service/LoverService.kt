package com.lovemap.lovemapandroid.service

import android.util.Log
import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.api.lover.*
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.utils.LINK_PREFIX_API_CALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoverService(
    private val loverApi: LoverApi,
    private val loveDao: LoveDao,
    private val loveSpotDao: LoveSpotDao,
    private val loveSpotReviewDao: LoveSpotReviewDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private val tag = "LoverService"
    private var ranksQueried = false

    companion object {
        @Volatile
        var otherLoverId: Long = 0

        @Volatile
        var otherLover: LoverViewDto? = null
    }

    suspend fun getSelectedLover(): LoverViewDto? {
        if (otherLover != null && otherLover?.id == otherLoverId) {
            Log.i(tag, "Retutning getSelectedLover with already set otherLover $otherLover")
            return otherLover
        }
        return getOtherById(otherLoverId)
    }

    suspend fun getMyself(): LoverRelationsDto? {
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
                toaster.showResponseError(response)
                localLover
            }
        }
    }

    suspend fun fetchMyself(): LoverRelationsDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = metadataStore.getUser()
            val call = loverApi.getById(loggedInUser.id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val result: LoverRelationsDto = response.body()!!
                metadataStore.saveLover(result)
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun getOtherById(loverId: Long): LoverViewDto? {
        return withContext(Dispatchers.IO) {
            Log.i(tag, "Getting otherLover from the server. LoverId: '$loverId'")
            val call = loverApi.getLoverView(loverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val result: LoverViewDto = response.body()!!
                result
            } else {
                val errorMessages = response.getErrorMessages()
                toaster.showToast(errorMessages.toString())
                null
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

    suspend fun contributions(): LoverContributionsDto? {
        return withContext(Dispatchers.IO) {
            val call = loverApi.contributions(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                val loveListDto = response.body()!!
                loveDao.insert(*loveListDto.loves.toTypedArray())
                loveSpotDao.insert(*loveListDto.loveSpots.toTypedArray())
                loveSpotReviewDao.insert(*loveListDto.loveSpotReviews.toTypedArray())
                loveListDto
            } else {
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun getByUuid(uuid: String): LoverViewDto? {
        return withContext(Dispatchers.IO) {
            val call = loverApi.getByUuid(uuid.substringAfter(LINK_PREFIX_API_CALL))
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

    suspend fun fetchRanks(): LoverRanks? {
        return withContext(Dispatchers.IO) {
            val call = loverApi.getRanks()
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    ranksQueried = true
                    val ranks = response.body()!!
                    metadataStore.saveRanks(ranks)
                } else {
                    toaster.showNoServerToast()
                    null
                }
            } catch (e: Exception) {
                toaster.showNoServerToast()
                null
            }
        }
    }

}