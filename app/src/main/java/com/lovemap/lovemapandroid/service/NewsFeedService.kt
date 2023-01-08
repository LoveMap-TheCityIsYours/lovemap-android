package com.lovemap.lovemapandroid.service

import android.util.Log
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedApi
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsFeedService(
    private val newsFeedApi: NewsFeedApi,
    private val toaster: Toaster
) {
    private val tag = "NewsFeedService"

    suspend fun getPage(page: Int, size: Int): List<NewsFeedItemResponse> {
        return withContext(Dispatchers.IO) {
            Log.i(tag, "getPage called with page '$page', size '$size'")
            val call = newsFeedApi.getPage(page, size)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_load_news_feed)
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                Log.i(tag, "getPage call successfull")
                response.body()!!
            } else {
                toaster.showResponseError(response)
                emptyList()
            }
        }
    }
}
