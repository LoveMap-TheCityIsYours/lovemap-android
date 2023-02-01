package com.lovemap.lovemapandroid.ui.lover

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityLoverListBinding
import com.lovemap.lovemapandroid.service.lover.relation.RelationService
import com.lovemap.lovemapandroid.ui.lover.LoverRecyclerViewAdapter.Type.FOLLOWERS
import com.lovemap.lovemapandroid.ui.lover.LoverRecyclerViewAdapter.Type.FOLLOWINGS
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoverListActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService
    private val metadataStore = appContext.metadataStore

    private lateinit var binding: ActivityLoverListBinding
    private lateinit var loverActivityTitle: TextView
    private lateinit var followersDisplayName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoverListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loverActivityTitle = binding.loverActivityTitle
        followersDisplayName = binding.followersDisplayName
        loverActivityTitle.text = when (RelationService.LOVER_LIST_TYPE) {
            FOLLOWINGS -> getString(R.string.your_followings)
            FOLLOWERS -> getString(R.string.your_followers)
        }
        MainScope().launch {
            if (RelationService.LOVER_ID == appContext.userId) {
                followersDisplayName.text = metadataStore.getLover().displayName
            } else {
                loverService.getOtherByIdWithoutRelation(RelationService.LOVER_ID)?.let {
                    followersDisplayName.text = it.displayName
                }
            }
        }
    }
}