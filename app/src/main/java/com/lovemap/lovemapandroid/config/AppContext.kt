package com.lovemap.lovemapandroid.config

import android.annotation.SuppressLint
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.google.android.gms.location.*
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.admin.AdminApi
import com.lovemap.lovemapandroid.api.authentication.AuthenticationApi
import com.lovemap.lovemapandroid.api.geolocation.GeoLocationApi
import com.lovemap.lovemapandroid.api.love.LoveApi
import com.lovemap.lovemapandroid.api.lover.LoverApi
import com.lovemap.lovemapandroid.api.lover.wishlist.WishlistApi
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotApi
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotRisks
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhotoApi
import com.lovemap.lovemapandroid.api.lovespot.report.LoveSpotReportApi
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewApi
import com.lovemap.lovemapandroid.api.partnership.PartnershipApi
import com.lovemap.lovemapandroid.data.AppDatabase
import com.lovemap.lovemapandroid.data.love.LoveDao
import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItemDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.lovespot.LoveSpotDao
import com.lovemap.lovemapandroid.data.lovespot.review.LoveSpotReviewDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.data.partnership.PartnershipDao
import com.lovemap.lovemapandroid.service.*
import com.lovemap.lovemapandroid.ui.events.MapInfoWindowShownEvent
import com.lovemap.lovemapandroid.ui.main.lovespot.list.LoveSpotListFilterState
import com.lovemap.lovemapandroid.utils.AUTHORIZATION_HEADER
import com.lovemap.lovemapandroid.utils.X_CLIENT_ID_HEADER
import com.lovemap.lovemapandroid.utils.X_CLIENT_SECRET_HEADER
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class AppContext : MultiDexApplication() {
    lateinit var toaster: Toaster

    lateinit var authenticationService: AuthenticationService
    lateinit var loveService: LoveService
    lateinit var loverService: LoverService
    lateinit var loveSpotService: LoveSpotService
    lateinit var loveSpotReviewService: LoveSpotReviewService
    lateinit var loveSpotReportService: LoveSpotReportService
    lateinit var partnershipService: PartnershipService
    lateinit var geoLocationService: GeoLocationService
    lateinit var loveSpotPhotoService: LoveSpotPhotoService
    lateinit var wishlistService: WishlistService

    lateinit var loveDao: LoveDao
    lateinit var loveSpotDao: LoveSpotDao
    lateinit var loveSpotReviewDao: LoveSpotReviewDao
    lateinit var partnershipDao: PartnershipDao
    lateinit var wishlistItemDao: WishlistItemDao

    lateinit var metadataStore: MetadataStore
    lateinit var database: AppDatabase

    @Volatile
    var userId: Long = 0

    @Volatile
    var isAdmin: Boolean = false

    @Volatile
    var otherLoverId: Long = 0

    @Volatile
    var displayDensity: Float = 0f

    @Volatile
    var selectedLoveSpotId: Long? = null

    @Volatile
    var loveSpotRisks: LoveSpotRisks? = null

    lateinit var country: String

    private lateinit var gsonConverterFactory: GsonConverterFactory
    private lateinit var authorizingRetrofit: Retrofit

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        runBlocking {
            initInstance()
            initClients()
        }
        MainScope().launch {
            withContext(Dispatchers.IO) {
                fetchUserData()
            }
        }
        initCountry()
    }

    private fun initCountry() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val countryCode: String = telephonyManager.networkCountryIso
        country = Locale("EN", countryCode).getDisplayCountry(Locale("EN"))
        LoveSpotListFilterState.locationName = country
    }

    private fun initInstance() {
        INSTANCE = this
        displayDensity = applicationContext.resources.displayMetrics.density
        toaster = Toaster(applicationContext.mainLooper, applicationContext)
        metadataStore = MetadataStore(applicationContext)
        gsonConverterFactory = GsonConverterFactory.create()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "database"
        ).fallbackToDestructiveMigration().build()
        EventBus.getDefault().register(this)
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
        loveDao = database.loveDao()
        loveSpotDao = database.loveSpotDao()
        loveSpotReviewDao = database.loveSpotReviewDao()
        partnershipDao = database.partnershipDao()
        wishlistItemDao = database.wishlistElementDao()
    }

    override fun onTerminate() {
        super.onTerminate()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapInfoWindowShownEvent(event: MapInfoWindowShownEvent) {
        MapContext.selectedMarker = event.marker
        selectedLoveSpotId = event.loveSpot?.id
    }

    suspend fun findSelectedSpotLocally(): LoveSpot? {
        return selectedLoveSpotId?.let { loveSpotService.findLocally(it) }
    }

    private suspend fun initClients() {
        authorizingRetrofit = Retrofit.Builder()
            .client(authorizingHttpClient())
            .baseUrl(API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        loverService = LoverService(
            loverApi = authorizingRetrofit.create(LoverApi::class.java),
            loveDao = loveDao,
            loveSpotDao = loveSpotDao,
            loveSpotReviewDao = loveSpotReviewDao,
            metadataStore = metadataStore,
            toaster = toaster
        )
        geoLocationService = GeoLocationService(
            geoLocationApi = authorizingRetrofit.create(GeoLocationApi::class.java),
            metadataStore = metadataStore,
            toaster = toaster
        )
        loveSpotService = LoveSpotService(
            loveSpotApi = authorizingRetrofit.create(LoveSpotApi::class.java),
            loveSpotDao = loveSpotDao,
            geoLocationService = geoLocationService,
            metadataStore = metadataStore,
            toaster = toaster,
        )
        loveSpotReviewService = LoveSpotReviewService(
            loveSpotReviewApi = authorizingRetrofit.create(LoveSpotReviewApi::class.java),
            loveSpotReviewDao = loveSpotReviewDao,
            loveSpotService = loveSpotService,
            metadataStore = metadataStore,
            toaster = toaster,
        )
        wishlistService = WishlistService(
            toaster = toaster,
            metadataStore = metadataStore,
            loveSpotService = loveSpotService,
            wishlistApi = authorizingRetrofit.create(WishlistApi::class.java),
            wishlistItemDao = wishlistItemDao
        )
        loveService = LoveService(
            loveApi = authorizingRetrofit.create(LoveApi::class.java),
            loveDao = loveDao,
            wishlistService = wishlistService,
            loveSpotReviewService = loveSpotReviewService,
            metadataStore = metadataStore,
            context = applicationContext,
            toaster = toaster,
        )
        loveSpotReportService = LoveSpotReportService(
            loveSpotReportApi = authorizingRetrofit.create(LoveSpotReportApi::class.java),
            adminApi = authorizingRetrofit.create(AdminApi::class.java),
            loveSpotService = loveSpotService,
            loverService = loverService,
            loveService = loveService,
            metadataStore = metadataStore,
            toaster = toaster,
        )
        partnershipService = PartnershipService(
            partnershipApi = authorizingRetrofit.create(PartnershipApi::class.java),
            partnershipDao = partnershipDao,
            metadataStore = metadataStore,
            toaster = toaster
        )
        loveSpotPhotoService = LoveSpotPhotoService(
            loveSpotPhotoApi = authorizingRetrofit.create(LoveSpotPhotoApi::class.java),
            loveSpotService = loveSpotService,
            toaster = toaster
        )
        if (metadataStore.isLoggedIn()) {
            val user = metadataStore.getUser()
            userId = user.id
            isAdmin = metadataStore.isAdmin(user)
        } else {
            userId = 0
        }
    }

    private suspend fun fetchUserData() {
        if (userId != 0L) {
            loverService.getRanks()
            loveSpotRisks = loveSpotService.getRisks()
            loveService.list()
            loveSpotReviewService.getReviewsByLover()
            geoLocationService.getCities()
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
                            .addHeader(
                                X_CLIENT_SECRET_HEADER,
                                getString(R.string.lovemap_client_secret)
                            )
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
        .callTimeout(Duration.ofMinutes(5))
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(Duration.ofMinutes(5))
        .writeTimeout(Duration.ofMinutes(5))

    companion object {
        var INSTANCE = AppContext()
    }

    suspend fun onLogin() {
        runBlocking {
            initClients()
        }
        withContext(Dispatchers.IO) {
            fetchUserData()
        }
    }

    suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            metadataStore.deleteAll()
            loveDao.delete(*loveDao.getAll().toTypedArray())
            loveSpotDao.delete(*loveSpotDao.getAll().toTypedArray())
            loveSpotReviewDao.delete(*loveSpotReviewDao.getAll().toTypedArray())
            partnershipDao.delete(*partnershipDao.getAll().toTypedArray())
            wishlistItemDao.delete(*wishlistItemDao.getAll().toTypedArray())
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        MapContext.locationEnabled = true
        val locationRequest = LocationRequest.create()
            .setInterval(60 * 1000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    MapContext.lastLocation = locationResult.lastLocation?.let {
                        com.javadocmd.simplelatlng.LatLng(
                            it.latitude,
                            it.longitude
                        )
                    }
                }
            },
            Looper.myLooper()
        )
    }
}
