package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.api.geolocation.Cities
import com.lovemap.lovemapandroid.api.geolocation.Countries
import com.lovemap.lovemapandroid.api.geolocation.GeoLocationApi
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeoLocationService(
    private val geoLocationApi: GeoLocationApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster
) {
    suspend fun getCities(): Cities? {
        return withContext(Dispatchers.IO) {
            val localCities: Cities? = if (metadataStore.isCitiesStored()) {
                metadataStore.getCities()
            } else {
                null
            }
            val call = geoLocationApi.getCities()
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val cities = response.body()!!
                    metadataStore.saveCountries(Countries(cities.cities.map { it.country }.toSet()))
                    metadataStore.saveCities(cities)
                } else {
                    toaster.showResponseError(response)
                    localCities
                }
            } catch (e: Exception) {
                toaster.showNoServerToast()
                localCities
            }
        }
    }

    suspend fun getCitiesLocally(): Set<String> = withContext(Dispatchers.IO) {
        if (metadataStore.isCitiesStored()) {
            metadataStore.getCities().cities.map { "${it.city}, ${it.country}" }.toSortedSet()
        } else {
            emptySet()
        }
    }

    suspend fun getCountriesLocally(): Set<String> = withContext(Dispatchers.IO) {
        if (metadataStore.isCountriesStored()) {
            metadataStore.getCountries().countries.toSortedSet()
        } else {
            emptySet()
        }
    }
}