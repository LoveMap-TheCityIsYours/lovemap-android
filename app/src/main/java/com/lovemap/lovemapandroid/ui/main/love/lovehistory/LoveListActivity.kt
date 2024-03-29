package com.lovemap.lovemapandroid.ui.main.love.lovehistory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityLoveListBinding
import com.lovemap.lovemapandroid.utils.IS_CLICKABLE
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoveListBinding
    private lateinit var loveListFragment: LoveListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoveListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loveListFragment =
            supportFragmentManager.findFragmentById(R.id.loveListFragment) as LoveListFragment

        loveListFragment.isClickableOverride = intent.getBooleanExtra(IS_CLICKABLE, true)
        MainScope().launch {
            AppContext.INSTANCE.findSelectedSpotLocally()?.let {
                binding.loveSpotTitle.text = it.name
            }
        }
    }
}