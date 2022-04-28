package com.smackmap.smackmapandroid.data.smackspot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotAvailabilityApiStatus
import java.time.LocalTime

@Entity(tableName = "smack_spot")
data class SmackSpot(
    @PrimaryKey val id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val longitude: Double,
    @ColumnInfo val latitude: Double,
    @ColumnInfo val description: String,
    @ColumnInfo val averageRating: Double?,
    @ColumnInfo val numberOfReports: Int,
    @ColumnInfo var customAvailability: String?,
    @ColumnInfo var availability: SmackSpotAvailabilityApiStatus,
    @ColumnInfo var averageDanger: Double?,
    @ColumnInfo var numberOfRatings: Int,
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