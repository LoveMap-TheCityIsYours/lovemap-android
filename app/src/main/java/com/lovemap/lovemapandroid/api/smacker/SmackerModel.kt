package com.lovemap.lovemapandroid.api.smacker

import com.lovemap.lovemapandroid.api.relation.RelationStatusDto
import java.time.Instant

data class SmackerDto(
    val id: Long,
    val userName: String,
    val email: String,
    val rank: Int,
    val points: Int,
    val numberOfSmacks: Int,
    val numberOfReports: Int,
    val smackSpotsAdded: Int,
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

data class SmackerRelationsDto(
    val id: Long,
    val relations: List<SmackerViewDto>,
    val userName: String,
    val email: String,
    val rank: Int,
    val points: Int,
    val numberOfSmacks: Int,
    val numberOfReports: Int,
    val smackSpotsAdded: Int,
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

data class SmackerViewDto(
    val id: Long,
    val userName: String,
    val rank: Int,
    val relation: RelationStatusDto
)

data class SmackerRanks(val rankList: List<Rank>) {
    data class Rank(
        val rank: Int,
        val nameEN: String,
        val nameHU: String,
        val pointsNeeded: Int
    )
}
