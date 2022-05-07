package com.lovemap.lovemapandroid.ui.main.lovespot

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.databinding.FragmentLovespotItemBinding
import com.lovemap.lovemapandroid.ui.data.LoveSpotContent.LoveSpotItem

/**
 * [RecyclerView.Adapter] that can display a [LoveSpotItem].
 * TODO: Replace the implementation with code for your data type.
 */
class LoveSpotRecyclerViewAdapter(
    private val values: List<LoveSpotItem>
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
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLovespotItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}