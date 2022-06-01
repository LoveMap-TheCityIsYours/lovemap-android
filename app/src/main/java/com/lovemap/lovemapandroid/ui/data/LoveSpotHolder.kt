package com.lovemap.lovemapandroid.ui.data

import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot

data class LoveSpotHolder(
    var id: Long,
    var name: String,
    var longitude: Double,
    var latitude: Double,
    var description: String,
    var averageRating: Double?,
    var numberOfReports: Int,
    var customAvailability: String?,
    var availability: Availability,
    var averageDanger: Double?,
    var numberOfRatings: Int,
    var addedBy: Long,
) : Comparable<LoveSpotHolder> {
    companion object {
        fun of(loveSpot: LoveSpot): LoveSpotHolder {
            return LoveSpotHolder(
                id= loveSpot.id,
                name = loveSpot.name,
                longitude = loveSpot.longitude,
                latitude = loveSpot.latitude,
                description = loveSpot.description,
                averageDanger = loveSpot.averageDanger,
                numberOfReports = loveSpot.numberOfReports,
                customAvailability = loveSpot.customAvailability,
                availability = loveSpot.availability,
                averageRating = loveSpot.averageRating,
                numberOfRatings = loveSpot.numberOfRatings,
                addedBy = loveSpot.addedBy
            )
        }
    }

    override fun compareTo(other: LoveSpotHolder): Int {
        return (averageRating?.toInt() ?: 0) - (other.averageDanger?.toInt() ?: 0)
    }
}