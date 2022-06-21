package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Intent
import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLovespotItemBinding
import com.lovemap.lovemapandroid.ui.data.LoveSpotHolder
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.utils.canEditLoveSpot

class LoveSpotRecyclerViewAdapter(
    val values: MutableList<LoveSpotHolder>
) : RecyclerView.Adapter<LoveSpotRecyclerViewAdapter.ViewHolder>() {

    var position: Int = 0

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

    override fun onViewRecycled(holder: LoveSpotRecyclerViewAdapter.ViewHolder) {
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
        holder.itemView.setOnLongClickListener {
            this.position = holder.absoluteAdapterPosition
            false
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
        val loveSpotItemDescription: TextView = binding.loveSpotItemDescription
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
            menu.setHeaderTitle(loveSpotItem.name)
            if (canEditLoveSpot(loveSpotItem.addedBy)) {
                menu.add(Menu.NONE, EDIT_LOVE_SPOT_MENU_ID, Menu.NONE, R.string.edit)
            }
            menu.add(Menu.NONE, WISHLIST_LOVE_SPOT_MENU_ID, Menu.NONE, R.string.to_wishlist)
            menu.add(Menu.NONE, MAKE_LOVE_LOVE_SPOT_MENU_ID, Menu.NONE, R.string.make_love)
            menu.add(Menu.NONE, REPORT_LOVE_SPOT_MENU_ID, Menu.NONE, R.string.report_spot)
        }
    }

    companion object {
        const val EDIT_LOVE_SPOT_MENU_ID = 10
        const val WISHLIST_LOVE_SPOT_MENU_ID = 11
        const val MAKE_LOVE_LOVE_SPOT_MENU_ID = 12
        const val REPORT_LOVE_SPOT_MENU_ID = 13
    }
}