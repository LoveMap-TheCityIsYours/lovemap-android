package com.lovemap.lovemapandroid.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.relation.RelationStatus
import com.lovemap.lovemapandroid.config.AppContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

const val AUTHORIZATION_HEADER = "Authorization"
const val X_CLIENT_ID_HEADER = "x-client-id"
const val X_CLIENT_SECRET_HEADER = "x-client-secret"

const val LINK_PREFIX_API_CALL = "https://api.lovemap.app/lover?uuid="
const val LINK_PREFIX_VISIBLE = "https://api.lovemap.app/join-us/lover?uuid="

const val IS_CLICKABLE = "isClickable"

const val ROLE_ADMIN = "ROLE_ADMIN"

val objectMapper = jacksonObjectMapper()

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

fun canEditLoveSpot(addedBy: Long) =
    AppContext.INSTANCE.userId == addedBy || AppContext.INSTANCE.isAdmin

