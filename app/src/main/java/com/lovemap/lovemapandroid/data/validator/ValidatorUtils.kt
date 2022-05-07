package com.lovemap.lovemapandroid.data.validator

import java.util.regex.Matcher
import java.util.regex.Pattern

val VALID_EMAIL_ADDRESS_REGEX: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

fun validateEmail(email: String): Boolean {
    val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email)
    return matcher.find()
}

fun validateUsername(username: String): Boolean {
    return username.length >= 3
}

fun validatePassword(password: String): Boolean {
    return password.length > 5
}

fun validatePasswordAgain(password: String, passwordAgain: String): Boolean {
    return password.length > 5 && passwordAgain == password
}