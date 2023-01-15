package com.lovemap.lovemapandroid.ui.relations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.partnership.CancelPartnershipRequest
import com.lovemap.lovemapandroid.api.partnership.PartnershipReaction
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus.PARTNERSHIP_REQUESTED
import com.lovemap.lovemapandroid.api.partnership.RespondPartnershipRequest
import com.lovemap.lovemapandroid.api.relation.RelationStatus
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.databinding.ActivityViewOtherLoverBinding
import com.lovemap.lovemapandroid.service.LoverService
import com.lovemap.lovemapandroid.service.relations.RelationState
import com.lovemap.lovemapandroid.service.relations.RelationState.*
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveListFragment
import com.lovemap.lovemapandroid.ui.utils.AlertDialogUtils
import com.lovemap.lovemapandroid.ui.utils.I18nUtils
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import com.lovemap.lovemapandroid.utils.LINK_PREFIX_API_CALL
import com.lovemap.lovemapandroid.utils.LINK_PREFIX_VISIBLE
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ViewOtherLoverActivity : AppCompatActivity() {

    private val tag = "ViewOtherLoverActivity"
    private val appContext = AppContext.INSTANCE
    private val loverService = AppContext.INSTANCE.loverService
    private val partnershipService = AppContext.INSTANCE.partnershipService

    private lateinit var binding: ActivityViewOtherLoverBinding
    private lateinit var otherLoverSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var profileDisplayName: TextView
    private lateinit var otherLoverPoints: TextView
    private lateinit var otherLoverPointsToNextLevel: TextView
    private lateinit var otherLoverRank: TextView
    private lateinit var otherLoverProgressBar: ProgressBar
    private lateinit var relationText: TextView
    private lateinit var partnerViewLoveMakingsText: TextView

    private lateinit var followFab: ExtendedFloatingActionButton
    private lateinit var requestPartnershipFab: ExtendedFloatingActionButton
    private lateinit var acceptPartnershipFab: ExtendedFloatingActionButton
    private lateinit var denyPartnershipFab: ExtendedFloatingActionButton
    private lateinit var endPartnershipFab: ExtendedFloatingActionButton
    private lateinit var cancelRequestPartnershipFab: ExtendedFloatingActionButton

    private lateinit var respondPartnershipViews: LinearLayout

    private lateinit var partnerLoveListFragment: LoveListFragment

    private var userId: Long = 0
    private var loverUuid: String? = null
    private var otherLover: LoverViewDto? = null
    private var partnership: Partnership? = null

    private var relationState: RelationState = NOTHING

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        otherLoverSwipeRefreshLayout.isRefreshing = true
        MainScope().launch {
            otherLover?.let {
                otherLover = loverService.getOtherById(it.id)
                setViews(it)
            }
            otherLoverSwipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        if (intent.data != null) {
            val loverLink = intent.data.toString()
            loverUuid = loverLink.substringAfter(LINK_PREFIX_VISIBLE, "")
            if (loverUuid == "") {
                loverUuid = loverLink.substringAfter(LINK_PREFIX_API_CALL)
            }
        }
        userId = appContext.userId
        setViewState()
        setFollowButton()
        setRequestPartnershipButton()
        setAcceptPartnershipButton()
        setDenyPartnershipButton()
        setEndPartnershipButton()
        setCancelRequestPartnershipButton()
    }

    private fun initViews() {
        binding = ActivityViewOtherLoverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        otherLoverSwipeRefreshLayout = binding.otherLoverSwipeRefreshLayout
        otherLoverSwipeRefreshLayout.setOnRefreshListener(refreshListener)
        profileDisplayName = binding.profileDisplayName
        otherLoverPoints = binding.otherLoverPoints
        otherLoverPointsToNextLevel = binding.otherLoverPointsToNextLevel
        otherLoverRank = binding.otherLoverRank
        otherLoverProgressBar = binding.otherLoverProgressBar
        relationText = binding.relationText
        partnerViewLoveMakingsText = binding.partnerViewLoveMakingsText

        followFab = binding.followFab
        requestPartnershipFab = binding.requestPartnershipFab
        acceptPartnershipFab = binding.acceptPartnershipFab
        denyPartnershipFab = binding.denyPartnershipFab
        endPartnershipFab = binding.endPartnershipFab
        cancelRequestPartnershipFab = binding.cancelRequestPartnershipFab

        respondPartnershipViews = binding.respondPartnershipViews

        partnerLoveListFragment =
            supportFragmentManager.findFragmentById(R.id.partnerLoveListFragment) as LoveListFragment
        (partnerLoveListFragment.view?.findViewById(R.id.loveList) as RecyclerView).isNestedScrollingEnabled =
            false

        hideLoveListFragment()
    }

    private fun hideLoveListFragment() {
        supportFragmentManager
            .beginTransaction()
            .hide(partnerLoveListFragment)
            .commit()
    }

    private fun setViewState() {
        MainScope().launch {
            getOtherLover()?.let { otherLover ->
                setViews(otherLover)
            }
        }
    }

    private suspend fun setViews(otherLover: LoverViewDto) {
        setPointsAndRank(otherLover)
        profileDisplayName.text = otherLover.displayName
        val partnership = partnershipService.getPartnership()
        this@ViewOtherLoverActivity.partnership = partnership
        setRelationWithLover(otherLover, partnership)
        showLovesWithPartner()
    }

    private fun setPointsAndRank(otherLover: LoverViewDto) {
        otherLoverPoints.text = otherLover.points.toString()
        ProfileUtils.setRanks(
            otherLover.points,
            otherLoverRank,
            otherLoverPointsToNextLevel,
            otherLoverProgressBar
        )
    }

    private fun setRelationWithLover(otherLover: LoverViewDto, partnership: Partnership?) {
        when (otherLover.relation) {
            RelationStatus.PARTNER -> setRelationState(PARTNERSHIP)
            RelationStatus.FOLLOWING -> setRelationState(YOU_ARE_FOLLOWING_THEM)
            RelationStatus.BLOCKED -> setRelationState(YOU_BLOCKED_THEM)
            RelationStatus.NOTHING -> {
                val partnershipStatus: RelationState? =
                    partnershipService.getPartnershipStatus(otherLover, partnership)
                Log.i(tag, "PartnershipStatus is: $partnershipStatus")
                when (partnershipStatus) {
                    YOU_REQUESTED_PARTNERSHIP -> setRelationState(YOU_REQUESTED_PARTNERSHIP)
                    THEY_REQUESTED_PARTNERSHIP -> setRelationState(THEY_REQUESTED_PARTNERSHIP)
                    YOURSELF -> setRelationState(YOURSELF)
                    else -> setRelationState(NOTHING)
                }
            }
        }
    }

    private fun showLovesWithPartner() {
        if (!this@ViewOtherLoverActivity.isFinishing) {
            if (relationState == PARTNERSHIP) {
                partnerViewLoveMakingsText.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    .show(partnerLoveListFragment)
                    .commit()
            }
            otherLoverSwipeRefreshLayout.isRefreshing = false
        }
    }

    private suspend fun getOtherLover(): LoverViewDto? {
        val loverViewDto = loverUuid?.let { uuid ->
            val lover = loverService.getByUuid(uuid)
            lover?.let {
                LoverService.otherLover = it
                LoverService.otherLoverId = it.id
            }
            lover
        } ?: loverService.getSelectedLover()
        this.otherLover = loverViewDto
        return loverViewDto
    }

    private fun setFollowButton() {
        followFab.setOnClickListener {
            AppContext.INSTANCE.toaster.showToast(R.string.not_yet_implemented)
        }
    }

    private fun setRequestPartnershipButton() {
        requestPartnershipFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    if (partnership != null) {
                        appContext.toaster.showToast(R.string.already_have_a_partner)
                    } else {
                        val partnership = partnershipService.requestPartnership(
                            otherLover!!.id,
                            this@ViewOtherLoverActivity
                        )
                        if (partnership != null) {
                            appContext.toaster.showToast(R.string.partnership_requested)
                            setRelationState(YOU_REQUESTED_PARTNERSHIP)
                        }
                    }
                }
            }
        }
    }

    private fun setCancelRequestPartnershipButton() {
        cancelRequestPartnershipFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    partnership = partnershipService.cancelPartnershipRequest(
                        CancelPartnershipRequest(
                            userId, it.id
                        )
                    )
                    if (partnership == null) {
                        setRelationState(NOTHING)
                    }
                }
            }
        }
    }

    private fun setEndPartnershipButton() {
        endPartnershipFab.setOnClickListener {
            otherLover?.let {
                AlertDialogUtils.newDialog(
                    activity = this@ViewOtherLoverActivity,
                    R.string.end_partnership_title,
                    R.string.end_partnership_message,
                    {
                        MainScope().launch {
                            partnership = partnershipService.endPartnership(it.id)
                            if (partnership == null) {
                                setRelationState(NOTHING)
                            }
                        }
                    }
                )
            }

        }
    }

    private fun setAcceptPartnershipButton() {
        acceptPartnershipFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    partnership = partnershipService.respondPartnership(
                        RespondPartnershipRequest(
                            it.id, userId, PartnershipReaction.ACCEPT
                        )
                    )
                    if (partnership != null) {
                        setRelationState(PARTNERSHIP)
                    }
                }
            }
        }
    }

    private fun setDenyPartnershipButton() {
        denyPartnershipFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    partnershipService.respondPartnership(
                        RespondPartnershipRequest(
                            it.id, userId, PartnershipReaction.DENY
                        )
                    )
                    setRelationState(NOTHING)
                }
            }
        }
    }

    private fun setRelationState(state: RelationState) {
        relationState = state
        when (state) {
            NOTHING -> {
                relationText.text =
                    I18nUtils.relationStatus(RelationStatus.NOTHING, applicationContext)
                enableRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                hideEndButton()
                hideLoveListFragment()
            }
            YOURSELF -> {
                relationText.text = getString(R.string.itIsYou)
                disableRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                hideEndButton()
            }
            YOU_REQUESTED_PARTNERSHIP -> {
                relationText.text = I18nUtils.partnershipStatus(
                    PARTNERSHIP_REQUESTED,
                    applicationContext
                )
                hideRequestButton()
                hideRespondView()
                showCancelRequestButton()
                hideEndButton()
            }
            THEY_REQUESTED_PARTNERSHIP -> {
                relationText.text = I18nUtils.partnershipStatus(
                    PARTNERSHIP_REQUESTED,
                    applicationContext
                )
                hideRequestButton()
                showRespondView()
                hideCancelRequestButton()
                hideEndButton()
            }
            PARTNERSHIP -> {
                relationText.text =
                    I18nUtils.relationStatus(RelationStatus.PARTNER, applicationContext)
                hideRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                showEndButton()
            }
            HAS_OTHER_PARTNER -> {
                relationText.text =
                    I18nUtils.relationStatus(RelationStatus.NOTHING, applicationContext)
                disableRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                hideEndButton()
            }
            YOU_BLOCKED_THEM -> {
                relationText.text = getString(R.string.you_blocked_them)
                disableRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                hideEndButton()
            }
            YOU_ARE_FOLLOWING_THEM -> {
                relationText.text = getString(R.string.you_follow_this_lover)
                disableRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                hideEndButton()
            }
        }
    }

    private fun showRequestButton() {
        requestPartnershipFab.visibility = View.VISIBLE
    }

    private fun hideRequestButton() {
        requestPartnershipFab.visibility = View.GONE
    }

    private fun enableRequestButton() {
        showRequestButton()
        requestPartnershipFab.isEnabled = true
    }

    private fun disableRequestButton() {
        showRequestButton()
        requestPartnershipFab.isEnabled = false
    }

    private fun showRespondView() {
        respondPartnershipViews.visibility = View.VISIBLE
    }

    private fun hideRespondView() {
        respondPartnershipViews.visibility = View.GONE
    }


    private fun showCancelRequestButton() {
        cancelRequestPartnershipFab.visibility = View.VISIBLE
    }

    private fun hideCancelRequestButton() {
        cancelRequestPartnershipFab.visibility = View.GONE
    }

    private fun showEndButton() {
        endPartnershipFab.visibility = View.VISIBLE
    }

    private fun hideEndButton() {
        endPartnershipFab.visibility = View.GONE
    }
}
