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
import com.lovemap.lovemapandroid.ui.main.MainActivity
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
                    sendNotification(NotificationType.valueOf(it))
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

    private fun sendNotification(type: NotificationType) {
        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(MainActivity.OPEN_PAGE, MainActivity.NEWS_FEED_PAGE)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val messageBody = getMessageBody(type)
        val notificationBuilder = NotificationCompat.Builder(this, DEFAULT_CHANNEL)
            .setContentTitle(getMessageTitle(type))
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_two_hearts_purple)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            DEFAULT_CHANNEL,
            resources.getString(R.string.notification_default_channel),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(getNotificationId(type), notificationBuilder.build())
    }

    private fun getMessageTitle(type: NotificationType): String {
        return when (type) {
            NotificationType.COME_BACK_PLEASE -> resources.getString(R.string.notification_come_back_title)
        }
    }

    private fun getMessageBody(type: NotificationType): String {
        return when (type) {
            NotificationType.COME_BACK_PLEASE -> resources.getString(R.string.notification_come_back_body)
        }
    }

    private fun getNotificationId(type: NotificationType): Int {
        return when (type) {
            NotificationType.COME_BACK_PLEASE -> 0
        }
    }

    companion object {
        private const val DEFAULT_CHANNEL = "fcm_default_channel"
        private const val NOTIFICATION_TYPE = "notificationType"
    }
}
