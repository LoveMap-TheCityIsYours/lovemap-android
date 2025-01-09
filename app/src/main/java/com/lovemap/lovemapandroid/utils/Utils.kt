package com.lovemap.lovemapandroid.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.lover.relation.RelationStatus
import com.lovemap.lovemapandroid.config.AppContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.*

const val AUTHORIZATION_HEADER = "Authorization"
const val X_CLIENT_ID_HEADER = "x-client-id"
const val X_CLIENT_SECRET_HEADER = "x-client-secret"

const val LINK_PREFIX_API_CALL = "https://lovemap.app/lover?uuid="
const val LINK_PREFIX_VISIBLE = "https://lovemap.app/join-us/lover?uuid="

const val IS_CLICKABLE = "isClickable"

const val ROLE_ADMIN = "ROLE_ADMIN"

val objectMapper = jacksonObjectMapper()

val timeZone: TimeZone = TimeZone.getDefault()

val locale: Locale = Locale.forLanguageTag("HU")
val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
    DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.MEDIUM, FormatStyle.MEDIUM, Chronology.ofLocale(locale), locale
    )
)

fun partnersFromRelations(relations: List<LoverViewDto>) =
    relations.filter { isPartner(it) }

fun isPartner(it: LoverViewDto) =
    it.relation == RelationStatus.PARTNER

fun pixelToDp(pixel: Float): Float = pixel / AppContext.INSTANCE.displayDensity

fun Instant.toApiString(): String {
    return "$epochSecond.$nano"
}

fun Instant.toFormattedDateTime(): String {
    val timeZone = TimeZone.getDefault()
    val zonedDateTime = this.atZone(timeZone.toZoneId())
    return zonedDateTime.format(dateTimeFormatter)
}

fun Instant.toFormattedDate(): String {
    val timeZone = TimeZone.getDefault()
    val localDate = this.atZone(timeZone.toZoneId()).toLocalDate()
    return localDate.format(DateTimeFormatter.ofPattern("yyyy MMM dd"))
}

fun LocalDateTime.toInstant(): Instant {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(this)
    return toInstant(zoneOffset)
}

fun LocalDateTime.ofInstant(instant: Instant): LocalDateTime {
    return LocalDateTime.ofInstant(instant, timeZone.toZoneId())
}

fun instantOfApiString(instantString: String): Instant {
    return Instant.ofEpochSecond(
        instantString.substringBefore(".").toLong(),
        instantString.substringAfter(".").toLong()
    )
}

fun dpToPixel(dp: Float): Float = dp * AppContext.INSTANCE.displayDensity

fun canEditLoveSpot(addedBy: Long) =
    AppContext.INSTANCE.userId == addedBy || AppContext.INSTANCE.isAdmin

