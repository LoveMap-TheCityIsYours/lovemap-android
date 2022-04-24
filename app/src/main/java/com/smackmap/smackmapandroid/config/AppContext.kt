package com.smackmap.smackmapandroid.config

import android.app.Application
import com.smackmap.smackmapandroid.api.authentication.AuthenticationApi
import com.smackmap.smackmapandroid.api.smacker.SmackerApi
import com.smackmap.smackmapandroid.data.UserDataStore
import com.smackmap.smackmapandroid.service.Toaster
import com.smackmap.smackmapandroid.service.authentication.AuthenticationService
import com.smackmap.smackmapandroid.service.smacker.SmackerService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContext : Application() {
    lateinit var toaster: Toaster
    lateinit var authenticationService: AuthenticationService
    lateinit var smackerService: SmackerService
    lateinit var userDataStore: UserDataStore

    private lateinit var gsonConverterFactory: GsonConverterFactory
    private lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        toaster = Toaster(applicationContext.mainLooper, applicationContext)
        userDataStore = UserDataStore(applicationContext)
        gsonConverterFactory = GsonConverterFactory.create()
        runBlocking {
            initRetrofit()
            authenticationService = AuthenticationService(
                retrofit.create(AuthenticationApi::class.java),
                userDataStore,
                toaster,
                applicationContext
            )
            smackerService = SmackerService(
                retrofit.create(SmackerApi::class.java),
                userDataStore,
                toaster
            )
        }

        INSTANCE = this
    }

    private suspend fun initRetrofit() {
        val client = createHttpClient()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private suspend fun createHttpClient(): OkHttpClient {
        val client = if (userDataStore.isLoggedIn()) {
            val loggedInUser = userDataStore.get()
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader(AUTHORIZATION_HEADER, "Bearer ${loggedInUser.jwt}")
                        .build()
                    chain.proceed(request)
                }
                .build()
        } else {
            OkHttpClient.Builder().build()
        }
        return client
    }

    companion object {
        var INSTANCE = AppContext()
    }

    fun onLogin() {
        MainScope().launch {
            initRetrofit()
        }
    }
}