package com.lovemap.lovemapandroid.ui.main.lovespot

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.PhotoUploadUtils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PhotoRecyclerAdapter(
    private val activity: Activity,
    private val loveSpot: LoveSpot,
    private val photoList: List<LoveSpotPhoto>,
    private val launcher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder>() {

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
            viewHolder.spotPhotosUploadButton.setOnClickListener {
                MainScope().launch {
                    if (PhotoUploadUtils.canUploadForSpot(loveSpot.id)) {
                        PhotoUploadUtils.verifyStoragePermissions(activity)
                        PhotoUploadUtils.startPickerIntent(launcher)
                    }
                }
            }
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