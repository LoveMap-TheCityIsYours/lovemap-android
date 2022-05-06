package com.smackmap.smackmapandroid.ui.main.smackspot

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smackmap.smackmapandroid.databinding.FragmentSmackspotItemBinding
import com.smackmap.smackmapandroid.ui.data.SmackSpotContent.SmackSpotItem

/**
 * [RecyclerView.Adapter] that can display a [SmackSpotItem].
 * TODO: Replace the implementation with code for your data type.
 */
class SmackSpotRecyclerViewAdapter(
    private val values: List<SmackSpotItem>
) : RecyclerView.Adapter<SmackSpotRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSmackspotItemBinding.inflate(
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

    inner class ViewHolder(binding: FragmentSmackspotItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}