package com.lovemap.lovemapandroid.data.metadata

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.GsonBuilder
import com.lovemap.lovemapandroid.api.geolocation.Cities
import com.lovemap.lovemapandroid.api.geolocation.Countries
import com.lovemap.lovemapandroid.api.lover.LoverRanks
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotRisks
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.utils.ROLE_ADMIN
import com.lovemap.lovemapandroid.utils.objectMapper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.nio.charset.StandardCharsets

class MetadataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MetadataStore")
    private val gson = GsonBuilder().create()
    private val currentAppMetadataVersion = 2
    private val currentTouVersion = 2

    private val metadataVersionKey = intPreferencesKey("metadataVersion")
    private val touAcceptedKey = intPreferencesKey("touAccepted")

    private val userKey = stringPreferencesKey("user")
    private val loverKey = stringPreferencesKey("lover")
    private val risksKey = stringPreferencesKey("risks")
    private val ranksKey = stringPreferencesKey("ranks")
    private val citiesKey = stringPreferencesKey("cities")
    private val countriesKey = stringPreferencesKey("countries")

    suspend fun checkSanity(): Boolean {
        return if (isMetadataVersionStored()) {
            getMetadataVersion() == currentAppMetadataVersion
        } else {
            false
        }
    }

    suspend fun updateMetadataVersion() {
        context.dataStore.edit { dataStore ->
            dataStore[metadataVersionKey] = currentAppMetadataVersion
        }
    }

    private suspend fun getMetadataVersion(): Int = context.dataStore.data.map { dataStore ->
        dataStore[metadataVersionKey]
    }.first()!!

    suspend fun login(user: LoggedInUser): LoggedInUser {
        saveUser(user)
        updateMetadataVersion()
        AppContext.INSTANCE.onLogin()
        return user
    }

    suspend fun saveUser(user: LoggedInUser): LoggedInUser {
        context.dataStore.edit { dataStore ->
            dataStore[userKey] = gson.toJson(user)
        }
        return user
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[userKey] != null
        }.first()
    }

    suspend fun isTouAccepted(): Boolean {
        return runCatching { context.dataStore.data.map { dataStore ->
            dataStore[touAcceptedKey] == currentTouVersion
        }.first() }.getOrNull() ?: false
    }

    suspend fun setTouAccepted(accepted: Boolean) {
        context.dataStore.edit { dataStore ->
            dataStore[touAcceptedKey] = if (accepted) {
                currentTouVersion
            } else {
                0
            }
        }
    }

    suspend fun getUser(): LoggedInUser {
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[userKey], LoggedInUser::class.java)
        }.first()
    }

    fun isAdmin(user: LoggedInUser): Boolean {
        val base64Payload = user.jwt.substringBeforeLast(".").substringAfter(".")
        val payloadBytes = Base64.decode(base64Payload, Base64.DEFAULT)
        val payloadJson: JsonNode =
            objectMapper.readTree(String(payloadBytes, StandardCharsets.UTF_8))
        return payloadJson["roles"].textValue().contains(ROLE_ADMIN)
    }

    suspend fun saveLover(lover: LoverRelationsDto): LoverRelationsDto {
        context.dataStore.edit { dataStore ->
            dataStore[loverKey] = gson.toJson(lover)
        }
        if (isLoggedIn()) {
            val loggedInUser = getUser()
            saveUser(LoggedInUser.of(lover, loggedInUser.jwt))
        }
        return lover
    }

    suspend fun isLoverStored(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[loverKey] != null
        }.first()
    }

    suspend fun getLover(): LoverRelationsDto {
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[loverKey], LoverRelationsDto::class.java)
        }.first()
    }

    suspend fun saveRisks(risks: LoveSpotRisks): LoveSpotRisks {
        context.dataStore.edit { dataStore ->
            dataStore[risksKey] = gson.toJson(risks)
        }
        return risks
    }

    suspend fun getRisks(): LoveSpotRisks {
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[risksKey], LoveSpotRisks::class.java)
        }.first()
    }

    suspend fun isRisksStored(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[risksKey] != null
        }.first()
    }

    suspend fun saveRanks(ranks: LoverRanks): LoverRanks {
        context.dataStore.edit { dataStore ->
            dataStore[ranksKey] = gson.toJson(ranks)
        }
        return ranks
    }

    suspend fun getRanks(): LoverRanks {
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[ranksKey], LoverRanks::class.java)
        }.first()
    }

    suspend fun isRanksStored(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[ranksKey] != null
        }.first()
    }

    suspend fun saveCities(cities: Cities): Cities {
        context.dataStore.edit { dataStore ->
            dataStore[citiesKey] = gson.toJson(cities)
        }
        return cities
    }

    suspend fun getCities(): Cities {
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[citiesKey], Cities::class.java)
        }.first()
    }

    suspend fun isCitiesStored(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[citiesKey] != null
        }.first()
    }

    suspend fun isMetadataVersionStored(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[metadataVersionKey] != null
        }.first()
    }

    suspend fun saveCountries(countries: Countries): Countries {
        context.dataStore.edit { dataStore ->
            dataStore[countriesKey] = gson.toJson(countries)
        }
        return countries
    }

    suspend fun getCountries(): Countries {
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[countriesKey], Countries::class.java)
        }.first()
    }

    suspend fun isCountriesStored(): Boolean {
        return context.dataStore.data.map { dataStore ->
            dataStore[countriesKey] != null
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