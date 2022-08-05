package com.lovemap.lovemapandroid.service

import android.app.Activity
import com.lovemap.lovemapandroid.api.ErrorMessage
import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.api.partnership.PartnershipApi
import com.lovemap.lovemapandroid.api.partnership.RequestPartnershipRequest
import com.lovemap.lovemapandroid.api.partnership.RespondPartnershipRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PartnershipService(
    private val partnershipApi: PartnershipApi,
    private val partnershipDao: PartnershipDao,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {

    suspend fun getPartnerships(): List<Partnership> {
        return withContext(Dispatchers.IO) {
            if (metadataStore.isLoggedIn()) {
                val localPartnerships = partnershipDao.getAll()
                val call = partnershipApi.getLoverPartnerships(metadataStore.getUser().id)
                val response = try {
                    call.execute()
                } catch (e: Exception) {
                    toaster.showNoServerToast()
                    return@withContext localPartnerships
                }
                if (response.isSuccessful) {
                    val partnershipsResponse = response.body()!!
                    val partnerships = partnershipsResponse.partnerships
                    partnershipDao.insert(*partnerships.toTypedArray())
                    partnerships
                } else {
                    toaster.showNoServerToast()
                    localPartnerships
                }
            } else {
                emptyList()
            }
        }
    }

    suspend fun requestPartnership(respondentId: Long, activity: Activity): Partnership? {
        return withContext(Dispatchers.IO) {
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
                val partnership = response.body()!!
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

    suspend fun respondPartnership(request: RespondPartnershipRequest): List<Partnership> {
        return withContext(Dispatchers.IO) {
            val localPartnerships = partnershipDao.getAll()
            val call = partnershipApi.respondPartnership(request)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext localPartnerships
            }
            if (response.isSuccessful) {
                val partnershipsResponse = response.body()!!
                val partnerships = partnershipsResponse.partnerships
                partnershipDao.insert(*partnerships.toTypedArray())
                partnerships
            } else {
                toaster.showNoServerToast()
                localPartnerships
            }
        }
    }
}