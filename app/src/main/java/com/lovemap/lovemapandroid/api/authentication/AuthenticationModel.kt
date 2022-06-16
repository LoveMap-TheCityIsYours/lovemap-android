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

data class ResetPasswordRequest(
    val email: String
)

data class ResetPasswordResponse(
    val text: String
)

data class NewPasswordRequest(
    val email: String,
    val resetCode: String,
    val newPassword: String
)