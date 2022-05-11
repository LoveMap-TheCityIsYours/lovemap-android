package com.lovemap.lovemapandroid.config

import android.app.Application
import androidx.room.Room
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.lovemap.lovemapandroid.api.authentication.AuthenticationApi
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.api.lover.LoverApi
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotApi
import com.lovemap.lovemapandroid.api.partnership.PartnershipApi
import com.lovemap.lovemapandroid.data.AppDatabase
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao
import com.lovemap.lovemapandroid.service.*
import com.lovemap.lovemapandroid.ui.events.MainActivityEventListener
import com.lovemap.lovemapandroid.ui.events.MapInfoWindowShownEvent
import com.lovemap.lovemapandroid.ui.events.MapMarkerEventListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
    lateinit var loveService: LoveService
    lateinit var loverService: LoverService
    lateinit var loveSpotService: LoveSpotService
    lateinit var loveSpotReviewService: LoveSpotReviewService
    lateinit var partnershipService: PartnershipService

    lateinit var loveDao: LoveDao
    lateinit var loveSpotDao: LoveSpotDao
    lateinit var loveSpotReviewDao: LoveSpotReviewDao
    lateinit var partnershipDao: PartnershipDao

    lateinit var metadataStore: MetadataStore
    lateinit var database: AppDatabase

    lateinit var mapMarkerEventListener: MapMarkerEventListener
    lateinit var mainActivityEventListener: MainActivityEventListener

    var userId: Long = 0
    var areMarkerFabsOpen = false
    var areAddLoveSpotFabsOpen = false
    var shouldCloseFabs = false
    var displayDensity: Float = 0f
    var selectedMarker: Marker? = null
    var shouldMoveMapCamera: Boolean = false
    var zoomOnNewLoveSpot: LoveSpot? = null

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
        loveDao = database.loveDao()
        loveSpotDao = database.loveSpotDao()
        loveSpotReviewDao = database.loveSpotReviewDao()
        partnershipDao = database.partnershipDao()
        val loveSpotApi = retrofit.create(LoveSpotApi::class.java)
        loveService = LoveService(
            retrofit.create(LoveApi::class.java),
            loveDao,
            metadataStore,
            toaster
        )
        loverService = LoverService(
            retrofit.create(LoverApi::class.java),
            loveDao,
            loveSpotDao,
            loveSpotReviewDao,
            metadataStore,
            toaster
        )
        loveSpotService = LoveSpotService(
            loveSpotApi,
            loveSpotDao,
            metadataStore,
            toaster,
        )
        loveSpotReviewService = LoveSpotReviewService(
            loveSpotApi,
            loveSpotReviewDao,
            metadataStore,
            toaster,
        )
        partnershipService = PartnershipService(
            retrofit.create(PartnershipApi::class.java),
            partnershipDao,
            metadataStore,
            toaster
        )
        userId = if (metadataStore.isLoggedIn()) {
            loveService.list()
            loverService.getRanks()
            loveSpotService.getRisks()
            loveSpotReviewService.getReviewsByLover()
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

    fun deleteAllData() {
        MainScope().launch {
            metadataStore.deleteAll()
            loveDao.delete(*loveDao.getAll().toTypedArray())
            loveSpotDao.delete(*loveSpotDao.getAll().toTypedArray())
            loveSpotReviewDao.delete(*loveSpotReviewDao.getAll().toTypedArray())
        }
    }
}