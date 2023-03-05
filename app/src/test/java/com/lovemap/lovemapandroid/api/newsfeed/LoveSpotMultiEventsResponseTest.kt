package com.lovemap.lovemapandroid.api.newsfeed

import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType.PUBLIC_SPACE
import com.lovemap.lovemapandroid.utils.toApiString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

internal class LoveSpotMultiEventsResponseTest {

    @Test
    fun getLatestEventLover1() {
        val now = Instant.now().toApiString()
        val nowMinus1 = Instant.now().minus(Duration.ofHours(1)).toApiString()
        val nowMinus2 = Instant.now().minus(Duration.ofHours(2)).toApiString()
        val nowMinus3 = Instant.now().minus(Duration.ofHours(3)).toApiString()
        val nowMinus4 = Instant.now().minus(Duration.ofHours(4)).toApiString()

        val lover1 = LoverViewWithoutRelationDto(1, "a", 1, 1, 1, 1, 1, now, true, null)
        val lover2 = LoverViewWithoutRelationDto(2, "b", 1, 1, 1, 1, 1, now, true, null)
        val lover3 = LoverViewWithoutRelationDto(3, "c", 1, 1, 1, 1, 1, now, true, null)
        val multiEvents = LoveSpotMultiEventsResponse(
            lovers = listOf(
                lover1,
                lover2,
                lover3,
            ),
            loveSpot = LoveSpotNewsFeedResponse(
                1, now, 1, "a", "a", PUBLIC_SPACE, "a"
            ),
            loves = listOf(
                LoveNewsFeedResponse(1, "a", 1, 1, nowMinus1, null, null),
                LoveNewsFeedResponse(2, "b", 1, 2, nowMinus2, null, null),
            ),
            photos = listOf(
                LoveSpotPhotoNewsFeedResponse(1, 1, 3, nowMinus3, "", "", null, 1, 1),
                LoveSpotPhotoNewsFeedResponse(2, 1, 1, nowMinus4, "", "", null, 1, 1),
            ),
            loveSpotAddedHere = false
        )

        assertEquals(lover1, multiEvents.getLatestEventLover())
    }

    @Test
    fun getLatestEventLover2() {
        val now = Instant.now().toApiString()
        val nowMinus1 = Instant.now().minus(Duration.ofHours(1)).toApiString()
        val nowMinus2 = Instant.now().minus(Duration.ofHours(2)).toApiString()
        val nowMinus3 = Instant.now().minus(Duration.ofHours(3)).toApiString()
        val nowMinus4 = Instant.now().minus(Duration.ofHours(4)).toApiString()

        val lover1 = LoverViewWithoutRelationDto(1, "a", 1, 1, 1, 1, 1, now, true, null)
        val lover2 = LoverViewWithoutRelationDto(2, "b", 1, 1, 1, 1, 1, now, true, null)
        val lover3 = LoverViewWithoutRelationDto(3, "c", 1, 1, 1, 1, 1, now, true, null)
        val multiEvents = LoveSpotMultiEventsResponse(
            lovers = listOf(
                lover1,
                lover2,
                lover3,
            ),
            loveSpot = LoveSpotNewsFeedResponse(
                1, nowMinus4, 1, "a", "a", PUBLIC_SPACE, "a"
            ),
            loves = listOf(
                LoveNewsFeedResponse(1, "a", 1, 1, nowMinus1, null, null),
                LoveNewsFeedResponse(2, "b", 1, 2, now, null, null),
            ),
            photos = listOf(
                LoveSpotPhotoNewsFeedResponse(1, 1, 3, nowMinus3, "", "", null, 1, 1),
                LoveSpotPhotoNewsFeedResponse(2, 1, 1, nowMinus4, "", "", null, 1, 1),
            ),
            loveSpotAddedHere = false
        )

        assertEquals(lover2, multiEvents.getLatestEventLover())
    }

    @Test
    fun getLatestEventLover3() {
        val now = Instant.now().toApiString()
        val nowMinus1 = Instant.now().minus(Duration.ofHours(1)).toApiString()
        val nowMinus2 = Instant.now().minus(Duration.ofHours(2)).toApiString()
        val nowMinus3 = Instant.now().minus(Duration.ofHours(3)).toApiString()
        val nowMinus4 = Instant.now().minus(Duration.ofHours(4)).toApiString()

        val lover1 = LoverViewWithoutRelationDto(1, "a", 1, 1, 1, 1, 1, now, true, null)
        val lover2 = LoverViewWithoutRelationDto(2, "b", 1, 1, 1, 1, 1, now, true, null)
        val lover3 = LoverViewWithoutRelationDto(3, "c", 1, 1, 1, 1, 1, now, true, null)
        val multiEvents = LoveSpotMultiEventsResponse(
            lovers = listOf(
                lover1,
                lover2,
                lover3,
            ),
            loveSpot = LoveSpotNewsFeedResponse(
                1, nowMinus4, 1, "a", "a", PUBLIC_SPACE, "a"
            ),
            loves = listOf(
                LoveNewsFeedResponse(1, "a", 1, 1, nowMinus1, null, null),
                LoveNewsFeedResponse(2, "b", 1, 2, nowMinus2, null, null),
            ),
            photos = listOf(
                LoveSpotPhotoNewsFeedResponse(1, 1, 3, now, "", "", null, 1, 1),
                LoveSpotPhotoNewsFeedResponse(2, 1, 1, nowMinus4, "", "", null, 1, 1),
            ),
            loveSpotAddedHere = false
        )

        assertEquals(lover3, multiEvents.getLatestEventLover())
    }
}