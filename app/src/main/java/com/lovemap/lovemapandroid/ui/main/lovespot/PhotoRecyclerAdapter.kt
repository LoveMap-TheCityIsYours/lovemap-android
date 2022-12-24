package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.squareup.picasso.Picasso

class PhotoRecyclerAdapter(
    private val context: Context,
    private val loveSpot: LoveSpot,
    private val photoList: List<LoveSpotPhoto>
) :
    RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.love_spot_photo_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position == photoList.size) {
            viewHolder.imageView.visibility = View.GONE
            viewHolder.spotPhotosUploadButton.visibility = View.VISIBLE
        } else {
            viewHolder.imageView.visibility = View.VISIBLE
            viewHolder.spotPhotosUploadButton.visibility = View.GONE
            Picasso.get()
                .load(photoList[position].url)
                .placeholder(LoveSpotUtils.getTypeImageResource(loveSpot.type))
                .into(viewHolder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return photoList.size + 1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById<View>(R.id.loveSpotPhotoItem) as ImageView
        val spotPhotosUploadButton: ExtendedFloatingActionButton =
            view.findViewById<View>(R.id.spotPhotosUploadButton) as ExtendedFloatingActionButton
    }
}