package com.smackmap.smackmapandroid.api.smacker

import com.smackmap.smackmapandroid.api.relation.RelationStatusDto

class SmackerModel {
}

data class SmackerDto(
    val id: Long,
    val userName: String,
    val email: String,
    val shareableLink: String? = null
)

data class SmackerRelationsDto(
    val id: Long,
    val relations: List<SmackerViewDto>,
    val userName: String,
    val email: String,
    val shareableLink: String? = null
)

data class SmackerViewDto(
    val id: Long,
    val userName: String,
    val relation: RelationStatusDto
)