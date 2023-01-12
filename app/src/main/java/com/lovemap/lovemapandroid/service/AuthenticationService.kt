package com.lovemap.lovemapandroid.service

import android.content.Context
import android.os.Looper
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.authentication.*
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.LoggedInUser
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthenticationService(
    private val authenticationApi: AuthenticationApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
    private val context: Context
) {
    private val mainLooper = Looper.getMainLooper()
    private val appContext = AppContext.INSTANCE

    suspend fun login(email: String, password: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.login(
                LoginLoverRequest(email, password)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(e.message ?: "null")
                return@withContext null
            }
            if (response.isSuccessful) {
                loggedInUser = metadataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                toaster.showResponseError(response)
            }
            loggedInUser
        }
    }

    suspend fun register(userName: String, email: String, password: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val registrationCountry = appContext.userCurrentCountry
            val call = authenticationApi.register(
                CreateLoverRequest(userName, password, email, registrationCountry)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                loggedInUser = metadataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                toaster.showResponseError(response)
            }
            loggedInUser
        }
    }

    suspend fun requestPasswordReset(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.requestPasswordReset(
                ResetPasswordRequest(email)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(e.message ?: "null")
                return@withContext false
            }
            if (response.isSuccessful) {
                toaster.showToast(R.string.reset_code_requested)
                return@withContext true
            } else {
                toaster.showResponseError(response)
                return@withContext false
            }
        }
    }

    suspend fun newPassword(email: String, resetCode: String, newPassword: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.newPassword(
                NewPasswordRequest(email, resetCode, newPassword)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(e.message ?: "null")
                return@withContext null
            }
            if (response.isSuccessful) {
                loggedInUser = metadataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                toaster.showResponseError(response)
            }
            loggedInUser
        }
    }

    suspend fun facebookLogin(email: String, facebookId: String, token: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val registrationCountry = appContext.userCurrentCountry
            val call = authenticationApi.facebookLogin(
                FacebookAuthenticationRequest(email, facebookId, token, registrationCountry)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(e.message ?: "null")
                return@withContext null
            }
            if (response.isSuccessful) {
                loggedInUser = metadataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                toaster.showResponseError(response)
            }
            loggedInUser
        }
    }
}