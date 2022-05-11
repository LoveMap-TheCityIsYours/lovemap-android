package com.lovemap.lovemapandroid.api.partnership

import com.lovemap.lovemapandroid.data.partnership.Partnership

data class RequestPartnershipRequest(
    val initiatorId: Long,
    val respondentId: Long,
)

data class RespondPartnershipRequest(
    val initiatorId: Long,
    val respondentId: Long,
    val response: PartnershipReaction
)

enum class PartnershipReaction {
    ACCEPT, DENY
}

data class LoverPartnershipsResponse(
    val loverId: Long,
    val partnerships: List<Partnership>
)

enum class PartnershipStatus {
    PARTNERSHIP_REQUESTED, IN_PARTNERSHIP;
}