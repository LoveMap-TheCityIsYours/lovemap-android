package com.lovemap.lovemapandroid.ui.main.love

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityLoveListBinding
import com.lovemap.lovemapandroid.databinding.ActivityReviewListBinding
import com.lovemap.lovemapandroid.ui.main.lovespot.review.LoveSpotReviewListFragment

class LoveListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoveListBinding
    private lateinit var loveListFragment: LoveListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoveListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loveListFragment =
            supportFragmentManager.findFragmentById(R.id.loveListFragment) as LoveListFragment

        binding.loveSpotTitle.text = AppContext.INSTANCE.selectedLoveSpot?.name
    }
}