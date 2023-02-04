package com.lovemap.lovemapandroid.notification

data class NotificationMessage(
    val type: NotificationType
)

enum class NotificationType {
    COME_BACK_PLEASE
}
