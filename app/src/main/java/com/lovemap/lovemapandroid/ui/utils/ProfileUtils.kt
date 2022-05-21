package com.lovemap.lovemapandroid.ui.utils

import android.widget.ProgressBar
import android.widget.TextView
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object ProfileUtils {

    fun setRanks(
        points: Int,
        currentRank: TextView,
        pointsToNextLevel: TextView,
        progressBar: ProgressBar
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
                        1
                    }
                }
                val rank = rankList[levelIndex - 1]
                val nextRank = rankList[levelIndex]
                // TODO: translation
                currentRank.text = rank.nameEN
                pointsToNextLevel.text = nextRank.pointsNeeded.toString()
                progressBar.min = 0
                progressBar.max = nextRank.pointsNeeded - rank.pointsNeeded
                progressBar.setProgress(points - rank.pointsNeeded, true)
            }
        }

    }
}