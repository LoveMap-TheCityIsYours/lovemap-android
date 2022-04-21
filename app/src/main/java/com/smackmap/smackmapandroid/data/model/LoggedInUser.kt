package com.smackmap.smackmapandroid.data.model

import com.smackmap.smackmapandroid.api.smacker.SmackerDto
import com.smackmap.smackmapandroid.api.smacker.SmackerRelationsDto

data class LoggedInUser(
    var id: Long = 0,
    var userName: String,
    var email: String,
    var jwt: String,
    var shareableLink: String? = null,
) {
    companion object {
        fun of(smackerDto: SmackerDto, jwt: String): LoggedInUser {
            return LoggedInUser(
                id = smackerDto.id,
                userName = smackerDto.userName,
                email = smackerDto.email,
                shareableLink = smackerDto.shareableLink,
                jwt = jwt
            )
        }

        fun of(smackerDto: SmackerRelationsDto, jwt: String): LoggedInUser {
            return LoggedInUser(
                id = smackerDto.id,
                userName = smackerDto.userName,
                email = smackerDto.email,
                shareableLink = smackerDto.shareableLink,
                jwt = jwt
            )
        }
    }
}