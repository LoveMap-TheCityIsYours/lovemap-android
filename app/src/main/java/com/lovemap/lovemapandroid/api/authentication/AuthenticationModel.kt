package com.lovemap.lovemapandroid.api.authentication

data class CreateLoverRequest(
    val userName: String,
    val password: String,
    val email: String,
    val publicProfile: Boolean,
    val registrationCountry: String?
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

data class FacebookAuthenticationRequest(
    val email: String,
    val facebookId: String,
    val accessToken: String,
    val registrationCountry: String?
)
