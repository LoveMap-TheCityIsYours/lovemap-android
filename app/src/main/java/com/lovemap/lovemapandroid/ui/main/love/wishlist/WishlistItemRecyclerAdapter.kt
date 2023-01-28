package com.lovemap.lovemapandroid.ui.main.love.wishlist

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.databinding.FragmentWishlistItemBinding
import com.lovemap.lovemapandroid.ui.data.WishlistItemHolder
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.utils.AlertDialogUtils
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation
import com.lovemap.lovemapandroid.utils.instantOfApiString
import com.lovemap.lovemapandroid.utils.toFormattedDateTime
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WishlistItemRecyclerAdapter(
    val values: MutableList<WishlistItemHolder>,
    val recyclerView: RecyclerView,
    val activity: Activity
) : RecyclerView.Adapter<WishlistItemRecyclerAdapter.ViewHolder>() {

    var lastPosition: Int = -1
    var position: Int = 0

    private val appContext = AppContext.INSTANCE
    private val wishlistService = appContext.wishlistService

    fun updateData(data: List<WishlistItemHolder>) {
        values.clear()
        values.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentWishlistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = values[position]
        viewHolder.name.text = item.loveSpot.name
        viewHolder.description.text = item.loveSpot.description
        LoveSpotUtils.setRating(item.loveSpot.averageRating, viewHolder.rating)
        LoveSpotUtils.setType(item.loveSpot.type, viewHolder.type)
        LoveSpotUtils.setTypeImage(item.loveSpot.type, viewHolder.typeImage)
        LoveSpotUtils.setAvailability(item.loveSpot.availability, viewHolder.availability)
        LoveSpotUtils.setRisk(item.loveSpot.averageDanger, viewHolder.risk)
        LoveSpotUtils.setDistance(
            item.loveSpot,
            viewHolder.distance
        )

        viewHolder.addedAt.text =
            instantOfApiString(item.wishlistItem.addedAt).toFormattedDateTime()
        viewHolder.item = item
        viewHolder.itemCounter.text = "${item.number}."

        if (viewHolder.item.expanded) {
            viewHolder.collapsibleView.visibility = View.VISIBLE
        } else {
            viewHolder.collapsibleView.visibility = View.GONE
        }

        setListItemAnimation(viewHolder.itemView, position, lastPosition)
        if (position > lastPosition) {
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentWishlistItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        val name: TextView = binding.wishlistItemName
        val description: TextView = binding.wishlistItemDescription
        val rating: RatingBar = binding.wishlistItemRating
        val type: TextView = binding.wishlistItemType
        val typeImage: ImageView = binding.wishlistItemTypeImage
        val availability: TextView = binding.wishlistItemAvailability
        val risk: TextView = binding.wishlistItemRisk
        val distance: TextView = binding.wishlistItemDistance

        val addedAt: TextView = binding.wishlistItemAddedAt
        val itemCounter: TextView = binding.wishlistItemCounter
        val collapsibleView: LinearLayout = binding.wishlistItemCollapsibleView
        val openSpotButton: ExtendedFloatingActionButton = binding.wishlistItemOpenSpot
        val recordLoveButton: ExtendedFloatingActionButton = binding.wishlistItemRecordLove
        val deleteButton: ExtendedFloatingActionButton = binding.wishlistItemDeleteButton

        lateinit var item: WishlistItemHolder

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (item.expanded) {
                item.expanded = false
                collapsibleView.visibility = View.GONE
                notifyItemChanged(absoluteAdapterPosition + 1)
            } else {
                item.expanded = true
                collapsibleView.visibility = View.VISIBLE
                notifyItemChanged(absoluteAdapterPosition - 1)
            }

            openSpotButton.setOnClickListener {
                appContext.selectedLoveSpotId = item.loveSpot.id
                MapContext.selectedMarker = null
                view.context?.startActivity(
                    Intent(view.context, LoveSpotDetailsActivity::class.java)
                )
            }

            recordLoveButton.setOnClickListener {
                appContext.selectedLoveSpotId = item.loveSpot.id
                MapContext.selectedMarker = null
                view.context?.startActivity(
                    Intent(view.context, RecordLoveActivity::class.java)
                )
            }

            deleteButton.setOnClickListener {
                AlertDialogUtils.newDialog(
                    activity,
                    R.string.remove_from_wishlist_title,
                    R.string.remove_from_wishlist_message,
                    {
                        MainScope().launch {
                            wishlistService.removeWishlistItem(item.wishlistItem.wishlistItemId)
                        }
                    })
            }
        }
    }

}
