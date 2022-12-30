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
    val dislikes: Int,
    val url: String
) {
    fun getUploadedAt(): Instant {
        return instantOfApiString(uploadedAt)
    }
}