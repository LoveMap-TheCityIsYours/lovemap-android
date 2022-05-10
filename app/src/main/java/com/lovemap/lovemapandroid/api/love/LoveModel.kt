package com.lovemap.lovemapandroid.api.love

data class CreateLoveRequest(
    val name: String,
    val loveSpotId: Long,
    val loverId: Long,
    val happenedAt: String? = null,
    val loverPartnerId: Long? = null,
    val note: String? = null,
)
