package com.lovemap.lovemapandroid.service

import android.util.Log
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
    private val tag = "GeoLocationService"

    suspend fun getAndFetchCities(): Cities? {
        return withContext(Dispatchers.IO) {
            if (metadataStore.isCitiesStored()) {
                val localCities: Cities = metadataStore.getCities()
                val serverCities: Cities? = fetchCities()
                val cities: Cities = serverCities ?: localCities
                cities
            } else {
                fetchCities()
            }
        }
    }

    suspend fun fetchCities(): Cities? {
        return withContext(Dispatchers.IO) {
            val call = geoLocationApi.getCities()
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val cities = response.body()!!
                    metadataStore.saveCountries(Countries(cities.cities.map { it.country }.toSet()))
                    metadataStore.saveCities(cities)
                    cities
                } else {
                    Log.e(tag, "Failed to fetch cities. Response: [$response]")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch cities.", e)
                null
            }
        }
    }


    suspend fun getAndFetchCountries(): Countries? {
        return withContext(Dispatchers.IO) {
            if (metadataStore.isCountriesStored()) {
                val localCountries: Countries = metadataStore.getCountries()
                val serverCountries: Countries? = fetchCountries()
                val countries: Countries = serverCountries ?: localCountries
                countries
            } else {
                fetchCountries()
            }
        }
    }

    suspend fun fetchCountries(): Countries? {
        return withContext(Dispatchers.IO) {
            val call = geoLocationApi.getCountries()
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val countries = response.body()!!
                    metadataStore.saveCountries(Countries(countries.countries))
                    countries
                } else {
                    Log.e(tag, "Failed to fetch countries. Response: [$response]")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch countries.", e)
                null
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