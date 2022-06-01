package com.lovemap.lovemapandroid.config

import android.app.Application
import androidx.room.Room
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.admin.AdminApi
import com.lovemap.lovemapandroid.api.authentication.AuthenticationApi
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.api.lover.LoverApi
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotApi
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotRisks
import com.lovemap.lovemapandroid.api.lovespot.report.LoveSpotReportApi
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewApi
import com.lovemap.lovemapandroid.api.partnership.PartnershipApi
import com.lovemap.lovemapandroid.data.AppDatabase
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao
import com.lovemap.lovemapandroid.service.*
import com.lovemap.lovemapandroid.ui.events.MapInfoWindowShownEvent
import com.lovemap.lovemapandroid.ui.events.MapMarkerEventListener
import com.lovemap.lovemapandroid.utils.AUTHORIZATION_HEADER
import com.lovemap.lovemapandroid.utils.X_CLIENT_ID_HEADER
import com.lovemap.lovemapandroid.utils.X_CLIENT_SECRET_HEADER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContext : Application() {
    lateinit var mapCameraTarget: LatLng
    lateinit var toaster: Toaster

    lateinit var authenticationService: AuthenticationService
    lateinit var loveService: LoveService
    lateinit var loverService: LoverService
    lateinit var loveSpotService: LoveSpotService
    lateinit var loveSpotReviewService: LoveSpotReviewService
    lateinit var loveSpotReportService: LoveSpotReportService
    lateinit var partnershipService: PartnershipService

    lateinit var loveDao: LoveDao
    lateinit var loveSpotDao: LoveSpotDao
    lateinit var loveSpotReviewDao: LoveSpotReviewDao
    lateinit var partnershipDao: PartnershipDao

    lateinit var metadataStore: MetadataStore
    lateinit var database: AppDatabase

    lateinit var mapMarkerEventListener: MapMarkerEventListener

    @Volatile
    var userId: Long = 0

    @Volatile
    var otherLoverId: Long = 0

    @Volatile
    var areMarkerFabsOpen = false

    @Volatile
    var areAddLoveSpotFabsOpen = false

    @Volatile
    var shouldCloseFabs = false

    @Volatile
    var displayDensity: Float = 0f

    @Volatile
    var selectedMarker: Marker? = null

    @Volatile
    var selectedLoveSpot: LoveSpot? = null

    @Volatile
    var selectedLoveSpotId: Long? = null

    @Volatile
    var zoomOnLoveSpot: LoveSpot? = null

    @Volatile
    var shouldClearMap: Boolean = false

    @Volatile
    var loveSpotRisks: LoveSpotRisks? = null

    private lateinit var gsonConverterFactory: GsonConverterFactory
    private lateinit var authorizingRetrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        displayDensity = applicationContext.resources.displayMetrics.density
        toaster = Toaster(applicationContext.mainLooper, applicationContext)
        metadataStore = MetadataStore(applicationContext)
        gsonConverterFactory = GsonConverterFactory.create()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "database"
        ).build()
        EventBus.getDefault().register(this)
        runBlocking {
            initClients()
            val retrofit = Retrofit.Builder()
                .client(httpClient())
                .baseUrl(API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            authenticationService = AuthenticationService(
                retrofit.create(AuthenticationApi::class.java),
                metadataStore,
                toaster,
                applicationContext
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapInfoWindowShownEvent(event: MapInfoWindowShownEvent) {
        selectedMarker = event.marker
        selectedLoveSpot = event.loveSpot
        selectedLoveSpotId = event.loveSpot?.id
    }

    private suspend fun initClients() {
        authorizingRetrofit = Retrofit.Builder()
            .client(authorizingHttpClient())
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        loveDao = database.loveDao()
        loveSpotDao = database.loveSpotDao()
        loveSpotReviewDao = database.loveSpotReviewDao()
        partnershipDao = database.partnershipDao()
        loverService = LoverService(
            authorizingRetrofit.create(LoverApi::class.java),
            loveDao,
            loveSpotDao,
            loveSpotReviewDao,
            metadataStore,
            toaster
        )
        loveService = LoveService(
            loveApi = authorizingRetrofit.create(LoveApi::class.java),
            loveDao = loveDao,
            loverService = loverService,
            metadataStore = metadataStore,
            context = applicationContext,
            toaster = toaster,
        )
        loveSpotService = LoveSpotService(
            authorizingRetrofit.create(LoveSpotApi::class.java),
            loveSpotDao,
            metadataStore,
            toaster,
        )
        loveSpotReviewService = LoveSpotReviewService(
            loveSpotReviewApi = authorizingRetrofit.create(LoveSpotReviewApi::class.java),
            loveSpotReviewDao = loveSpotReviewDao,
            loveSpotService = loveSpotService,
            metadataStore = metadataStore,
            toaster = toaster,
        )
        loveSpotReportService = LoveSpotReportService(
            loveSpotReportApi = authorizingRetrofit.create(LoveSpotReportApi::class.java),
            adminApi = authorizingRetrofit.create(AdminApi::class.java),
            loveSpotService = loveSpotService,
            loverService = loverService,
            metadataStore = metadataStore,
            toaster = toaster,
        )
        partnershipService = PartnershipService(
            authorizingRetrofit.create(PartnershipApi::class.java),
            partnershipDao,
            metadataStore,
            toaster
        )
        userId = if (metadataStore.isLoggedIn()) {
            metadataStore.getUser().id
        } else {
            0
        }
        if (userId != 0L) {
            loverService.getRanks()
            loveSpotRisks = loveSpotService.getRisks()
//           TODO: for speeding up start, remove these 2 lines:
            loveService.list()
            loveSpotReviewService.getReviewsByLover()
        }
    }

    private suspend fun authorizingHttpClient(): OkHttpClient {
        val clientBuilder = getTimeoutClientBuilder()
        val client = if (metadataStore.isLoggedIn()) {
            clientBuilder.addInterceptor { chain ->
                val request = runBlocking {
                    val requestBuilder = chain.request()
                        .newBuilder()
                    if (metadataStore.isLoggedIn()) {
                        val jwt = runBlocking { metadataStore.getUser().jwt }
                        requestBuilder
                            .addHeader(AUTHORIZATION_HEADER, "Bearer $jwt")
                            .addHeader(X_CLIENT_ID_HEADER, getString(R.string.lovemap_client_id))
                            .addHeader(X_CLIENT_SECRET_HEADER, getString(R.string.lovemap_client_secret))
                            .build()
                    } else {
                        requestBuilder.build()
                    }
                }
                chain.proceed(request)
            }.build()
        } else {
            clientBuilder.build()
        }
        return client
    }

    private fun httpClient(): OkHttpClient {
        return getTimeoutClientBuilder().addInterceptor { chain ->
            val request = runBlocking {
                chain.request()
                    .newBuilder()
                    .addHeader(X_CLIENT_ID_HEADER, getString(R.string.lovemap_client_id))
                    .addHeader(X_CLIENT_SECRET_HEADER, getString(R.string.lovemap_client_secret))
                    .build()
            }
            chain.proceed(request)
        }.build()
    }

    private fun getTimeoutClientBuilder() = OkHttpClient.Builder()
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)

    companion object {
        var INSTANCE = AppContext()
    }

    suspend fun onLogin() {
        initClients()
    }

    suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            metadataStore.deleteAll()
            loveDao.delete(*loveDao.getAll().toTypedArray())
            loveSpotDao.delete(*loveSpotDao.getAll().toTypedArray())
            loveSpotReviewDao.delete(*loveSpotReviewDao.getAll().toTypedArray())
            partnershipDao.delete(*partnershipDao.getAll().toTypedArray())
        }
    }
}