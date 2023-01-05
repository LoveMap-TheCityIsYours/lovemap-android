package com.lovemap.lovemapandroid.ui.data

import com.javadocmd.simplelatlng.LatLng
import com.javadocmd.simplelatlng.LatLngTool
import com.javadocmd.simplelatlng.util.LengthUnit
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils

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
    var type: LoveSpotType,
    var averageDanger: Double?,
    var numberOfRatings: Int,
    var addedBy: Long,
    var distanceKm: Double? = null,
) : Comparable<LoveSpotHolder> {
    companion object {
        fun of(loveSpot: LoveSpot): LoveSpotHolder {
            return LoveSpotHolder(
                id = loveSpot.id,
                name = loveSpot.name,
                longitude = loveSpot.longitude,
                latitude = loveSpot.latitude,
                description = loveSpot.description,
                averageDanger = loveSpot.averageDanger,
                numberOfReports = loveSpot.numberOfReports,
                customAvailability = loveSpot.customAvailability,
                availability = loveSpot.availability,
                type = loveSpot.type,
                averageRating = loveSpot.averageRating,
                numberOfRatings = loveSpot.numberOfRatings,
                addedBy = loveSpot.addedBy,
                distanceKm = LoveSpotUtils.calculateDistance(loveSpot)
            )
        }
    }

    override fun compareTo(other: LoveSpotHolder): Int {
        return (averageRating?.toInt() ?: 0) - (other.averageDanger?.toInt() ?: 0)
    }
}