package com.lovemap.lovemapandroid.ui.main.lovespot.photos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotType
import com.lovemap.lovemapandroid.databinding.ActivityPhotoViewerBinding
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso

class PhotoViewerActivity : AppCompatActivity() {

    companion object {
        const val URL: String = "url"
        const val LOVE_SPOT_TYPE: String = "loveSpotType"
    }

    private lateinit var binding: ActivityPhotoViewerBinding
    private lateinit var viewerImageView: TouchImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewerImageView = binding.viewerImageView
        val url: String = intent.extras?.getString(URL) ?: "https://noimage.noimage.jpp"
        val loveSpotType: LoveSpotType = LoveSpotType.valueOf(intent.extras?.getString(LOVE_SPOT_TYPE) ?: "PUBLIC_SPACE")

        Picasso.get()
            .load(url)
            .placeholder(LoveSpotUtils.getTypeImageResource(loveSpotType))
            .into(viewerImageView)
    }
}