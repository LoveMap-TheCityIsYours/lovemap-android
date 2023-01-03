package com.lovemap.lovemapandroid.ui.main.lovespot.photos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.databinding.ActivityPhotoViewerBinding
import com.lovemap.lovemapandroid.ui.utils.PhotoUtils
import com.ortiz.touchview.TouchImageView

class PhotoViewerActivity : AppCompatActivity() {

    companion object {
        const val URL: String = "url"
        const val LOVE_SPOT_TYPE: String = "loveSpotType"
        const val FILE_NAME: String = "fileName"
    }

    private lateinit var binding: ActivityPhotoViewerBinding
    private lateinit var viewerImageView: TouchImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewerImageView = binding.viewerImageView
        val url: String = intent.extras?.getString(URL) ?: "https://noimage.noimage.jpg"
        val fileName: String = intent.extras?.getString(FILE_NAME) ?: "noimage.jpg"
        val loveSpotType: LoveSpotType =
            LoveSpotType.valueOf(intent.extras?.getString(LOVE_SPOT_TYPE) ?: "PUBLIC_SPACE")

        if (PhotoUtils.isHeif(fileName)) {
            PhotoUtils.loadHeif(this, viewerImageView, loveSpotType, url)
        } else {
            PhotoUtils.loadSimpleImage(viewerImageView, loveSpotType, url)
        }
    }
}