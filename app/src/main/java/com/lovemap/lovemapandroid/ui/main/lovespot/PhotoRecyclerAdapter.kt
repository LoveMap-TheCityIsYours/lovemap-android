package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.squareup.picasso.Picasso

class PhotoRecyclerAdapter(private val context: Context) :
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
        if (position == itemCount - 1) {
            viewHolder.imageView.visibility = View.GONE
            viewHolder.spotDetailsUploadButton.visibility = View.VISIBLE
        } else {
            viewHolder.imageView.visibility = View.VISIBLE
            viewHolder.spotDetailsUploadButton.visibility = View.GONE
            Picasso.get()
                .load("https://natureconservancy-h.assetsadobe.com/is/image/content/dam/tnc/nature/en/photos/Independence-Lake-Clean-Drinking-Water_4000x2200.jpg?crop=1175,0,1650,2200&wid=600&hei=800&scl=2.75")
                .placeholder(context.resources.getDrawable(R.drawable.ic_sex_booth_1)) // placeholder
                .into(viewHolder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return 12
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById<View>(R.id.loveSpotPhotoItem) as ImageView
        val spotDetailsUploadButton: ExtendedFloatingActionButton = view.findViewById<View>(R.id.spotDetailsUploadButton) as ExtendedFloatingActionButton
    }
}