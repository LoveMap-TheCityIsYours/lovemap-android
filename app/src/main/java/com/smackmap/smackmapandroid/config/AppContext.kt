package com.smackmap.smackmapandroid.config

import android.app.Application
import androidx.room.Room
import com.smackmap.smackmapandroid.api.authentication.AuthenticationApi
import com.smackmap.smackmapandroid.api.smacker.SmackerApi
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotApi
import com.smackmap.smackmapandroid.data.AppDatabase
import com.smackmap.smackmapandroid.data.MetadataStore
import com.smackmap.smackmapandroid.service.Toaster
import com.smackmap.smackmapandroid.service.authentication.AuthenticationService
import com.smackmap.smackmapandroid.service.smacker.SmackerService
import com.smackmap.smackmapandroid.service.smackspot.SmackSpotService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContext : Application() {
    lateinit var toaster: Toaster
    lateinit var authenticationService: AuthenticationService
    lateinit var smackerService: SmackerService
    lateinit var smackSpotService: SmackSpotService

    lateinit var metadataStore: MetadataStore
    lateinit var database: AppDatabase

    private lateinit var gsonConverterFactory: GsonConverterFactory
    private lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        toaster = Toaster(applicationContext.mainLooper, applicationContext)
        metadataStore = MetadataStore(applicationContext)
        gsonConverterFactory = GsonConverterFactory.create()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "database"
        ).build()
        runBlocking {
            initClients()
            authenticationService = AuthenticationService(
                retrofit.create(AuthenticationApi::class.java),
                metadataStore,
                toaster,
                applicationContext
            )
        }

        INSTANCE = this
    }

    private suspend fun initClients() {
        val client = createHttpClient()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        smackerService = SmackerService(
            retrofit.create(SmackerApi::class.java),
            metadataStore,
            toaster
        )
        smackSpotService = SmackSpotService(
            retrofit.create(SmackSpotApi::class.java),
            database.smackSpotDao(),
            metadataStore,
            toaster,
            applicationContext
        )
    }

    private suspend fun createHttpClient(): OkHttpClient {
        val client = if (metadataStore.isLoggedIn()) {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = runBlocking {
                        val requestBuilder = chain.request()
                            .newBuilder()
                        if (metadataStore.isLoggedIn()) {
                            val jwt = runBlocking { metadataStore.getUser().jwt }
                            requestBuilder
                                .addHeader(AUTHORIZATION_HEADER, "Bearer $jwt")
                                .build()
                        } else {
                            requestBuilder.build()
                        }
                    }
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

    suspend fun onLogin() {
        initClients()
    }
}