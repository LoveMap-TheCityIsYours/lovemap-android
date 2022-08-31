package com.lovemap.lovemapandroid.ui.main.lovespot.list

import android.content.Intent
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLovespotItemBinding
import com.lovemap.lovemapandroid.ui.data.LoveSpotHolder
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation

class LoveSpotRecyclerViewAdapter(
    val values: MutableList<LoveSpotHolder>
) : RecyclerView.Adapter<LoveSpotRecyclerViewAdapter.ViewHolder>() {

    var lastPosition: Int = -1
    var position: Int = 0
    val contextMenuIds = LoveSpotUtils.ContextMenuIds()

    private val appContext = AppContext.INSTANCE

    fun updateData(data: List<LoveSpotHolder>) {
        values.clear()
        values.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLovespotItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loveSpot = values[position]
        holder.loveSpotItem = loveSpot
        holder.loveSpotItemName.text = loveSpot.name
        holder.loveSpotItemDescription.text = loveSpot.description
        LoveSpotUtils.setRating(
            loveSpot.averageRating,
            holder.loveSpotItemRating
        )
        LoveSpotUtils.setAvailability(
            loveSpot.availability,
            holder.loveSpotItemAvailability
        )
        LoveSpotUtils.setRisk(
            loveSpot.averageDanger,
            holder.loveSpotItemRisk
        )
        LoveSpotUtils.setType(
            loveSpot.type,
            holder.loveSpotItemType
        )
        LoveSpotUtils.setTypeImage(
            loveSpot.type,
            holder.loveSpotItemTypeImage
        )
        LoveSpotUtils.setDistance(
            loveSpot.distanceKm,
            holder.loveSpotItemDistance
        )
        holder.itemView.setOnLongClickListener {
            this.position = holder.absoluteAdapterPosition
            false
        }
        setListItemAnimation(holder.itemView, position, lastPosition)
        if (position > lastPosition) {
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLovespotItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        View.OnCreateContextMenuListener {

        val loveSpotItemName: TextView = binding.loveSpotItemName
        val loveSpotItemRating: RatingBar = binding.loveSpotItemRating
        val loveSpotItemAvailability: TextView = binding.loveSpotItemAvailability
        val loveSpotItemType: TextView = binding.loveSpotItemType
        val loveSpotItemRisk: TextView = binding.loveSpotItemRisk
        val loveSpotItemDistance: TextView = binding.loveSpotItemDistance
        val loveSpotItemDescription: TextView = binding.loveSpotItemDescription
        val loveSpotItemTypeImage: ImageView = binding.loveSpotItemTypeImage
        lateinit var loveSpotItem: LoveSpotHolder

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnCreateContextMenuListener(this)
        }

        override fun onClick(v: View?) {
            val loveSpotId = values[absoluteAdapterPosition].id
            appContext.selectedLoveSpotId = loveSpotId
            appContext.selectedLoveSpot = null
            appContext.selectedMarker = null
            v?.context?.startActivity(
                Intent(v.context, LoveSpotDetailsActivity::class.java)
            )
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            LoveSpotUtils.onCreateContextMenu(
                menu,
                contextMenuIds,
                values[absoluteAdapterPosition].name,
                values[absoluteAdapterPosition].addedBy
            )
        }
    }
}