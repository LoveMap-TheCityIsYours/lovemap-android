package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLovespotItemBinding
import com.lovemap.lovemapandroid.ui.data.LoveSpotHolder
import com.lovemap.lovemapandroid.ui.utils.LoveSpotDetailsUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotRecyclerViewAdapter(
    private val values: List<LoveSpotHolder>
) : RecyclerView.Adapter<LoveSpotRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLovespotItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loveSpot = values[position]
        holder.loveSpotItemName.text = loveSpot.name
        holder.loveSpotItemDescription.text = loveSpot.description
        LoveSpotDetailsUtils.setRating(
            loveSpot.averageRating,
            holder.loveSpotItemRating
        )
        LoveSpotDetailsUtils.setAvailability(
            loveSpot.availability,
            AppContext.INSTANCE.applicationContext,
            holder.lostSpotItemAvailability
        )
        MainScope().launch {
            LoveSpotDetailsUtils.setRisk(
                loveSpot.averageDanger,
                AppContext.INSTANCE.loveSpotService,
                AppContext.INSTANCE.applicationContext,
                holder.loveSpotItemRisk
            )
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLovespotItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        val loveSpotItemName: TextView = binding.loveSpotItemName
        val loveSpotItemRating: RatingBar = binding.loveSpotItemRating
        val lostSpotItemAvailability: TextView = binding.lostSpotItemAvailability
        val loveSpotItemRisk: TextView = binding.loveSpotItemRisk
        val loveSpotItemDescription: TextView = binding.loveSpotItemDescription

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val loveSpotId = values[absoluteAdapterPosition].id
            AppContext.INSTANCE.selectedLoveSpotId = loveSpotId
            v?.context?.startActivity(
                Intent(v.context, LoveSpotDetailsActivity::class.java)
            )
        }
    }
}