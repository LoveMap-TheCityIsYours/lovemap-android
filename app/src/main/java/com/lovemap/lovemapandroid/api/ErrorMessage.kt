package com.lovemap.lovemapandroid.api

import android.content.Context
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

fun Response<out Any>.getErrorCodes(): List<ErrorCode> {
    return getErrorMessages().map { it.errorCode }
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
    put(SpotTooCloseToAnother, R.string.spotTooCloseToAnother)
    put(InvalidLoveSpotName, R.string.invalidLoveSpotName)
    put(InvalidLoveSpotDescription, R.string.invalidLoveSpotDescription)
    put(PartnershipNotFound, R.string.partnershipNotFound)
    put(InvalidPwResetCode, R.string.invalidPwResetCode)
    put(PwResetBackoffNotPassed, R.string.pwResetBackoffNotPassed)
    put(WrongPwResetCode, R.string.wrongPwResetCode)
    put(PwResetCodeTimedOut, R.string.pwResetCodeTimedOut)
    put(InvalidLimit, R.string.invalidLimit)
    put(MissingListCoordinates, R.string.missingListCoordinates)
    put(FacebookEmailOccupied, R.string.facebookEmailOccupied)
    put(FacebookLoginFailed, R.string.facebook_login_failed)
    put(UnsupportedImageFormat, R.string.unsupported_photo_format)
    put(ImageUploadFailed, R.string.photo_upload_failed)
    put(PhotoNotFound, R.string.photo_not_found)
    put(WishlistItemNotFound, R.string.wishlist_item_not_found)
    put(AlreadyOnWishlist, R.string.already_on_wishlist)
    put(LoveSpotNotFound, R.string.love_spot_not_available)
    put(InternalServerError, R.string.internal_server_error)
    put(UploadedPhotoFileEmpty, R.string.uploaded_photo_file_empty)
    put(NewsFeedPageNotFound, R.string.news_feed_page_not_found)
    put(InvalidPageRequest, R.string.invalid_page_request)
    put(AlreadyHasPartner, R.string.already_have_a_partner)
    put(Forbidden, R.string.not_allowed_to_do_that)
    put(PartnershipNotFound, R.string.partnershipNotFound)
    put(RespondentAlreadyHasPartner, R.string.respondent_already_has_a_partner)
    put(LoverNotFound, R.string.lover_not_found)
    put(LoverIsNotPublic, R.string.lover_is_not_public)
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
    SpotTooCloseToAnother,
    InvalidLoveSpotName,
    InvalidLoveSpotDescription,
    InvalidPwResetCode,
    PwResetBackoffNotPassed,
    WrongPwResetCode,
    PwResetCodeTimedOut,
    MissingListCountry,
    MissingListCity,
    MissingListCoordinates,
    InvalidDistanceInMeters,
    InvalidLimit,
    InvalidListLocationType,
    FacebookEmailOccupied,
    FacebookLoginFailed,
    UnsupportedImageFormat,
    ImageUploadFailed,
    PhotoNotFound,
    WishlistItemNotFound,
    AlreadyOnWishlist,
    LoveSpotNotFound,
    InternalServerError,
    UploadedPhotoFileEmpty,
    NewsFeedPageNotFound,
    InvalidPageRequest,
    AlreadyHasPartner,
    RespondentAlreadyHasPartner,
    LoverNotFound,
    LoverIsNotPublic,

    UNDEFINED
}

