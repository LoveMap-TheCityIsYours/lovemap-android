package com.smackmap.smackmapandroid.service.authentication

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.smackmap.smackmapandroid.api.authentication.AuthenticationApi
import com.smackmap.smackmapandroid.api.authentication.CreateSmackerRequest
import com.smackmap.smackmapandroid.api.authentication.LoginSmackerRequest
import com.smackmap.smackmapandroid.data.UserDataStore
import com.smackmap.smackmapandroid.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthenticationService(
    private val authenticationApi: AuthenticationApi,
    private val userDataStore: UserDataStore,
    private val context: Context
) {
    private val mainLooper = Looper.getMainLooper()

    suspend fun login(email: String, password: String): LoggedInUser? {
        var loggedInUser: LoggedInUser? = null
        return withContext(Dispatchers.IO) {
            val call = authenticationApi.login(
                LoginSmackerRequest(email, password)
            )

            val response = call.execute()
            if (response.isSuccessful) {
                response.body()
                loggedInUser = userDataStore.save(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                Handler(mainLooper).post {
                    val toast = Toast.makeText(
                        context,
                        response.errorBody()?.string(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
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

            val response = call.execute()
            if (response.isSuccessful) {
                loggedInUser = userDataStore.save(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                Handler(mainLooper).post {
                    val toast = Toast.makeText(
                        context,
                        response.errorBody()?.string(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
            }
            loggedInUser
        }
    }
}