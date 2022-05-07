package com.lovemap.lovemapandroid.api.lover

import com.lovemap.lovemapandroid.api.relation.RelationStatusDto
import java.time.Instant

data class LoverDto(
    val id: Long,
    val userName: String,
    val email: String,
    val rank: Int,
    val points: Int,
    val numberOfLoves: Int,
    val numberOfReports: Int,
    val loveSpotsAdded: Int,
    val numberOfFollowers: Int,
    val createdAt: String,
    val shareableLink: String? = null,
) {
    fun getCreatedAt(): Instant {
        return Instant.ofEpochSecond(
            createdAt.substringBefore(".").toLong(),
            createdAt.substringAfter(".").toLong()
        )
    }
}

data class LoverRelationsDto(
    val id: Long,
    val relations: List<LoverViewDto>,
    val userName: String,
    val email: String,
    val rank: Int,
    val points: Int,
    val numberOfLoves: Int,
    val numberOfReports: Int,
    val loveSpotsAdded: Int,
    val numberOfFollowers: Int,
    val createdAt: String,
    val shareableLink: String? = null,
) {
    fun getCreatedAt(): Instant {
        return Instant.ofEpochSecond(
            createdAt.substringBefore(".").toLong(),
            createdAt.substringAfter(".").toLong()
        )
    }
}

data class LoverViewDto(
    val id: Long,
    val userName: String,
    val rank: Int,
    val relation: RelationStatusDto
)

data class LoverRanks(val rankList: List<Rank>) {
    data class Rank(
        val rank: Int,
        val nameEN: String,
        val nameHU: String,
        val pointsNeeded: Int
    )
}