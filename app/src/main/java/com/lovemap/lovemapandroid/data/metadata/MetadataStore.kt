package com.lovemap.lovemapandroid.data.metadata

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.GsonBuilder
import com.lovemap.lovemapandroid.api.lover.LoverRanks
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotRisks
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.utils.ROLE_ADMIN
import com.lovemap.lovemapandroid.utils.objectMapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.nio.charset.StandardCharsets

class MetadataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MetadataStore")
    private val gson = GsonBuilder().create()

    private val userKeyName = "user"
    private val loverKeyName = "lover"
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

    fun isAdmin(user: LoggedInUser): Boolean {
        val base64Payload = user.jwt.substringBeforeLast(".").substringAfter(".")
        val payloadBytes = Base64.decode(base64Payload, Base64.DEFAULT)
        val payloadJson: JsonNode = objectMapper.readTree(String(payloadBytes, StandardCharsets.UTF_8))
        return payloadJson["roles"].textValue().contains(ROLE_ADMIN)
    }

    suspend fun saveLover(lover: LoverRelationsDto): LoverRelationsDto {
        val loverKey = stringPreferencesKey(loverKeyName)
        context.dataStore.edit { dataStore ->
            dataStore[loverKey] = gson.toJson(lover)
        }
        return lover
    }

    suspend fun isLoverStored(): Boolean {
        val loverKey = stringPreferencesKey(loverKeyName)
        return context.dataStore.data.map { dataStore ->
            dataStore[loverKey] != null
        }.first()
    }

    suspend fun getLover(): LoverRelationsDto {
        val loverKey = stringPreferencesKey(loverKeyName)
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[loverKey], LoverRelationsDto::class.java)
        }.first()
    }

    suspend fun saveRisks(risks: LoveSpotRisks): LoveSpotRisks {
        val risksKey = stringPreferencesKey(riskKeyName)
        context.dataStore.edit { dataStore ->
            dataStore[risksKey] = gson.toJson(risks)
        }
        return risks
    }

    suspend fun getRisks(): LoveSpotRisks {
        val risksKey = stringPreferencesKey(riskKeyName)
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[risksKey], LoveSpotRisks::class.java)
        }.first()
    }

    suspend fun isRisksStored(): Boolean {
        val riskKeyName = stringPreferencesKey(riskKeyName)
        return context.dataStore.data.map { dataStore ->
            dataStore[riskKeyName] != null
        }.first()
    }

    suspend fun saveRanks(ranks: LoverRanks): LoverRanks {
        val ranksKey = stringPreferencesKey(ranksKeyName)
        context.dataStore.edit { dataStore ->
            dataStore[ranksKey] = gson.toJson(ranks)
        }
        return ranks
    }

    suspend fun getRanks(): LoverRanks {
        val ranksKey = stringPreferencesKey(ranksKeyName)
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[ranksKey], LoverRanks::class.java)
        }.first()
    }

    suspend fun isRanksStored(): Boolean {
        val ranksKey = stringPreferencesKey(ranksKeyName)
        return context.dataStore.data.map { dataStore ->
            dataStore[ranksKey] != null
        }.first()
    }

    suspend fun deleteAll() {
        context.dataStore.edit {
            it.clear()
        }
        val isLoggedIn = isLoggedIn()
        println(isLoggedIn)
    }
}