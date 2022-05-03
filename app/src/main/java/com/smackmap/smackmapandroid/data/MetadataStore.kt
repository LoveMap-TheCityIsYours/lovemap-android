package com.smackmap.smackmapandroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.smackmap.smackmapandroid.api.smacker.SmackerRanks
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotRisks
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.data.model.LoggedInUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MetadataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MetadataStore")
    private val gson = GsonBuilder().create()

    private val userKeyName = "user"
    private val riskKeyName = "risks"
    private val ranksKeyName = "ranks"

    suspend fun login(user: LoggedInUser): LoggedInUser {
        val userKey = stringPreferencesKey(userKeyName)
        context.dataStore.edit { dataStore ->
            dataStore[userKey] = gson.toJson(user)
        }
        AppContext.INSTANCE.onLogin()
        return user
    }

    suspend fun isLoggedIn(): Boolean {
        val userKey = stringPreferencesKey(userKeyName)
        return context.dataStore.data.map { dataStore ->
            dataStore[userKey] != null
        }.first()
    }

    suspend fun getUser(): LoggedInUser {
        val userKey = stringPreferencesKey(userKeyName)
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[userKey], LoggedInUser::class.java)
        }.first()
    }

    suspend fun deleteAll() {
        val userKey = stringPreferencesKey(userKeyName)
        context.dataStore.edit {
            it.clear()
        }
        val isLoggedIn = isLoggedIn()
        println(isLoggedIn)
    }

    suspend fun saveRisks(risks: SmackSpotRisks): SmackSpotRisks {
        val risksKey = stringPreferencesKey(riskKeyName)
        context.dataStore.edit { dataStore ->
            dataStore[risksKey] = gson.toJson(risks)
        }
        return risks
    }

    suspend fun getRisks(): SmackSpotRisks {
        val risksKey = stringPreferencesKey(riskKeyName)
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[risksKey], SmackSpotRisks::class.java)
        }.first()
    }

    suspend fun isRisksStored(): Boolean {
        val riskKeyName = stringPreferencesKey(riskKeyName)
        return context.dataStore.data.map { dataStore ->
            dataStore[riskKeyName] != null
        }.first()
    }

    suspend fun saveRanks(ranks: SmackerRanks): SmackerRanks {
        val ranksKey = stringPreferencesKey(ranksKeyName)
        context.dataStore.edit { dataStore ->
            dataStore[ranksKey] = gson.toJson(ranks)
        }
        return ranks
    }

    suspend fun getRanks(): SmackerRanks {
        val ranksKey = stringPreferencesKey(ranksKeyName)
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[ranksKey], SmackerRanks::class.java)
        }.first()
    }

    suspend fun isRanksStored(): Boolean {
        val ranksKey = stringPreferencesKey(ranksKeyName)
        return context.dataStore.data.map { dataStore ->
            dataStore[ranksKey] != null
        }.first()
    }
}