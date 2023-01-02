package com.lovemap.lovemapandroid.api.lovespot.photo

import com.lovemap.lovemapandroid.utils.instantOfApiString
import java.time.Instant

data class LoveSpotPhoto(
    val id: Long,
    val loveSpotId: Long,
    val reviewId: Long?,
    val uploadedBy: Long,
    val uploadedAt: String,
    val likes: Int,
    val likers: Set<Long>,
    val dislikes: Int,
    val dislikers: Set<Long>,
    val url: String
) {
    fun getUploadedAt(): Instant {
        return instantOfApiString(uploadedAt)
    }
}