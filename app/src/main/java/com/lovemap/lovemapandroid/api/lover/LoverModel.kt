package com.lovemap.lovemapandroid.api.lover

import com.lovemap.lovemapandroid.api.lover.relation.RelationStatus
import com.lovemap.lovemapandroid.utils.instantOfApiString
import java.time.Instant

data class LoverDto(
    val id: Long,
    val userName: String,
    val displayName: String = userName,
    val email: String,
    val rank: Int,
    val points: Int,
    val numberOfLoves: Int,
    val reviewsSubmitted: Int,
    val reportsSubmitted: Int,
    val reportsReceived: Int,
    val loveSpotsAdded: Int,
    val numberOfFollowers: Int,
    val numberOfFollowings: Int,
    val photosUploaded: Int,
    val photoLikesReceived: Int,
    val photoDislikesReceived: Int,
    val hallOfFamePosition: Int?,
    val createdAt: String,
    val publicProfile: Boolean = false,
    val shareableLink: String? = null,
    val isAdmin: Boolean = false,
    val partnerId: Long?
) {
    fun getCreatedAt(): Instant {
        return instantOfApiString(createdAt)
    }
}

data class LoverRelationsDto(
    val id: Long,
    val relations: List<LoverViewDto>,
    val userName: String,
    val displayName: String = userName,
    val email: String,
    val rank: Int,
    val points: Int,
    val numberOfLoves: Int,
    val reviewsSubmitted: Int,
    val reportsSubmitted: Int,
    val reportsReceived: Int,
    val loveSpotsAdded: Int,
    val numberOfFollowers: Int,
    val numberOfFollowings: Int,
    val photosUploaded: Int,
    val photoLikesReceived: Int,
    val photoDislikesReceived: Int,
    val hallOfFamePosition: Int?,
    val createdAt: String,
    val publicProfile: Boolean = false,
    val shareableLink: String? = null,
    val isAdmin: Boolean = false,
    val partnerId: Long?
) {
    fun getCreatedAt(): Instant {
        return instantOfApiString(createdAt)
    }
}

data class LoverViewDto(
    val id: Long,
    val displayName: String,
    val points: Int,
    val rank: Int,
    val hallOfFamePosition: Int?,
    val numberOfFollowers: Int,
    val numberOfFollowings: Int,
    val createdAt: String,
    val relation: RelationStatus,
    val publicProfile: Boolean,
    val partnerId: Long?
)

data class LoverViewWithoutRelationDto(
    val id: Long,
    val displayName: String,
    val points: Int,
    val rank: Int,
    val hallOfFamePosition: Int?,
    val numberOfFollowers: Int,
    val numberOfFollowings: Int,
    val createdAt: String,
    val publicProfile: Boolean,
    val partnerId: Long?,
)

data class LoverRanks(val rankList: List<Rank>) {
    data class Rank(
        val rank: Int,
        val nameEN: String,
        val nameHU: String,
        val pointsNeeded: Int
    )
}

data class UpdateLoverRequest(
    val email: String? = null,
    val displayName: String? = null,
    val publicProfile: Boolean? = null,
)

data class FirebaseTokenRegistration(
    val token: String
)
