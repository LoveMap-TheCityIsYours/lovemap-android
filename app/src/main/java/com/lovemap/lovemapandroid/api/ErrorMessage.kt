package com.lovemap.lovemapandroid.api

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.ErrorCode.*
import retrofit2.Response

private val objectMapper = jacksonObjectMapper()

fun Response<out Any>.getErrorMessages(): List<ErrorMessage> {
    val errorMessages: MutableList<ErrorMessage> = ArrayList()
    errorBody()?.string()?.let {
        try {
            val error = objectMapper.readValue(it, ErrorMessage::class.java)
            errorMessages.add(error)
        } catch (e: Exception) {
            try {
                val errors = objectMapper.readValue(it, ErrorMessages::class.java)
                errorMessages.addAll(errors.errors)
            } catch (e: Exception) {
                errorMessages.add(ErrorMessage(UNDEFINED, "", "No error message received."))
            }
        }
    } ?: run {
        errorMessages.add(ErrorMessage(UNDEFINED, "", "No error message received."))
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
) {
    fun translatedString(context: Context): String {
        val stringId: Int? = errorCodeMessageMap[errorCode]
        return stringId?.let { context.getString(it) } ?: errorCode.toString()
    }
}

private val errorCodeMessageMap = HashMap<ErrorCode, Int>().apply {
    put(PartnershipAlreadyRequested, R.string.partnership_already_requested)
    put(PartnershipRerequestTimeNotPassed, R.string.partnership_already_requested)
    put(InvalidOperationOnYourself, R.string.invalid_operation_on_yourself)
    put(UserOccupied, R.string.userOccupied)
    put(EmailOccupied, R.string.emailOccupied)
    put(InvalidCredentials, R.string.invalidCredentials)
    put(InvalidCredentialsEmail, R.string.invalidEmail)
    put(InvalidCredentialsUser, R.string.invalidUsername)
    put(InvalidCredentialsPassword, R.string.invalidPassword)
    put(BadRequest, R.string.somethingWentWrong)
    put(UNDEFINED, R.string.somethingWentWrong)
}

enum class ErrorCode {
    UserOccupied,
    EmailOccupied,
    InvalidCredentials,
    InvalidCredentialsEmail,
    InvalidCredentialsUser,
    InvalidCredentialsPassword,
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
    InvalidOperationOnYourself,

    UNDEFINED
}

