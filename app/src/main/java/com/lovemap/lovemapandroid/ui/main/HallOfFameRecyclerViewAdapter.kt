package com.lovemap.lovemapandroid.ui.main

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLoveItemBinding
import com.lovemap.lovemapandroid.databinding.HallOfFameItemBinding
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.ui.relations.ViewOtherLoverActivity
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation

class HallOfFameRecyclerViewAdapter(
    private val context: Context,
    val values: MutableList<LoverViewWithoutRelationDto>
) : RecyclerView.Adapter<HallOfFameRecyclerViewAdapter.ViewHolder>() {

    var lastPosition: Int = -1
    var position: Int = 0

    fun updateData(data: List<LoverViewWithoutRelationDto>) {
        values.clear()
        values.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HallOfFameItemBinding.inflate(
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
        val lover = values[position]
        holder.lover = lover
        holder.hallOfFameItemPosition.text = "${position + 1}."
        holder.hallOfFameItemLoverName.text = lover.displayName.takeIf { lover.publicProfile }
            ?: context.getString(R.string.privateProfile)

        ProfileUtils.setRanks(
            points = lover.points,
            currentRank = holder.hallOfFameItemLoverRank,
        )

        holder.hallOfFameItemLoverPoints.text = "${lover.points}"

        holder.hallOfFameItemOpenLoverImage.visibility = if (lover.publicProfile) {
            holder.itemView.setOnClickListener {
                LoverService.otherLoverId = lover.id
                context.startActivity(Intent(context, ViewOtherLoverActivity::class.java))
            }
            View.VISIBLE
        } else {
            View.GONE
        }

        setListItemAnimation(holder.itemView, position, lastPosition)
        if (position > lastPosition) {
            lastPosition = position
        }

        holder.itemView.setOnLongClickListener {
            this.position = holder.absoluteAdapterPosition
            false
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: HallOfFameItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val hallOfFameItemPosition: TextView = binding.hallOfFameItemPosition
        val hallOfFameItemLoverName: TextView = binding.hallOfFameItemLoverName
        val hallOfFameItemOpenLoverImage: ImageView = binding.hallOfFameItemOpenLoverImage
        val hallOfFameItemLoverPoints: TextView = binding.hallOfFameItemLoverPoints
        val hallOfFameItemLoverRank: TextView = binding.hallOfFameItemLoverRank

        lateinit var lover: LoverViewWithoutRelationDto
    }

}