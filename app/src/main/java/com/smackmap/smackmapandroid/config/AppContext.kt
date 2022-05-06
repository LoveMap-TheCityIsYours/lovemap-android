package com.smackmap.smackmapandroid.config

import android.app.Application
import androidx.room.Room
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.smackmap.smackmapandroid.api.authentication.AuthenticationApi
import com.smackmap.smackmapandroid.api.smack.SmackApi
import com.smackmap.smackmapandroid.api.smacker.SmackerApi
import com.smackmap.smackmapandroid.api.smackspot.SmackSpotApi
import com.smackmap.smackmapandroid.data.AppDatabase
import com.smackmap.smackmapandroid.data.metadata.MetadataStore
import com.smackmap.smackmapandroid.service.*
import com.smackmap.smackmapandroid.ui.events.MainActivityEventListener
import com.smackmap.smackmapandroid.ui.events.MapInfoWindowShownEvent
import com.smackmap.smackmapandroid.ui.events.MapMarkerEventListener
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContext : Application() {
    lateinit var mapCameraTarget: LatLng
    lateinit var toaster: Toaster
    lateinit var authenticationService: AuthenticationService
    lateinit var smackerService: SmackerService
    lateinit var smackSpotService: SmackSpotService
    lateinit var smackService: SmackService

    lateinit var metadataStore: MetadataStore
    lateinit var database: AppDatabase

    lateinit var mapMarkerEventListener: MapMarkerEventListener
    lateinit var mainActivityEventListener: MainActivityEventListener

    var userId: Long = 0
    var areMarkerFabsOpen = false
    var areAddSmackSpotFabsOpen = false
    var shouldCloseFabs = false
    var displayDensity: Float = 0f
    var selectedMarker: Marker? = null
    var shouldMoveMapCamera: Boolean = false

    private lateinit var gsonConverterFactory: GsonConverterFactory
    private lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        displayDensity = applicationContext.resources.displayMetrics.density
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
        EventBus.getDefault().register(this)
        INSTANCE = this
    }

    override fun onTerminate() {
        super.onTerminate()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapInfoWindowShownEvent(event: MapInfoWindowShownEvent) {
        selectedMarker = event.marker
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
        )
        smackService = SmackService(
            retrofit.create(SmackApi::class.java),
            database.smackDao(),
            metadataStore,
            toaster
        )
        userId = if (metadataStore.isLoggedIn()) {
            metadataStore.getUser().id
        } else {
            0
        }
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