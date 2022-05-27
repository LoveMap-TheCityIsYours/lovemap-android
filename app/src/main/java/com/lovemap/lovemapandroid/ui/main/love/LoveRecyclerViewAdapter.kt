package com.lovemap.lovemapandroid.ui.main.love

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLoveItemBinding
import com.lovemap.lovemapandroid.ui.data.LoveHolder
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity

class LoveRecyclerViewAdapter(
    private val values: List<LoveHolder>,
    private val isClickable: Boolean
) : RecyclerView.Adapter<LoveRecyclerViewAdapter.ViewHolder>() {

    private val appContext = AppContext.INSTANCE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLoveItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val love = values[position]
        holder.loveItemName.text = love.name
        holder.loveItemPartner.text = love.partner
        holder.loveItemNote.text = love.note
        holder.loveItemHappenedAt.text = love.happenedAt
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLoveItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val loveItemName: TextView = binding.loveItemName
        val loveItemPartner: TextView = binding.loveItemPartner
        val loveItemNote: TextView = binding.loveItemNote
        val loveItemHappenedAt: TextView = binding.loveItemHappenedAt

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (isClickable) {
                val loveSpotId = values[absoluteAdapterPosition].loveSpotId
                appContext.selectedLoveSpotId = loveSpotId
                appContext.selectedLoveSpot = null
                appContext.selectedMarker = null
                v?.context?.startActivity(
                    Intent(v.context, LoveSpotDetailsActivity::class.java)
                )
            }
        }
    }
}