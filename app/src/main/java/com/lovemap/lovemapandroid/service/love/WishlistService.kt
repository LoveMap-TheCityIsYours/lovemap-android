package com.lovemap.lovemapandroid.service.love

import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.wishlist.WishlistApi
import com.lovemap.lovemapandroid.api.lover.wishlist.WishlistResponse
import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItem
import com.lovemap.lovemapandroid.data.lover.wishlist.WishlistItemDao
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.service.Toaster
import com.lovemap.lovemapandroid.service.lovespot.LoveSpotService
import com.lovemap.lovemapandroid.ui.data.WishlistItemDto
import com.lovemap.lovemapandroid.ui.data.WishlistItemHolder
import com.lovemap.lovemapandroid.ui.events.WishlistUpdatedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.util.*

class WishlistService(
    private val toaster: Toaster,
    private val metadataStore: MetadataStore,
    private val loveSpotService: LoveSpotService,
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
                mergeResponseAndLocalWishlist(response, localWishlist).first
            } else {
                toaster.showResponseError(response)
                localWishlist
            }
        }
    }

    suspend fun getWishlistHolderList(): List<WishlistItemHolder> {
        val wishlistDtoList = getWishlistDtoList()
        return convertDtosToHolders(wishlistDtoList)
    }

    fun convertDtosToHolders(wishlistDtoList: SortedSet<WishlistItemDto>) =
        wishlistDtoList.mapIndexed { index, wishlistItemDto ->
            WishlistItemHolder(
                wishlistItem = wishlistItemDto.wishlistItem,
                loveSpot = wishlistItemDto.loveSpot,
                number = wishlistDtoList.size - index
            )
        }

    suspend fun getWishlistDtoList(): SortedSet<WishlistItemDto> {
        return withContext(Dispatchers.IO) {
            val localWishlist = wishlistItemDao.getAllOrderedByDate()
            val localResults = getLocalWishlistDtoResult(localWishlist)
            val call = wishlistApi.getWishlist(metadataStore.getUser().id)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                return@withContext localResults
            }
            if (response.isSuccessful) {
                mergeResponseAndLocalWishlist(response, localWishlist).second
            } else {
                toaster.showResponseError(response)
                localResults
            }
        }
    }

    private suspend fun getLocalWishlistDtoResult(localWishlist: List<WishlistItem>): SortedSet<WishlistItemDto> {
        val wishlistItemsByLoveSpotIds: Map<Long, WishlistItem> =
            localWishlist.associateBy { it.loveSpotId }
        val loveSpots: List<LoveSpot> =
            loveSpotService.listSpotsLocallyByIds(wishlistItemsByLoveSpotIds.keys)
        val localResults = loveSpots.map {
            WishlistItemDto(
                wishlistItem = wishlistItemsByLoveSpotIds[it.id]
                    ?: throw IllegalStateException("Missing Wishlist Item!"),
                loveSpot = it
            )
        }.toSortedSet()
        return localResults
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
                val resultPair = mergeResponseAndLocalWishlist(response, localWishlist)
                EventBus.getDefault()
                    .post(WishlistUpdatedEvent(convertDtosToHolders(resultPair.second)))
                resultPair.first
            } else {
                toaster.showResponseError(response)
                localWishlist
            }
        }
    }

    suspend fun removeWishlistItem(wishlistItemId: Long): List<WishlistItem> {
        return withContext(Dispatchers.IO) {
            val localWishlist = wishlistItemDao.getAllOrderedByDate()
            val call = wishlistApi.deleteWishlistItem(metadataStore.getUser().id, wishlistItemId)
            val response = try {
                call.execute()
            } catch (e: Exception) {
                toaster.showToast(R.string.failed_to_remove_from_wishlist)
                return@withContext localWishlist
            }
            if (response.isSuccessful) {
                val resultPair = mergeResponseAndLocalWishlist(response, localWishlist)
                EventBus.getDefault()
                    .post(WishlistUpdatedEvent(convertDtosToHolders(resultPair.second)))
                resultPair.first
            } else {
                toaster.showResponseError(response)
                localWishlist
            }
        }
    }

    suspend fun removeLocallyByLoveSpot(loveSpotId: Long): List<WishlistItem> {
        return withContext(Dispatchers.IO) {
            wishlistItemDao.getByLoveSpotId(loveSpotId)?.let { wishlistItem ->
                wishlistItemDao.delete(wishlistItem)
                val wishlist = wishlistItemDao.getAllOrderedByDate()
                val wishlistItemsToLoveSpotIds = wishlist.associateBy { it.loveSpotId }

                val loveSpots: List<LoveSpot> = loveSpotService
                    .listSpotsLocallyByIds(wishlistItemsToLoveSpotIds.keys)

                runCatching {
                    loveSpots.map {
                        WishlistItemDto(wishlistItemsToLoveSpotIds[it.id]!!, it)
                    }.toSortedSet()
                }.onSuccess { sortedWishlist ->
                    EventBus.getDefault().post(WishlistUpdatedEvent(convertDtosToHolders(sortedWishlist)))
                }

                wishlist
            } ?: wishlistItemDao.getAllOrderedByDate()
        }
    }

    private suspend fun mergeResponseAndLocalWishlist(
        response: Response<List<WishlistResponse>>,
        localWishlist: List<WishlistItem>
    ): Pair<List<WishlistItem>, SortedSet<WishlistItemDto>> {
        val wishlistResult = response.body()!!
        val wishlistDtos = wishlistResult.map { WishlistItemDto(it.toWishlistItem(), it.loveSpot) }
            .toSortedSet()
        val serverWishlist = wishlistResult.map { it.toWishlistItem() }
        val localWishlistSet = HashSet(localWishlist)
        val serverWishlistSet = HashSet(serverWishlist)
        val deletedWishlistElements = localWishlistSet.subtract(serverWishlistSet)
        wishlistItemDao.delete(*deletedWishlistElements.toTypedArray())
        wishlistItemDao.insert(*serverWishlistSet.toTypedArray())
        loveSpotService.saveAll(wishlistResult.map { it.loveSpot })
        return Pair(serverWishlist, wishlistDtos)
    }
}
