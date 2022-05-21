package com.lovemap.lovemapandroid.ui.utils

import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.relation.RelationStatus
import com.lovemap.lovemapandroid.config.AppContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

val timeZone = TimeZone.getDefault()
val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

fun partnersFromRelations(relations: List<LoverViewDto>) =
    relations.filter { isPartner(it) }

fun isPartner(it: LoverViewDto) =
    it.relation == RelationStatus.PARTNER

fun pixelToDp(pixel: Float): Float = pixel / AppContext.INSTANCE.displayDensity

fun Instant.toApiString(): String {
    return "$epochSecond.$nano"
}

fun Instant.toFormattedString(): String {
    val timeZone = TimeZone.getDefault()
    val zonedDateTime = this.atZone(timeZone.toZoneId())
    return zonedDateTime.format(dateTimeFormatter)
}

fun instantOfApiString(instantString: String): Instant {
    return Instant.ofEpochSecond(
        instantString.substringBefore(".").toLong(),
        instantString.substringAfter(".").toLong()
    )
}

fun dpToPixel(dp: Float): Float = dp * AppContext.INSTANCE.displayDensity
