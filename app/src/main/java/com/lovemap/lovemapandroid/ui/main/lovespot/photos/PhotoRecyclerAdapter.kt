package com.lovemap.lovemapandroid.ui.main.lovespot.photos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.utils.AlertDialogUtils
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

    private val toaster = AppContext.INSTANCE.toaster
    private val loveSpotPhotoService = AppContext.INSTANCE.loveSpotPhotoService

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
            viewHolder.buttonViewGroup.visibility = View.GONE

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
            viewHolder.buttonViewGroup.visibility = View.VISIBLE

            val photo = photoList[position]
            Picasso.get()
                .load(photo.url)
                .placeholder(LoveSpotUtils.getTypeImageResource(loveSpot.type))
                .into(viewHolder.imageView)

            viewHolder.imageView.setOnClickListener {
                val intent = Intent(activity, PhotoViewerActivity::class.java)
                val bundle = Bundle().apply {
                    putString(PhotoViewerActivity.URL, photo.url)
                    putString(PhotoViewerActivity.LOVE_SPOT_TYPE, loveSpot.type.name)
                }
                intent.putExtras(bundle)
                activity.startActivity(intent)
            }

            viewHolder.deleteButton.setOnClickListener {
                AlertDialogUtils.newDialog(
                    activity,
                    R.string.delete_photo_dialog_title,
                    R.string.delete_photo_dialog_message,
                    {
                        MainScope().launch {
                            loveSpotPhotoService.deletePhoto(loveSpot.id, photo.id)
                        }
                    }
                ).show()
            }

            viewHolder.likeButton.setOnClickListener {
                toaster.showToast(R.string.not_yet_implemented)
            }

            viewHolder.dislikeButton.setOnClickListener {
                toaster.showToast(R.string.not_yet_implemented)
            }
        }
    }

    override fun getItemCount(): Int {
        return photoList.size + 1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById<View>(R.id.loveSpotPhotoItem) as ImageView
        val spotPhotosUploadButton: ExtendedFloatingActionButton =
            view.findViewById<View>(R.id.spotPhotosUploadButton) as ExtendedFloatingActionButton

        val buttonViewGroup: LinearLayout =
            view.findViewById<View>(R.id.buttonViewGroup) as LinearLayout

        val likeButton: ExtendedFloatingActionButton =
            view.findViewById<View>(R.id.likeButton) as ExtendedFloatingActionButton
        val photoItemLikes: TextView = view.findViewById<View>(R.id.photoItemLikes) as TextView

        val dislikeButton: ExtendedFloatingActionButton =
            view.findViewById<View>(R.id.dislikeButton) as ExtendedFloatingActionButton
        val photoItemDislikes: TextView =
            view.findViewById<View>(R.id.photoItemDislikes) as TextView

        val deleteButton: ExtendedFloatingActionButton =
            view.findViewById<View>(R.id.deleteButton) as ExtendedFloatingActionButton
    }
}