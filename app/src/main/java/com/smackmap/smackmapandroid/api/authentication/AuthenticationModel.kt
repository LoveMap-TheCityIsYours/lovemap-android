package com.smackmap.smackmapandroid.api.authentication

data class CreateSmackerRequest(
    val userName: String,
    val password: String,
    val email: String
)

data class LoginSmackerRequest(
    val email: String,
    val password: String
)
