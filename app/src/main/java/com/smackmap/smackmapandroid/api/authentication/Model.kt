package com.smackmap.smackmapandroid.api.authentication

data class CreateSmackerRequest(
    val userName: String,
    val password: String,
    val email: String
)

data class SmackerResponse(
    val id: Long,
    val userName: String,
    val email: String,
    val shareableLink: String? = null
)
