package com.lovemap.lovemapandroid.data.lovespot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import java.time.LocalTime

@Entity(
    tableName = "love_spot", indices = [
        Index(value = ["longitude", "latitude"])
    ]
)
data class LoveSpot(
    @PrimaryKey var id: Long,
    @ColumnInfo var name: String,
    @ColumnInfo(index = true) var longitude: Double,
    @ColumnInfo(index = true) var latitude: Double,
    @ColumnInfo var description: String,
    @ColumnInfo(index = true) var averageRating: Double?,
    @ColumnInfo var numberOfReports: Int,
    @ColumnInfo var customAvailability: String?,
    @ColumnInfo var availability: Availability,
    @ColumnInfo var averageDanger: Double?,
    @ColumnInfo var numberOfRatings: Int,
    @ColumnInfo var addedBy: Long,
    @ColumnInfo var numberOfPhotos: Int,
    @ColumnInfo var type: LoveSpotType = LoveSpotType.PUBLIC_SPACE,
) {
    fun readCustomAvailability(): Pair<LocalTime, LocalTime>? {
        customAvailability?.let {
            return gson.fromJson(it, object : TypeToken<Pair<LocalTime, LocalTime>>() {}.type)
        }
        return null
    }

    fun setCustomAvailability(fromTo: Pair<LocalTime, LocalTime>?) {
        if (fromTo != null) {
            customAvailability = gson.toJson(fromTo)
        }
    }

    companion object {
        private val gson = Gson()
    }
}