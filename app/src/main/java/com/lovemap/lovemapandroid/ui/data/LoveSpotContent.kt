package com.lovemap.lovemapandroid.ui.data

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object LoveSpotContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<LoveSpotItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, LoveSpotItem> = HashMap()

    private val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createPlaceholderItem(i))
        }
    }

    private fun addItem(item: LoveSpotItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createPlaceholderItem(position: Int): LoveSpotItem {
        return LoveSpotItem(position.toString(), "Love Spot $position", makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class LoveSpotItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}