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
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.photo.LoveSpotPhoto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.events.LoveSpotPhotoDeleted
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.utils.AlertDialogUtils
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class PhotoRecyclerAdapter(
    private val activity: Activity,
    private val loveSpot: LoveSpot,
    private val photoList: List<LoveSpotPhoto>,
    private val launcher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder>() {

    private val userId: Long = AppContext.INSTANCE.userId
    private val toaster = AppContext.INSTANCE.toaster
    private val loveSpotPhotoService = AppContext.INSTANCE.loveSpotPhotoService
    private val loveSpotReviewService = AppContext.INSTANCE.loveSpotReviewService

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
                    PhotoUtils.verifyStoragePermissions(activity)
                    if (PhotoUtils.canUploadForSpot(loveSpot.id)) {
                        PhotoUtils.startPickerIntent(launcher)
                    } else if (PhotoUtils.canUploadForReview(loveSpot.id)) {
                        loveSpotReviewService.findByLoverAndSpotId(loveSpot.id)?.let { review ->
                            if (activity is LoveSpotDetailsActivity) {
                                activity.photoUploadReviewId = review.id
                            }
                            PhotoUtils.startPickerIntent(launcher)
                        }
                    }
                }
            }
        } else {
            viewHolder.imageView.visibility = View.VISIBLE
            viewHolder.spotPhotosUploadButton.visibility = View.GONE
            viewHolder.buttonViewGroup.visibility = View.VISIBLE
            val photo: LoveSpotPhoto = photoList[position]
            if (PhotoUtils.canDeletePhoto(photo)) {
                viewHolder.deleteButton.visibility = View.VISIBLE
            } else {
                viewHolder.deleteButton.visibility = View.GONE
            }

            if (PhotoUtils.isHeif(photo)) {
                PhotoUtils.loadHeif(activity, viewHolder.imageView, loveSpot.type, photo.url)
            } else {
                PhotoUtils.loadSimpleImage(viewHolder.imageView, loveSpot.type, photo.url)
            }


            viewHolder.imageView.setOnClickListener {
                val intent = Intent(activity, PhotoViewerActivity::class.java)
                val bundle = Bundle().apply {
                    putString(PhotoViewerActivity.URL, photo.url)
                    putString(PhotoViewerActivity.FILE_NAME, photo.fileName)
                    putString(PhotoViewerActivity.LOVE_SPOT_TYPE, loveSpot.type.name)
                }
                intent.putExtras(bundle)
                activity.startActivity(intent)
            }

            viewHolder.deleteButton.setOnClickListener {
                AlertDialogUtils.newDialog(
                    activity,
                    R.string.delete_photo_dialog_title,
                    R.string.delete_photo_dialog_message, {
                        MainScope().launch {
                            val deleteResult =
                                loveSpotPhotoService.deletePhoto(loveSpot.id, photo.id)
                            deleteResult.onSuccess { remainingPhotos ->
                                EventBus.getDefault().post(
                                    LoveSpotPhotoDeleted(remainingPhotos)
                                )
                            }

                        }
                    }
                )
            }

            updateLikeButtons(viewHolder, photo)

            viewHolder.likeButton.setOnClickListener {
                MainScope().launch {
                    loveSpotPhotoService.likePhoto(photo.loveSpotId, photo.id)?.let {
                        updateLikeButtons(viewHolder, it)
                    }
                }
            }

            viewHolder.dislikeButton.setOnClickListener {
                MainScope().launch {
                    loveSpotPhotoService.dislikePhoto(photo.loveSpotId, photo.id)?.let {
                        updateLikeButtons(viewHolder, it)
                    }
                }
            }
        }
    }

    private fun updateLikeButtons(
        viewHolder: ViewHolder,
        photo: LoveSpotPhoto
    ) {
        viewHolder.photoItemLikes.text = "${photo.likes}"
        viewHolder.photoItemDislikes.text = "${photo.dislikes}"
        if (photo.likers.contains(userId)) {
            viewHolder.likeButton.icon =
                AppCompatResources.getDrawable(activity, R.drawable.ic_baseline_thumb_up_alt_24)
        } else {
            viewHolder.likeButton.icon =
                AppCompatResources.getDrawable(activity, R.drawable.ic_baseline_thumb_up_off_alt_24)
        }
        if (photo.dislikers.contains(userId)) {
            viewHolder.dislikeButton.icon =
                AppCompatResources.getDrawable(activity, R.drawable.ic_baseline_thumb_down_alt_24)
        } else {
            viewHolder.dislikeButton.icon = AppCompatResources.getDrawable(
                activity,
                R.drawable.ic_baseline_thumb_down_off_alt_24
            )
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