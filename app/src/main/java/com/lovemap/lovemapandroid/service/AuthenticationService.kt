package com.lovemap.lovemapandroid.service

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.ErrorCode.*
import com.lovemap.lovemapandroid.api.authentication.AuthenticationApi
import com.lovemap.lovemapandroid.api.authentication.CreateLoverRequest
import com.lovemap.lovemapandroid.api.authentication.LoginLoverRequest
import com.lovemap.lovemapandroid.api.getErrorMessages
import com.lovemap.lovemapandroid.data.metadata.LoggedInUser
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthenticationService(
    private val authenticationApi: AuthenticationApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
    private val context: Context
) {
    private val mainLooper = Looper.getMainLooper()

    suspend fun login(email: String, password: String, activity: Activity): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.login(
                LoginLoverRequest(email, password)
            )
            val loadingBarShower = LoadingBarShower(activity)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                loadingBarShower.onResponse()
                toaster.showToast(e.message ?: "null")
                return@withContext null
            }
            if (response.isSuccessful) {
                loadingBarShower.onResponse()
                loggedInUser = metadataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                loadingBarShower.onResponse()
                showErrorToast(response)
            }
            loggedInUser
        }
    }

    suspend fun register(userName: String, email: String, password: String, activity: Activity): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.register(
                CreateLoverRequest(userName, password, email)
            )
            val loadingBarShower = LoadingBarShower(activity)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                loadingBarShower.onResponse()
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                loadingBarShower.onResponse()
                loggedInUser = metadataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                loadingBarShower.onResponse()
                showErrorToast(response)
            }
            loggedInUser
        }
    }

    private fun showErrorToast(response: Response<out Any>) {
        val error = response.getErrorMessages().first()
        val errorCode = error.errorCode
        Handler(mainLooper).post {
            val message: String = when (errorCode) {
                UserOccupied -> {
                    context.getString(R.string.userOccupied)
                }
                EmailOccupied -> {
                    context.getString(R.string.emailOccupied)
                }
                InvalidCredentialsEmail -> {
                    context.getString(R.string.invalidCredentials)
                }
                InvalidCredentialsUser -> {
                    context.getString(R.string.invalidCredentials)
                }
                else -> {
                    context.getString(R.string.invalidCredentials)
                }
            }
            toaster.showToast(message)
        }
    }
}