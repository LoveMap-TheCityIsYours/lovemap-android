package com.lovemap.lovemapandroid.ui.data

import android.content.Context
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.utils.instantOfApiString
import com.lovemap.lovemapandroid.utils.toFormattedDateTime

data class LoveHolder(
    val id: Long,
    val loveSpotId: Long,
    val name: String,
    val partner: String,
    val partnerId: Long?,
    val note: String,
    val happenedAt: String,
    val happenedAtLong: Long,
    val number: Int,
) : Comparable<LoveHolder> {
    var expanded = false

    override fun compareTo(other: LoveHolder): Int {
        return (other.happenedAtLong - happenedAtLong).toInt()
    }

    companion object {
        fun of(love: Love, number: Int, context: Context): LoveHolder {
            val partnerId = love.getPartnerId()
            return LoveHolder(
                id = love.id,
                loveSpotId = love.loveSpotId,
                name = love.name,
                partner = love.partnerName ?: context.getString(R.string.not_app_user),
                partnerId = partnerId,
                note = love.note ?: "",
                happenedAt = instantOfApiString(love.happenedAt).toFormattedDateTime(),
                happenedAtLong = instantOfApiString(love.happenedAt).epochSecond,
                number = number,
            )
        }
    }
}
