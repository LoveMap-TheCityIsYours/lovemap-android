package com.lovemap.lovemapandroid.ui.lover

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewWithoutRelationDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLoverItemBinding
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.service.lover.relation.RelationService
import com.lovemap.lovemapandroid.ui.utils.AlertDialogUtils
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import com.lovemap.lovemapandroid.ui.utils.setListItemAnimation
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoverRecyclerViewAdapter(
    val activity: Activity,
    val type: Type,
    val values: MutableList<LoverViewWithoutRelationDto>
) : RecyclerView.Adapter<LoverRecyclerViewAdapter.ViewHolder>() {

    var lastPosition: Int = -1
    var position: Int = 0

    private val relationService = AppContext.INSTANCE.relationService

    fun updateData(data: List<LoverViewWithoutRelationDto>) {
        values.clear()
        values.addAll(data)
    }

    enum class Type {
        FOLLOWERS, FOLLOWINGS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLoverItemBinding.inflate(
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

        holder.itemView.setOnClickListener {
            LoverService.otherLoverId = lover.id
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, ViewOtherLoverActivity::class.java)
            )
        }

        holder.loverItemCounter.text = "${position + 1}."
        holder.loverItemName.text = lover.displayName
        holder.loverItemPoints.text = lover.points.toString()
        ProfileUtils.setRanks(lover.points, holder.loverItemRank)
        if (type == Type.FOLLOWERS && RelationService.LOVER_ID == AppContext.INSTANCE.userId) {
            holder.loverItemRemoveFollower.visibility = View.VISIBLE
            holder.loverItemRemoveFollower.setOnClickListener {
                AlertDialogUtils.newDialog(
                    activity,
                    R.string.remove_follower_title,
                    R.string.remove_follower_message,
                    {
                        MainScope().launch {
                            val followers = relationService.removeFollower(lover.id)
                            updateData(followers)
                            notifyDataSetChanged()
                        }
                    })
            }
        } else {
            holder.loverItemRemoveFollower.visibility = View.GONE
            holder.loverItemRemoveFollower.setOnClickListener {}
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

    inner class ViewHolder(binding: FragmentLoverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val loverItemName: TextView = binding.loverItemName
        val loverItemCounter: TextView = binding.loverItemCounter
        val loverItemPoints: TextView = binding.loverItemPoints
        val loverItemRank: TextView = binding.loverItemRank
        val loverItemRemoveFollower: FloatingActionButton = binding.loverItemRemoveFollower

        lateinit var lover: LoverViewWithoutRelationDto
    }
}