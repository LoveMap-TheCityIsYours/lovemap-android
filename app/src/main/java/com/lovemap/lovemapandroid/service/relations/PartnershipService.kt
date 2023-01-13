package com.lovemap.lovemapandroid.service.relations

import android.app.Activity
import com.lovemap.lovemapandroid.api.ErrorMessage
import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.partnership.LoverPartnershipsResponse
import com.lovemap.lovemapandroid.api.partnership.PartnershipApi
import com.lovemap.lovemapandroid.api.partnership.RequestPartnershipRequest
import com.lovemap.lovemapandroid.api.partnership.RespondPartnershipRequest
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

    suspend fun getPartnershipLocally(): Partnership? {
        return withContext(Dispatchers.IO) {
            partnershipDao.getAll().firstOrNull()
        }
    }

    fun getPartnershipStatus(otherLover: LoverViewDto, partnership: Partnership?): RelationState? {
        return partnership?.let {
            if (otherLover.id == AppContext.INSTANCE.userId) {
                RelationState.YOURSELF
            } else if (otherLover.id == partnership.respondentId) {
                RelationState.YOU_REQUESTED_PARTNERSHIP
            } else if (otherLover.id == partnership.initiatorId) {
                RelationState.THEY_REQUESTED_PARTNERSHIP
            } else {
                null
            }
        }
    }

    suspend fun getPartnershipStatus(otherLover: LoverViewDto): RelationState? {
        return getPartnership()?.let { partnership ->
            if (otherLover.id == partnership.initiatorId) {
                RelationState.THEY_REQUESTED_PARTNERSHIP
            } else if (otherLover.id == partnership.respondentId) {
                RelationState.YOU_REQUESTED_PARTNERSHIP
            } else {
                null
            }
        }
    }

    suspend fun getPartnership(): Partnership? {
        return withContext(Dispatchers.IO) {
            if (metadataStore.isLoggedIn()) {
                val localPartnership = getPartnershipLocally()
                val call = partnershipApi.getLoverPartnerships(metadataStore.getUser().id)
                val response = try {
                    call.execute()
                } catch (e: Exception) {
                    toaster.showNoServerToast()
                    return@withContext localPartnership
                }
                if (response.isSuccessful) {
                    val partnershipsResponse: LoverPartnershipsResponse = response.body()!!
                    val partnerships = partnershipsResponse.partnerships
                    partnershipDao.insert(*partnerships.toTypedArray())
                    partnerships.firstOrNull()
                } else {
                    toaster.showNoServerToast()
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
                val call = partnershipApi.requestPartnership(
                    RequestPartnershipRequest(
                        metadataStore.getUser().id,
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
                    val errorMessage: ErrorMessage = response.getErrorMessages()[0]
                    toaster.showToast(errorMessage.translatedString(AppContext.INSTANCE.applicationContext))
                    null
                }
            }
        }
    }

    suspend fun respondPartnership(request: RespondPartnershipRequest): Partnership? {
        return withContext(Dispatchers.IO) {
            val localPartnership = getPartnershipLocally()
            val call = partnershipApi.respondPartnership(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localPartnership
            }
            if (response.isSuccessful) {
                val partnershipsResponse = response.body()!!
                val partnerships = partnershipsResponse.partnerships
                partnershipDao.insert(*partnerships.toTypedArray())
                partnerships.firstOrNull()
            } else {
                toaster.showNoServerToast()
                localPartnership
            }
        }
    }
}