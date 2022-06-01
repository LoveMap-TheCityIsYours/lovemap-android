package com.lovemap.lovemapandroid.api.lovespot

import java.time.LocalTime

enum class Availability {
    ALL_DAY, NIGHT_ONLY
}

data class CreateLoveSpotRequest(
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    var customAvailability: Pair<LocalTime, LocalTime>?,
    var availability: Availability
)

data class LoveSpotSearchRequest(
    val latFrom: Double,
    val longFrom: Double,
    val latTo: Double,
    val longTo: Double,
    val limit: Int
)

data class LoveSpotRisks(val levels: Int, val riskList: List<Risk>) {
    data class Risk(
        val level: Int,
        val nameEN: String,
        val nameHU: String
    )
}

data class UpdateLoveSpotRequest(
    val name: String? = null,
    val description: String? = null,
    var customAvailability: Pair<LocalTime, LocalTime>? = null,
    var availability: Availability? = null
)
