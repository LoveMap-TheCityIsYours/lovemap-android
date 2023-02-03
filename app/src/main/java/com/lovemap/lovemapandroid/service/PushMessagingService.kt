package com.lovemap.lovemapandroid.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PushMessagingService : FirebaseMessagingService() {
    private val tag = "PushMessagingService"

    override fun onNewToken(token: String) {
        MainScope().launch {
            AppContext.INSTANCE.loverService.registerFirebaseToken(token)
        }
    }
}
