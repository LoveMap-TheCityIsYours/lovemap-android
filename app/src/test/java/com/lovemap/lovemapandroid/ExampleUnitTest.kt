package com.lovemap.lovemapandroid

import com.lovemap.lovemapandroid.utils.EmojiUtils
import com.vdurmont.emoji.EmojiManager
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun flagEmojiTest() {
        val map = EmojiManager.getForTag("flag")
            .filter { it.tags.firstOrNull { tag -> tag != "flag" } != null }
            .associateBy({ it.tags.firstOrNull { tag -> tag != "flag" } }, { it.unicode })
        val treeMap = TreeMap(map)
        val usFlag = EmojiUtils.getFlagEmoji("United States ")
        val huFlag = EmojiUtils.getFlagEmoji("Hungary")
        val mmFlag = EmojiUtils.getFlagEmoji(" Myanmar (Burma) ")
        println(usFlag)
        println(huFlag)
        println(mmFlag)
        assertEquals(4, 2 + 2)
    }
}