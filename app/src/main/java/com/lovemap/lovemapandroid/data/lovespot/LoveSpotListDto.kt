package com.lovemap.lovemapandroid.data.lovespot

data class LoveSpotListDto(
    val loveSpots: List<LoveSpot>,
    val deletedIds: List<Long>
)
