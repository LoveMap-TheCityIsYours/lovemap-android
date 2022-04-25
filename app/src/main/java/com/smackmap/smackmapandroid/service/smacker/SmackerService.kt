package com.smackmap.smackmapandroid.service.smacker

import android.os.Looper
import com.smackmap.smackmapandroid.api.smacker.SmackerApi
import com.smackmap.smackmapandroid.api.smacker.SmackerDto
import com.smackmap.smackmapandroid.api.smacker.SmackerRelationsDto
import com.smackmap.smackmapandroid.api.smacker.SmackerViewDto
import com.smackmap.smackmapandroid.config.AUTHORIZATION_HEADER
import com.smackmap.smackmapandroid.data.UserDataStore
import com.smackmap.smackmapandroid.service.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmackerService(
    private val smackerApi: SmackerApi,
    private val userDataStore: UserDataStore,
    private val toaster: Toaster,
) {
    private val mainLooper = Looper.getMainLooper()

    suspend fun getById(): SmackerRelationsDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = userDataStore.get()
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
                toaster.showNoServerToast()
                null
            }
        }
    }

    suspend fun generateLink(): SmackerDto? {
        return withContext(Dispatchers.IO) {
            val loggedInUser = userDataStore.get()
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
            val loggedInUser = userDataStore.get()
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
}