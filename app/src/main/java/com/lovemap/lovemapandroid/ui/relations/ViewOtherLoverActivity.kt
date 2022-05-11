package com.lovemap.lovemapandroid.ui.relations

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus
import com.lovemap.lovemapandroid.api.partnership.RequestPartnershipRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.LINK_PREFIX
import com.lovemap.lovemapandroid.databinding.ActivityViewOtherLoverBinding
import com.lovemap.lovemapandroid.ui.utils.I18nUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ViewOtherLoverActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loverService = AppContext.INSTANCE.loverService
    private val partnershipService = AppContext.INSTANCE.partnershipService

    private lateinit var binding: ActivityViewOtherLoverBinding
    private var lover: LoverViewDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOtherLoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loverLink = intent.data.toString()
        val loverUuid = loverLink.substringAfter(LINK_PREFIX)

        MainScope().launch {
            lover = appContext.loverService.getByUuid(loverUuid)
            lover?.let {
                binding.profileUserName.text = lover!!.userName
                binding.relationText.text =
                    I18nUtils.relationStatus(lover!!.relation, applicationContext)
                setRank(lover!!)
            }
        }

        binding.requestPartnershipFab.setOnClickListener {
            MainScope().launch {
                lover?.let {
                    if (partnershipService.getPartnerships()
                            .any { it.partnershipStatus == PartnershipStatus.IN_PARTNERSHIP }
                    ) {
                        appContext.toaster.showToast(R.string.already_have_a_partner)
                    } else {
                        partnershipService.requestPartnership(lover!!.id)
                    }
                }
            }
        }
    }

    private suspend fun setRank(lover: LoverViewDto) {
        val ranks = loverService.getRanks()
        ranks?.let {
            val rankList = ranks.rankList
            binding.profileUserLevelText.text = rankList[lover.rank - 1].nameEN
        }
    }
}