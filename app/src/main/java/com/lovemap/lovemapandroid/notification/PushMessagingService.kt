package com.lovemap.lovemapandroid.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.notification.NotificationType.*
import com.lovemap.lovemapandroid.ui.lover.ViewOtherLoverActivity
import com.lovemap.lovemapandroid.ui.main.MainActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PushMessagingService : FirebaseMessagingService() {
    private val tag = "PushMessagingService"

    override fun onNewToken(token: String) {
        MainScope().launch {
            AppContext.INSTANCE.loverService.registerFirebaseToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(tag, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(tag, "Message data payload: ${remoteMessage.data}")
            remoteMessage.data[NOTIFICATION_TYPE]?.let {
                runCatching {
                    sendNotification(NotificationType.valueOf(it), remoteMessage.data)
                }.onFailure { e ->
                    Log.e(tag, "shit happened", e)
                }
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
            } else {
                // Handle message within 10 seconds
//                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(tag, "Message Notification Body: ${it.body}")
        }
    }

    private fun sendNotification(type: NotificationType, data: Map<String, String>) {
        val pendingIntent = getIntent(type, data)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val messageBody = getMessageBody(type)
        val notificationBuilder = NotificationCompat.Builder(this, getNotificationChannelId(type))
            .setContentTitle(getMessageTitle(type))
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_two_hearts_white)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            getNotificationChannelId(type),
            getNotificationChannelName(type),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(getNotificationId(type, data), notificationBuilder.build())
    }

    private fun getIntent(type: NotificationType, data: Map<String, String>): PendingIntent {
        return when (type) {

            COME_BACK_PLEASE -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(MainActivity.OPEN_PAGE, MainActivity.NEWS_FEED_PAGE)
                PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            NEW_PUBLIC_LOVER -> {
                val intent = Intent(this, ViewOtherLoverActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                data[LOVER_ID]?.toLong()?.let { loverId ->
                    Log.i(tag, "Public Lover Notification with loverId: '$loverId'")
                    intent.putExtra(ViewOtherLoverActivity.LOVER_ID, loverId)
                    PendingIntent.getActivity(
                        this, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } ?: throw IllegalArgumentException("Cannot parse loverId. Data: $data")
            }

            NEW_FOLLOWER, PARTNERSHIP_REQUESTED, PARTNERSHIP_ACCEPTED, PARTNERSHIP_DENIED -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(MainActivity.OPEN_PAGE, MainActivity.PROFILE_PAGE)
                PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            NEW_LOVE_SPOT, NEW_LOVE_SPOT_PHOTO, NEW_LOVE_SPOT_REVIEW, NEW_REVIEW_ON_YOUR_LOVE_SPOT,
            NEW_LIKE_ON_YOUR_PHOTO, NEW_DISLIKE_ON_YOUR_PHOTO -> {
                val intent = Intent(this, LoveSpotDetailsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                data[LOVE_SPOT_ID]?.toLong()?.let { loveSpotId ->
                    Log.i(tag, "LoveSpot Notification with loveSpotId: '$loveSpotId'")
                    AppContext.INSTANCE.selectedLoveSpotId = loveSpotId
                    intent.putExtra(LoveSpotDetailsActivity.LOVE_SPOT_ID, loveSpotId)
                    PendingIntent.getActivity(
                        this, loveSpotId.toInt(), intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } ?: throw IllegalArgumentException("Cannot parse LoveSpotId. Data: $data")
            }

        }
    }

    private fun getMessageTitle(type: NotificationType): String {
        return when (type) {
            COME_BACK_PLEASE -> resources.getString(R.string.notification_come_back_title)
            NEW_LOVE_SPOT -> resources.getString(R.string.notification_new_love_spot_title)
            NEW_LOVE_SPOT_PHOTO -> resources.getString(R.string.notification_new_photo_title)
            NEW_LOVE_SPOT_REVIEW -> resources.getString(R.string.notification_new_review_title)
            NEW_PUBLIC_LOVER -> resources.getString(R.string.notification_new_public_lover_title)

            NEW_REVIEW_ON_YOUR_LOVE_SPOT -> resources.getString(R.string.notification_new_review_on_your_spot_title)
            NEW_LIKE_ON_YOUR_PHOTO -> resources.getString(R.string.notification_new_like_on_your_photo_title)
            NEW_DISLIKE_ON_YOUR_PHOTO -> resources.getString(R.string.notification_new_dislike_on_your_photo_title)
            NEW_FOLLOWER -> resources.getString(R.string.notification_new_follower_title)
            PARTNERSHIP_REQUESTED -> resources.getString(R.string.notification_partnership_requested_title)
            PARTNERSHIP_ACCEPTED -> resources.getString(R.string.notification_partnership_accepted_title)
            PARTNERSHIP_DENIED -> resources.getString(R.string.notification_partnership_denied_title)
        }
    }

    private fun getMessageBody(type: NotificationType): String {
        return when (type) {
            COME_BACK_PLEASE -> resources.getString(R.string.notification_come_back_body)
            NEW_LOVE_SPOT -> resources.getString(R.string.notification_new_love_spot_body)
            NEW_LOVE_SPOT_PHOTO -> resources.getString(R.string.notification_new_photo_body)
            NEW_LOVE_SPOT_REVIEW -> resources.getString(R.string.notification_new_review_body)
            NEW_PUBLIC_LOVER -> resources.getString(R.string.notification_new_public_lover_body)

            NEW_REVIEW_ON_YOUR_LOVE_SPOT -> resources.getString(R.string.notification_new_review_on_your_spot_body)
            NEW_LIKE_ON_YOUR_PHOTO -> resources.getString(R.string.notification_new_like_on_your_photo_body)
            NEW_DISLIKE_ON_YOUR_PHOTO -> resources.getString(R.string.notification_new_dislike_on_your_photo_body)
            NEW_FOLLOWER -> resources.getString(R.string.notification_new_follower_body)
            PARTNERSHIP_REQUESTED -> resources.getString(R.string.notification_partnership_requested_body)
            PARTNERSHIP_ACCEPTED -> resources.getString(R.string.notification_partnership_accepted_body)
            PARTNERSHIP_DENIED -> resources.getString(R.string.notification_partnership_denied_body)
        }
    }

    private fun getNotificationId(type: NotificationType, data: Map<String, String>): Int {
        return when (type) {
            COME_BACK_PLEASE -> 0
            NEW_LOVE_SPOT -> data[LOVE_SPOT_ID]?.toLong()?.toInt() ?: 1
            NEW_LOVE_SPOT_PHOTO -> data[LOVE_SPOT_ID]?.toLong()?.toInt() ?: 2
            NEW_LOVE_SPOT_REVIEW -> data[LOVE_SPOT_ID]?.toLong()?.toInt() ?: 3
            NEW_PUBLIC_LOVER -> data[LOVER_ID]?.toLong()?.toInt() ?: 4

            NEW_REVIEW_ON_YOUR_LOVE_SPOT -> data[LOVE_SPOT_ID]?.toLong()?.toInt() ?: 5
            NEW_LIKE_ON_YOUR_PHOTO -> data[LOVE_SPOT_ID]?.toLong()?.toInt() ?: 6
            NEW_DISLIKE_ON_YOUR_PHOTO -> data[LOVE_SPOT_ID]?.toLong()?.toInt() ?: 7
            NEW_FOLLOWER -> 8
            PARTNERSHIP_REQUESTED -> 9
            PARTNERSHIP_ACCEPTED -> 10
            PARTNERSHIP_DENIED -> 11
        }
    }

    private fun getNotificationChannelId(type: NotificationType): String {
        return when (type) {
            COME_BACK_PLEASE -> "fcm_come_back_please"
            NEW_LOVE_SPOT -> "fcm_new_love_spot"
            NEW_LOVE_SPOT_PHOTO -> "fcm_new_love_spot_photo"
            NEW_LOVE_SPOT_REVIEW -> "fcm_new_love_spot_review"
            NEW_PUBLIC_LOVER -> "fcm_new_public_lover"

            NEW_REVIEW_ON_YOUR_LOVE_SPOT -> "fcm_new_review_on_your_spot"
            NEW_LIKE_ON_YOUR_PHOTO -> "fcm_new_like_on_your_photo"
            NEW_DISLIKE_ON_YOUR_PHOTO -> "fcm_new_dislike_on_your_photo"
            NEW_FOLLOWER -> "fcm_new_follower"
            PARTNERSHIP_REQUESTED -> "fcm_partnership_requested"
            PARTNERSHIP_ACCEPTED -> "fcm_partnership_accepted"
            PARTNERSHIP_DENIED -> "fcm_partnership_denied"
        }
    }

    private fun getNotificationChannelName(type: NotificationType): String {
        return when (type) {
            COME_BACK_PLEASE -> resources.getString(R.string.notification_channel_come_back_please)
            NEW_LOVE_SPOT -> resources.getString(R.string.notification_channel_new_love_spot)
            NEW_LOVE_SPOT_PHOTO -> resources.getString(R.string.notification_channel_new_love_spot_photo)
            NEW_LOVE_SPOT_REVIEW -> resources.getString(R.string.notification_channel_new_love_spot_review)
            NEW_PUBLIC_LOVER -> resources.getString(R.string.notification_channel_new_public_lover)

            NEW_REVIEW_ON_YOUR_LOVE_SPOT -> resources.getString(R.string.notification_channel_new_review_on_your_spot)
            NEW_LIKE_ON_YOUR_PHOTO -> resources.getString(R.string.notification_channel_new_like_on_your_photo)
            NEW_DISLIKE_ON_YOUR_PHOTO -> resources.getString(R.string.notification_channel_new_dislike_on_your_photo)
            NEW_FOLLOWER -> resources.getString(R.string.notification_channel_new_follower)
            PARTNERSHIP_REQUESTED -> resources.getString(R.string.notification_channel_partnership_requested)
            PARTNERSHIP_ACCEPTED -> resources.getString(R.string.notification_channel_partnership_accepted)
            PARTNERSHIP_DENIED -> resources.getString(R.string.notification_channel_partnership_denied)
        }
    }

    companion object {
        private const val NOTIFICATION_TYPE = "notificationType"
        private const val LOVE_SPOT_ID = "loveSpotId"
        private const val LOVER_ID = "loverId"
    }
}
