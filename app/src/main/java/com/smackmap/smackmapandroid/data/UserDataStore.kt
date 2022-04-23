package com.smackmap.smackmapandroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smackmap.smackmapandroid.data.model.LoggedInUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserDataStore(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "UserDataStore")
    private val gson = GsonBuilder().create()

    suspend fun save(user: LoggedInUser): LoggedInUser {
        val userKey = stringPreferencesKey("user")
        context.dataStore.edit { dataStore ->
            dataStore[userKey] = gson.toJson(user)
        }
        return user
    }

    suspend fun isLoggedIn(): Boolean {
        val userKey = stringPreferencesKey("user")
        return context.dataStore.data.map { dataStore ->
            dataStore[userKey] != null
        }.first()
    }

    suspend fun get(): LoggedInUser {
        val userKey = stringPreferencesKey("user")
        return context.dataStore.data.map { dataStore ->
            gson.fromJson(dataStore[userKey], LoggedInUser::class.java)
        }.first()
    }

    suspend fun delete() {
        val userKey = stringPreferencesKey("user")
        context.dataStore.edit {
            it.clear()
        }
    }

//    suspend fun get2(): LoggedInUser {
//        var loggedInUser: LoggedInUser
//        GlobalScope.async {
//            val userKey = stringPreferencesKey("user")
//            loggedInUser = context.dataStore.data.map { dataStore ->
//                gson.fromJson(dataStore[userKey], LoggedInUser::class.java)
//            }.first()
//        }
//        return loggedInUser
//    }
}