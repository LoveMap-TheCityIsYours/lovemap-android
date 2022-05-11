package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.api.partnership.LoverPartnershipsResponse
import com.lovemap.lovemapandroid.api.partnership.PartnershipApi
import com.lovemap.lovemapandroid.api.partnership.RequestPartnershipRequest
import com.lovemap.lovemapandroid.api.partnership.RespondPartnershipRequest
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao
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
        }
    }

    suspend fun requestPartnership(respondentId: Long): Partnership? {
        return withContext(Dispatchers.IO) {
            val call = partnershipApi.requestPartnership(
                RequestPartnershipRequest(
                    metadataStore.getUser().id,
                    respondentId
                )
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(e.message!!)
                return@withContext null
            }
            if (response.isSuccessful) {
                val partnership = response.body()!!
                partnershipDao.insert(partnership)
                partnership
            } else {
                toaster.showToast(response.getErrorMessages().toString())
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