package com.smackmap.smackmapandroid.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.smackmap.smackmapandroid.api.authentication.AuthenticationService
import com.smackmap.smackmapandroid.api.authentication.CreateSmackerRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SmackmapBackendApi {
    fun hello(context: Context) {

        val mainLooper = Looper.getMainLooper()

        GlobalScope.launch {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.58:8090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val authenticationService = retrofit
                .create(AuthenticationService::class.java)
            val call = authenticationService.register(
                CreateSmackerRequest("banan1", "banan1", "banan1@banan.ba")
            )

            val response = call.execute()
            val result: String = response.body()?.toString() ?: response.message()

            Handler(mainLooper).post {
                val toast = Toast.makeText(context, result, Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }
}