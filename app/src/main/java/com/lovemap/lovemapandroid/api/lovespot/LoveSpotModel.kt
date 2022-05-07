package com.lovemap.lovemapandroid.api.lovespot

import java.time.LocalTime

enum class LoveSpotAvailabilityApiStatus {
    ALL_DAY, NIGHT_ONLY
}

data class CreateLoveSpotRequest(
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    var customAvailability: Pair<LocalTime, LocalTime>?,
    var availability: LoveSpotAvailabilityApiStatus
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
