package com.lovemap.lovemapandroid.api.lovespot.photo

data class LoveSpotPhoto(
    val loveSpotId: Long,
    val reviewId: Long?,
    val likes: Int,
    val dislikes: Int,
    val url: String
)