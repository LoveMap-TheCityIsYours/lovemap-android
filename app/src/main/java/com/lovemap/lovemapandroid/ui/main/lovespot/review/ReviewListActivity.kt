package com.lovemap.lovemapandroid.ui.main.lovespot.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityReviewListBinding
import kotlinx.coroutines.MainScope

class ReviewListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewListBinding
    private lateinit var reviewListFragment: LoveSpotReviewListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reviewListFragment =
            supportFragmentManager.findFragmentById(R.id.reviewListFragment) as LoveSpotReviewListFragment

        binding.loveSpotTitle.text = AppContext.INSTANCE.selectedLoveSpot?.name
    }
}