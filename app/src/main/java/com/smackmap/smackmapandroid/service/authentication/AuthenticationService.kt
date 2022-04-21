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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class AuthenticationService(
    private val authenticationApi: AuthenticationApi,
    private val userDataStore: UserDataStore,
    private val context: Context
) {
    private val mainLooper = Looper.getMainLooper()

    fun login(email: String, password: String): Boolean {
        var success = false
        GlobalScope.async {
            val call = authenticationApi.login(
                LoginSmackerRequest(email, password)
            )

            val response = call.execute()
            success = response.isSuccessful
            if (success) {
                response.body()
                userDataStore.save(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                Handler(mainLooper).post {
                    val toast = Toast.makeText(context, response.message(), Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }
        return success
    }

    fun register(userName: String, email: String, password: String): Boolean {
        var success = false
        GlobalScope.async {
            val call = authenticationApi.register(
                CreateSmackerRequest(userName, password, email)
            )

            val response = call.execute()
            success = response.isSuccessful
            if (success) {
                response.body()
                userDataStore.save(
                    LoggedInUser.of(
                        response.body()!!,
                        response.headers()["authorization"]!!
                    )
                )
            } else {
                Handler(mainLooper).post {
                    val toast = Toast.makeText(context, response.message(), Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }
        return success
    }
}