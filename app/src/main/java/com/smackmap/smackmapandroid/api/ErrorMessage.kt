package com.smackmap.smackmapandroid.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.smackmap.smackmapandroid.api.ErrorCode.UNDEFINED
import retrofit2.Response

private val gson = Gson()

fun Response<out Any>.getErrorMessages(): List<ErrorMessage> {
    val errorMessages: MutableList<ErrorMessage> = ArrayList()
    errorBody()?.toString()?.let {
        try {
            val errorMessage = gson.fromJson(it, ErrorMessage::class.java)
            errorMessages.add(errorMessage)
        } catch (e: JsonSyntaxException) {
            try {
                return gson.fromJson(it, ErrorMessages::class.java).errors
            } catch (e: JsonSyntaxException) {
                errorMessages.add(ErrorMessage(UNDEFINED, "", it))
            }
        }
    }
    return errorMessages
}

data class ErrorMessages(
    val errors: List<ErrorMessage>
)

data class ErrorMessage(
    val errorCode: ErrorCode,
    val subject: String,
    val message: String,
)

enum class ErrorCode {
    UserOccupied,
    EmailOccupied,
    InvalidCredentialsEmail,
    InvalidCredentialsUser,
    NotFoundByLink,
    NotFoundById,
    ConstraintViolation,
    Forbidden,
    Conflict,
    BadRequest,
    YouBlockedHimUnblockFirst,
    BlockedByUser,
    RelationNotFound,
    PartnershipNotFound,
    AlreadyPartners,
    PartnershipRerequestTimeNotPassed,
    PartnershipAlreadyRequested,

    UNDEFINED
}