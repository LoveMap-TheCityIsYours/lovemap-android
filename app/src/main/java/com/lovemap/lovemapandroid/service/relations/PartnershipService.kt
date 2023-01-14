package com.lovemap.lovemapandroid.service.relations

import android.app.Activity
import android.util.Log
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.partnership.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao
import com.lovemap.lovemapandroid.service.Toaster
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PartnershipService(
    private val partnershipApi: PartnershipApi,
    private val partnershipDao: PartnershipDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {
    private val tag = "PartnershipService"

    suspend fun getPartnershipLocally(): Partnership? {
        return withContext(Dispatchers.IO) {
            partnershipDao.getAll().firstOrNull()
        }
    }

    fun getPartnershipStatus(otherLover: LoverViewDto, partnership: Partnership?): RelationState? {
        if (otherLover.id == AppContext.INSTANCE.userId) {
            return RelationState.YOURSELF
        }
        return partnership?.let {
            if (otherLover.id == partnership.respondentId) {
                RelationState.YOU_REQUESTED_PARTNERSHIP
            } else if (otherLover.id == partnership.initiatorId) {
                RelationState.THEY_REQUESTED_PARTNERSHIP
            } else {
                null
            }
        }
    }

    suspend fun getPartnership(): Partnership? {
        return withContext(Dispatchers.IO) {
            if (metadataStore.isLoggedIn()) {
                val localPartnership = getPartnershipLocally()
                val call = partnershipApi.getLoverPartnership(metadataStore.getUser().id)
                val response = try {
                    call.execute()
                } catch (e: Exception) {
                    Log.e(tag, "getPartnership ERROR", e)
                    toaster.showNoServerToast()
                    return@withContext localPartnership
                }
                if (response.isSuccessful) {
                    val partnershipResponse: LoverPartnershipV2Response = response.body()!!
                    Log.i(tag, "partnershipResponse: $partnershipResponse")
                    val partnership = partnershipResponse.partnership
                    partnership?.let {
                        partnershipDao.insert(it)
                    } ?: run { partnershipDao.deleteAll() }
                    partnership
                } else {
                    toaster.showResponseError(response)
                    localPartnership
                }
            } else {
                null
            }
        }
    }

    suspend fun requestPartnership(respondentId: Long, activity: Activity): Partnership? {
        return withContext(Dispatchers.IO) {
            val localPartnership = getPartnershipLocally()
            if (localPartnership != null) {
                null
            } else {
                val initiatorId = metadataStore.getUser().id
                val call = partnershipApi.requestPartnership(
                    initiatorId, RequestPartnershipRequest(
                        initiatorId,
                        respondentId
                    )
                )
                val loadingBarShower = LoadingBarShower(activity)
                val response = try {
                    call.execute()
                } catch (e: Exception) {
                    loadingBarShower.onResponse()
                    toaster.showNoServerToast()
                    return@withContext null
                }
                if (response.isSuccessful) {
                    loadingBarShower.onResponse()
                    val partnership: Partnership = response.body()!!
                    partnershipDao.insert(partnership)
                    partnership
                } else {
                    loadingBarShower.onResponse()
                    toaster.showResponseError(response)
                    null
                }
            }
        }
    }

    suspend fun respondPartnership(request: RespondPartnershipRequest): Partnership? {
        return withContext(Dispatchers.IO) {
            val localPartnership = getPartnershipLocally()
            val initiatorId = metadataStore.getUser().id
            val call = partnershipApi.respondPartnership(initiatorId, request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localPartnership
            }
            if (response.isSuccessful) {
                val partnershipResponse = response.body()!!
                val partnership = partnershipResponse.partnership
                partnership?.let { partnershipDao.insert(it) }
                    ?: run { partnershipDao.deleteAll() }
                partnership
            } else {
                toaster.showResponseError(response)
                localPartnership
            }
        }
    }

    suspend fun cancelPartnershipRequest(request: CancelPartnershipRequest): Partnership? {
        return withContext(Dispatchers.IO) {
            val localPartnership = getPartnershipLocally()
            val initiatorId = metadataStore.getUser().id
            val call = partnershipApi.cancelPartnershipRequest(initiatorId, request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localPartnership
            }
            if (response.isSuccessful) {
                val partnershipResponse = response.body()!!
                val partnership = partnershipResponse.partnership
                partnership?.let { partnershipDao.insert(it) }
                    ?: run { partnershipDao.deleteAll() }
                partnership
            } else {
                toaster.showResponseError(response)
                localPartnership
            }
        }
    }

    suspend fun endPartnership(partnerLoverId: Long): Partnership? {
        return withContext(Dispatchers.IO) {
            val localPartnership = getPartnershipLocally()
            val loverId = metadataStore.getUser().id
            val call = partnershipApi.endPartnership(loverId, partnerLoverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localPartnership
            }
            if (response.isSuccessful) {
                val partnershipResponse = response.body()!!
                val partnership = partnershipResponse.partnership
                partnership?.let { partnershipDao.insert(it) }
                    ?: run { partnershipDao.deleteAll() }
                partnership
            } else {
                toaster.showResponseError(response)
                localPartnership
            }
        }
    }
}
