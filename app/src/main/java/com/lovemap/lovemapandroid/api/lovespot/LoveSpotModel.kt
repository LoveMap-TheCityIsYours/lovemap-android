package com.lovemap.lovemapandroid.api.lovespot

import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import java.time.LocalTime

enum class Availability {
    ALL_DAY, NIGHT_ONLY
}

enum class LoveSpotType {
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
    val type: LoveSpotType = LoveSpotType.PUBLIC_SPACE,
)

data class LoveSpotListRequest(
    val latFrom: Double,
    val longFrom: Double,
    val latTo: Double,
    val longTo: Double,
    val limit: Int
)

data class LoveSpotSearchRequest(
    val limit: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceInMeters: Int? = null,
    val locationName: String? = null,
    val typeFilter: List<LoveSpotType> = LoveSpotType.values().toList()
)

enum class ListOrdering {
    CLOSEST, TOP_RATED, RECENTLY_ACTIVE, POPULAR
}

enum class ListLocation {
    COORDINATE, CITY, COUNTRY
}

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
    val type: LoveSpotType,
    val customAvailability: Pair<LocalTime, LocalTime>? = null
)

data class RecommendationsRequest(
    val longitude: Double?,
    val latitude: Double?,
    val country: String,
    val typeFilter: List<LoveSpotType> = LoveSpotType.values().toList()
)

data class RecommendationsResponse(
    val topRatedSpots: List<LoveSpot>,
    val closestSpots: List<LoveSpot>,
    val recentlyActiveSpots: List<LoveSpot>,
    val popularSpots: List<LoveSpot>
) {
    companion object {
        fun empty(): RecommendationsResponse {
            return RecommendationsResponse(
                topRatedSpots = emptyList(),
                closestSpots = emptyList(),
                recentlyActiveSpots = emptyList(),
                popularSpots = emptyList()
            )
        }
    }
}
