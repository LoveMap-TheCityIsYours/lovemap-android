package com.smackmap.smackmapandroid.config

import android.app.Application
import com.smackmap.smackmapandroid.api.authentication.AuthenticationApi
import com.smackmap.smackmapandroid.data.UserDataStore
import com.smackmap.smackmapandroid.service.authentication.AuthenticationService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContext : Application() {
    lateinit var authenticationService: AuthenticationService
    lateinit var userDataStore: UserDataStore

    private lateinit var gsonConverterFactory: GsonConverterFactory
    private lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        retrofit = Retrofit.Builder()
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        gsonConverterFactory = GsonConverterFactory.create()
        userDataStore = UserDataStore(applicationContext)
        authenticationService = AuthenticationService(
            retrofit.create(AuthenticationApi::class.java),
            userDataStore,
            applicationContext
        )
        instance = this
    }

    companion object {
        var instance = AppContext()
    }
}