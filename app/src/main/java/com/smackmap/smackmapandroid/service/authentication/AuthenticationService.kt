package com.smackmap.smackmapandroid.service.authentication

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.api.ErrorCode.*
import com.smackmap.smackmapandroid.api.authentication.AuthenticationApi
import com.smackmap.smackmapandroid.api.authentication.CreateSmackerRequest
import com.smackmap.smackmapandroid.api.authentication.LoginSmackerRequest
import com.smackmap.smackmapandroid.api.getErrorMessages
import com.smackmap.smackmapandroid.data.UserDataStore
import com.smackmap.smackmapandroid.data.model.LoggedInUser
import com.smackmap.smackmapandroid.service.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthenticationService(
    private val authenticationApi: AuthenticationApi,
    private val userDataStore: UserDataStore,
    private val toaster: Toaster,
    private val context: Context
) {
    private val mainLooper = Looper.getMainLooper()

    suspend fun login(email: String, password: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.login(
                LoginSmackerRequest(email, password)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                loggedInUser = userDataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                showErrorToast(response)
            }
            loggedInUser
        }
    }

    suspend fun register(userName: String, email: String, password: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.register(
                CreateSmackerRequest(userName, password, email)
            )
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showNoServerToast()
                return@withContext null
            }
            if (response.isSuccessful) {
                loggedInUser = userDataStore.login(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
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