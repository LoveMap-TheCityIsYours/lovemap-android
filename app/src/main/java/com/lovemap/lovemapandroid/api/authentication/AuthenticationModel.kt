package com.lovemap.lovemapandroid.api.authentication

data class CreateLoverRequest(
    val userName: String,
    val password: String,
    val email: String
)

data class LoginLoverRequest(
    val email: String,
    val password: String
)
