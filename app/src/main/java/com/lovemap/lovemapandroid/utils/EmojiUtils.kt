package com.lovemap.lovemapandroid.utils

import com.lovemap.lovemapandroid.config.AppContext
import com.vdurmont.emoji.EmojiManager

object EmojiUtils {

    private val flagEmojiMap: Map<String?, String> =
        EmojiManager.getForTag("flag")
            .associateBy({ it.tags.firstOrNull { tag -> tag != "flag" } }, { it.unicode })

    private val flagForGlobal: String = EmojiManager.getForAlias("earth_americas").unicode

    fun getFlagEmoji(country: String): String? {
        if (country.equals(AppContext.INSTANCE.countryForGlobal, true)) {
            return flagForGlobal
        }
        val normalized = normalize(country)
        return flagEmojiMap[normalized]
    }

    private fun normalize(country: String): String {
        val normalized = country.lowercase().trim()
        return if (normalized == "united states") {
            return "united states of america"
        } else {
            normalized
        }
    }
}