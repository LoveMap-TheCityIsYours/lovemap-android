package com.lovemap.lovemapandroid.ui.utils

import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.relation.RelationStatusDto
import com.lovemap.lovemapandroid.config.AppContext
import java.time.Instant

fun partnersFromRelations(relations: List<LoverViewDto>) =
    relations.filter { isPartner(it) }

fun isPartner(it: LoverViewDto) =
    it.relation == RelationStatusDto.PARTNER

fun pixelToDp(pixel: Float): Float = pixel / AppContext.INSTANCE.displayDensity

fun Instant.toApiString(): String {
    return "$epochSecond.$nano"
}

fun dpToPixel(dp: Float): Float = dp * AppContext.INSTANCE.displayDensity
