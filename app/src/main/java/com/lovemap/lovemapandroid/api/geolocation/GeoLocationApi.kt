package com.lovemap.lovemapandroid.api.geolocation

import retrofit2.Call
import retrofit2.http.GET

interface GeoLocationApi {

    @GET("/geolocations/cities")
    fun getCities(): Call<Cities>

    @GET("/geolocations/countries")
    fun getCountries(): Call<Countries>
}