package com.lovemap.lovemapandroid.ui.main.love

import android.content.Intent
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentLoveItemBinding
import com.lovemap.lovemapandroid.ui.data.LoveHolder
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity

class LoveRecyclerViewAdapter(
    val values: MutableList<LoveHolder>,
    var isClickable: Boolean
) : RecyclerView.Adapter<LoveRecyclerViewAdapter.ViewHolder>() {

    var position: Int = 0

    private val appContext = AppContext.INSTANCE

    fun updateData(data: List<LoveHolder>) {
        values.clear()
        values.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLoveItemBinding.inflate(
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
        val love = values[position]
        holder.loveItemName.text = love.name
        holder.loveItemPartner.text = love.partner
        if (love.note.isBlank()) {
            holder.loveItemNoteView.visibility = View.GONE
        } else {
            holder.loveItemNoteView.visibility = View.VISIBLE
            holder.loveItemNote.text = love.note
        }
        holder.loveItemHappenedAt.text = love.happenedAt
        holder.loveItem = love
        holder.itemView.setOnLongClickListener {
            this.position = holder.absoluteAdapterPosition
            false
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLoveItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        View.OnCreateContextMenuListener {
        val loveItemName: TextView = binding.loveItemName
        val loveItemPartner: TextView = binding.loveItemPartner
        val loveItemNoteView: LinearLayout = binding.loveItemNoteView
        val loveItemNote: TextView = binding.loveItemNote
        val loveItemHappenedAt: TextView = binding.loveItemHappenedAt
        lateinit var loveItem: LoveHolder

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnCreateContextMenuListener(this)
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

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.setHeaderTitle(loveItem.name)
            menu.add(Menu.NONE, EDIT_LOVE_MENU_ID, Menu.NONE, R.string.edit)
            menu.add(Menu.NONE, DELETE_LOVE_MENU_ID, Menu.NONE, R.string.delete)
        }
    }

    companion object {
        const val EDIT_LOVE_MENU_ID = 0
        const val DELETE_LOVE_MENU_ID = 1
    }
}