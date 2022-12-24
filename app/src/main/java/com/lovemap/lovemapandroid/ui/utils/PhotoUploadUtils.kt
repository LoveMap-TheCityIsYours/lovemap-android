package com.lovemap.lovemapandroid.ui.utils

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext

object PhotoUploadUtils {
    suspend fun canUploadForSpot(loveSpotId: Long): Boolean {
        val loveSpot = AppContext.INSTANCE.loveSpotService.findLocally(loveSpotId)
        if (loveSpot != null) {
            if (loveSpot.addedBy == AppContext.INSTANCE.userId) {
                return true
            }
        }
        if (AppContext.INSTANCE.loveSpotReviewService.hasReviewedAlready(loveSpotId)) {
            return true
        }
        AppContext.INSTANCE.toaster.showToast(R.string.write_review_before_photo_upload)
        return false
    }
}