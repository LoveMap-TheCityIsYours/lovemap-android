package com.lovemap.lovemapandroid.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.ErrorCode.UNDEFINED
import retrofit2.Response

private val gson = Gson()

fun Response<out Any>.getErrorMessages(): List<ErrorMessage> {
    val errorMessages: MutableList<ErrorMessage> = ArrayList()
    errorBody()?.string()?.let {
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
    put(ErrorCode.PartnershipAlreadyRequested, R.string.partnership_already_requested)
    put(ErrorCode.PartnershipRerequestTimeNotPassed, R.string.partnership_already_requested)
    put(ErrorCode.InvalidOperationOnYourself, R.string.invalid_operation_on_yourself)
}

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
    InvalidOperationOnYourself,

    UNDEFINED
}

