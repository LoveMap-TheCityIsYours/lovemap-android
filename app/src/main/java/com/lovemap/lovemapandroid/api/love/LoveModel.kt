package com.lovemap.lovemapandroid.api.love

data class CreateLoveRequest(
    val name: String,
    val loveSpotId: Long,
    val loverId: Long,
    val happenedAt: String? = null,
    val loverPartnerId: Long? = null,
    val note: String? = null,
)

data class UpdateLoveRequest(
    val name: String? = null,
    val happenedAt: String? = null,
    val loverPartnerId: Long? = null,
    val note: String? = null,
)
