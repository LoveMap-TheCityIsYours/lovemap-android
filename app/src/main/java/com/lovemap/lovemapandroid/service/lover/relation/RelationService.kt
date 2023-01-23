package com.lovemap.lovemapandroid.service.lover.relation

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.api.lover.relation.RelationApi
import com.lovemap.lovemapandroid.api.newsfeed.NewsFeedItemResponse
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.service.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RelationService(
    private val relationApi: RelationApi,
    private val metadataStore: MetadataStore,
    private val toaster: Toaster,
) {

    suspend fun getFollowingNewsFeed(): List<NewsFeedItemResponse> {
        return withContext(Dispatchers.IO) {
            val userId = AppContext.INSTANCE.userId
            val call = relationApi.getFollowingNewsFeed(userId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_load_news_feed)
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                response.body()!!
            } else {
                toaster.showResponseError(response)
                emptyList()
            }
        }
    }

    suspend fun followLover(targetLoverId: Long): LoverRelationsDto? {
        return withContext(Dispatchers.IO) {
            val userId = AppContext.INSTANCE.userId
            val call = relationApi.followLover(userId, targetLoverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_follow_lover)
                return@withContext null
            }
            if (response.isSuccessful) {
                val result = response.body()!!
                metadataStore.saveLover(result)
                result
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun unfollowLover(targetLoverId: Long): LoverRelationsDto? {
        return withContext(Dispatchers.IO) {
            val userId = AppContext.INSTANCE.userId
            val call = relationApi.unfollowLover(userId, targetLoverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_unfollow_lover)
                return@withContext null
            }
            if (response.isSuccessful) {
                val result = response.body()!!
                metadataStore.saveLover(result)
                result
            } else {
                toaster.showResponseError(response)
                null
            }
        }
    }

    suspend fun getFollowers(): List<LoverViewWithoutRelationDto> {
        return withContext(Dispatchers.IO) {
            val userId = AppContext.INSTANCE.userId
            val call = relationApi.getFollowers(userId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_get_followers)
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                response.body()!!
            } else {
                toaster.showResponseError(response)
                emptyList()
            }
        }
    }

    suspend fun removeFollower(targetLoverId: Long): List<LoverViewWithoutRelationDto> {
        return withContext(Dispatchers.IO) {
            val userId = AppContext.INSTANCE.userId
            val call = relationApi.removeFollower(userId, targetLoverId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_remove_follower)
                return@withContext emptyList()
            }
            if (response.isSuccessful) {
                response.body()!!
            } else {
                toaster.showResponseError(response)
                emptyList()
            }
        }
    }

}
