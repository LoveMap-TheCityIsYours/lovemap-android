package com.smackmap.smackmapandroid.api.smackspot

import java.time.LocalTime

enum class SmackSpotAvailabilityApiStatus {
    ALL_DAY, NIGHT_ONLY
}

data class CreateSmackSpotRequest(
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    var customAvailability: Pair<LocalTime, LocalTime>?,
    var availability: SmackSpotAvailabilityApiStatus
)

data class SmackSpotSearchRequest(
    val latFrom: Double,
    val longFrom: Double,
    val latTo: Double,
    val longTo: Double,
    val limit: Int
)

data class SmackSpotRisks(val levels: Int, val riskList: List<Risk>) {
    data class Risk(
        val level: Int,
        val nameEN: String,
        val nameHU: String
    )
}
