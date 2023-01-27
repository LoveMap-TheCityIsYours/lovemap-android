package com.lovemap.lovemapandroid.ui.utils

import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.max

object ProfileUtils {

    private const val TAG = "ProfileUtils"

    fun setPublicPrivateProfileImage(public: Boolean, imageView: ImageView, textView: TextView) {
        try {
            val context = imageView.context
            if (public) {
                Glide.with(context)
                    .load(R.drawable.ic_baseline_public_24)
                    .into(imageView)
                textView.text = context.getString(R.string.public_profile)
            } else {
                Glide.with(context)
                    .load(R.drawable.ic_baseline_public_off_24)
                    .into(imageView)
                textView.text = context.getString(R.string.private_profile)
            }
        } catch (e: Exception) {
            Log.i(TAG, "Glide fucked up", e)
        }
    }

    fun setRanks(
        points: Int,
        currentRank: TextView,
        animateText: Boolean = false,
        pointsToNextLevel: TextView? = null,
        progressBar: ProgressBar? = null
    ) {
        MainScope().launch {
            val ranks = AppContext.INSTANCE.loverService.getRanks()
            ranks?.let {
                val rankList = ranks.rankList
                var levelIndex = 1
                for ((index, rank) in rankList.withIndex()) {
                    levelIndex = if (rank.pointsNeeded > points) {
                        levelIndex = index
                        break
                    } else {
                        rankList.size
                    }
                }
                levelIndex = max(levelIndex, 1)
                val rank = rankList[levelIndex - 1]
                if (levelIndex < rankList.size) {
                    val nextRank = rankList[levelIndex]
                    if (animateText) {
                        pointsToNextLevel?.animate()?.alpha(0f)?.setDuration(250)?.withEndAction {
                            pointsToNextLevel.text = nextRank.pointsNeeded.toString()
                            pointsToNextLevel.animate().alpha(1f).duration = 250
                        }
                    } else {
                        pointsToNextLevel?.text = nextRank.pointsNeeded.toString()
                    }
                    progressBar?.max = nextRank.pointsNeeded - rank.pointsNeeded
                    progressBar?.setProgress(points - rank.pointsNeeded, true)
                } else {
                    if (animateText) {
                        pointsToNextLevel?.animate()?.alpha(0f)?.setDuration(250)?.withEndAction {
                            pointsToNextLevel.text = AppContext.INSTANCE.getString(R.string.max_level_reached)
                            pointsToNextLevel.animate().alpha(1f).duration = 250
                        }
                    } else {
                        pointsToNextLevel?.text = AppContext.INSTANCE.getString(R.string.max_level_reached)
                    }
                    progressBar?.max = points
                    progressBar?.setProgress(points, true)
                }
                if (animateText) {
                    currentRank.animate().alpha(0f).setDuration(250).withEndAction {
                        // TODO: translation
                        currentRank.text = rank.nameEN
                        currentRank.animate().alpha(1f).duration = 250
                    }
                } else {
                    currentRank.text = rank.nameEN
                }
                progressBar?.min = 0
            }
        }

    }
}