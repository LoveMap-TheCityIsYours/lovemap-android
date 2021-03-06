package com.lovemap.lovemapandroid.api.lovespot

import java.time.LocalTime

enum class Availability {
    ALL_DAY, NIGHT_ONLY
}

enum class Type {
    PUBLIC_SPACE,
    SWINGER_CLUB,
    CRUISING_SPOT,
    SEX_BOOTH,
    NIGHT_CLUB,
    OTHER_VENUE;
}

data class CreateLoveSpotRequest(
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val description: String,
    val customAvailability: Pair<LocalTime, LocalTime>?,
    val availability: Availability,
    val type: Type = Type.PUBLIC_SPACE,
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
    val availability: Availability,
    val type: Type,
    val customAvailability: Pair<LocalTime, LocalTime>? = null
)
