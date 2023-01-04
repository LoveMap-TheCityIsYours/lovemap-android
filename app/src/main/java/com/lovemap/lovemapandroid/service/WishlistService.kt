package com.lovemap.lovemapandroid.service

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.wishlist.WishlistApi
import com.lovemap.lovemapandroid.api.lover.wishlist.WishlistResponse
import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItem
import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItemDao
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WishlistService(
    private val toaster: Toaster,
    private val metadataStore: MetadataStore,
    private val wishlistApi: WishlistApi,
    private val wishlistItemDao: WishlistItemDao,
) {

    suspend fun list(): List<WishlistItem> {
        return withContext(Dispatchers.IO) {
            val localWishlist = wishlistItemDao.getAllOrderedByDate()
            val call = wishlistApi.getWishlist(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                return@withContext localWishlist
            }
            if (response.isSuccessful) {
                updateLocalWishlist(response, localWishlist)
            } else {
                toaster.showResponseError(response)
                localWishlist
            }
        }
    }

    suspend fun addToWishlist(loveSpotId: Long): List<WishlistItem> {
        return withContext(Dispatchers.IO) {
            val localWishlist = wishlistItemDao.getAllOrderedByDate()
            val call = wishlistApi.addToWishlist(metadataStore.getUser().id, loveSpotId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_add_to_wishlist)
                return@withContext localWishlist
            }
            if (response.isSuccessful) {
                toaster.showToast(R.string.added_to_wishlist)
                updateLocalWishlist(response, localWishlist)
            } else {
                toaster.showResponseError(response)
                localWishlist
            }
        }
    }

    suspend fun removeLoveSpotFromWishlist(loveSpotId: Long): List<WishlistItem> {
        return withContext(Dispatchers.IO) {
            val localWishlist = wishlistItemDao.getAllOrderedByDate()
            val call = wishlistApi.addToWishlist(metadataStore.getUser().id, loveSpotId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_add_to_wishlist)
                return@withContext localWishlist
            }
            if (response.isSuccessful) {
                updateLocalWishlist(response, localWishlist)
            } else {
                toaster.showResponseError(response)
                localWishlist
            }
        }
    }

    private fun updateLocalWishlist(
        response: Response<List<WishlistResponse>>,
        localWishlist: List<WishlistItem>
    ): List<WishlistItem> {
        val serverWishlist = response.body()!!.map { it.toWishlistItem() }
        val localWishlistSet = HashSet(localWishlist)
        val serverWishlistSet = HashSet(serverWishlist)
        val deletedWishlistElements = localWishlistSet.subtract(serverWishlistSet)
        wishlistItemDao.delete(*deletedWishlistElements.toTypedArray())
        wishlistItemDao.insert(*serverWishlistSet.toTypedArray())
        return serverWishlist
    }
}
