package com.lovemap.lovemapandroid.data.metadata

import com.lovemap.lovemapandroid.api.lover.LoverDto
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto

data class LoggedInUser(
    var id: Long = 0,
    var userName: String,
    var email: String,
    var jwt: String,
    var shareableLink: String? = null,
) {
    companion object {
        fun of(loverDto: LoverDto, jwt: String): LoggedInUser {
            return LoggedInUser(
                id = loverDto.id,
                userName = loverDto.userName,
                email = loverDto.email,
                shareableLink = loverDto.shareableLink,
                jwt = jwt
            )
        }

        fun of(loverDto: LoverRelationsDto, jwt: String): LoggedInUser {
            return LoggedInUser(
                id = loverDto.id,
                userName = loverDto.userName,
                email = loverDto.email,
                shareableLink = loverDto.shareableLink,
                jwt = jwt
            )
        }
    }
}