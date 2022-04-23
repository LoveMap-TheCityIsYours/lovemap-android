package com.smackmap.smackmapandroid.service.smacker

import android.content.Context
import com.smackmap.smackmapandroid.api.smacker.SmackerApi
import com.smackmap.smackmapandroid.api.smacker.SmackerDto
import com.smackmap.smackmapandroid.api.smacker.SmackerRelationsDto
import com.smackmap.smackmapandroid.api.smacker.SmackerViewDto
import com.smackmap.smackmapandroid.data.UserDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmackerService(
    private val smackerApi: SmackerApi,
    private val userDataStore: UserDataStore,
    private val context: Context
) {
    suspend fun getById(): SmackerRelationsDto {
        return withContext(Dispatchers.IO) {
            val loggedInUser = userDataStore.get()
            val call = smackerApi.getById(loggedInUser.id)
            val response = call.execute()
            response.body()!!
        }
    }

    suspend fun generateLink(): SmackerDto {
        TODO()
    }

    suspend fun deleteLink(): SmackerDto {
        TODO()
    }

    suspend fun getByLink(smackerLink: String): SmackerViewDto {
        TODO()
    }
}