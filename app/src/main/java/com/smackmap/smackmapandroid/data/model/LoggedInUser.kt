package com.smackmap.smackmapandroid.data.model

data class LoggedInUser(
    var id: Long = 0,
    var userName: String,
    var email: String,
    var jwt: String,
    var link: String? = null,
)