package com.lovemap.lovemapandroid.api.lovespot.report

data class LoveSpotReportRequest(
    val loverId: Long,
    val loveSpotId: Long,
    val reportText: String,
)

data class LoveSpotReportDto(
    val id: Long,
    val loverId: Long,
    val loveSpotId: Long,
    val reportText: String,
)