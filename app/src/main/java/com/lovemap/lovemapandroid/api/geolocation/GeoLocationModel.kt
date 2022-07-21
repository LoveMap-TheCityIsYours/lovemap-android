package com.lovemap.lovemapandroid.api.geolocation

data class Countries(
    val countries: Set<String>
)

data class Cities(
    val cities: Set<City>
)

data class City(
    val country: String,
    val city: String
)
