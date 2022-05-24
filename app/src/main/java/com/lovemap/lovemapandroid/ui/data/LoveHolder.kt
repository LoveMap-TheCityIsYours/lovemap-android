package com.lovemap.lovemapandroid.ui.data

import android.content.Context
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.service.LoverService
import com.lovemap.lovemapandroid.ui.utils.instantOfApiString
import com.lovemap.lovemapandroid.ui.utils.toFormattedString

data class LoveHolder(
    val loveSpotId: Long,
    val name: String,
    val partner: String,
    val partnerId: Long?,
    val note: String,
    val happenedAt: String,
    val happenedAtLong: Long
) : Comparable<LoveHolder> {
    override fun compareTo(other: LoveHolder): Int {
        return (other.happenedAtLong - happenedAtLong).toInt()
    }

    companion object {
        suspend fun of(love: Love, loverService: LoverService, context: Context): LoveHolder {
            // TODO cache partners
            val partnerId = love.getPartnerId()
            val partner: LoverViewDto? = partnerId?.let {
                loverService.getOtherById(partnerId)
            }
            return LoveHolder(
                loveSpotId = love.loveSpotId,
                name = love.name,
                partner = partner?.userName ?: context.getString(R.string.not_app_user),
                partnerId = partner?.id,
                note = love.note ?: "",
                happenedAt = instantOfApiString(love.happenedAt).toFormattedString(),
                happenedAtLong = instantOfApiString(love.happenedAt).epochSecond
            )
        }
    }
}
