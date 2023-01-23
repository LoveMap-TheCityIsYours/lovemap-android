package com.lovemap.lovemapandroid.ui.relations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.api.partnership.CancelPartnershipRequest
import com.lovemap.lovemapandroid.api.partnership.PartnershipReaction
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus.PARTNERSHIP_REQUESTED
import com.lovemap.lovemapandroid.api.partnership.RespondPartnershipRequest
import com.lovemap.lovemapandroid.api.lover.relation.RelationStatus
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.databinding.ActivityViewOtherLoverBinding
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.service.lover.relation.RelationState
import com.lovemap.lovemapandroid.service.lover.relation.RelationState.*
import com.lovemap.lovemapandroid.ui.main.love.lovehistory.LoveListFragment
import com.lovemap.lovemapandroid.ui.main.newsfeed.NewsFeedFragment
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
    private val relationService = AppContext.INSTANCE.relationService
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
    private lateinit var profilePublicImage: ImageView
    private lateinit var profilePublicToggleText: TextView

    private lateinit var followFab: ExtendedFloatingActionButton
    private lateinit var unfollowFab: ExtendedFloatingActionButton
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
            getOtherLover()?.let {
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
        setUnfollowButton()
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
        profilePublicImage = binding.profilePublicImage
        profilePublicToggleText = binding.profilePublicToggleText

        followFab = binding.followFab
        unfollowFab = binding.unfollowFab
        requestPartnershipFab = binding.requestPartnershipFab
        acceptPartnershipFab = binding.acceptPartnershipFab
        denyPartnershipFab = binding.denyPartnershipFab
        endPartnershipFab = binding.endPartnershipFab
        cancelRequestPartnershipFab = binding.cancelRequestPartnershipFab

        respondPartnershipViews = binding.respondPartnershipViews

        partnerLoveListFragment =
            supportFragmentManager.findFragmentById(R.id.partnerLoveListFragment) as LoveListFragment

        hideLoveListFragment()
    }

    private fun hideLoveListFragment() {
        runCatching {
            supportFragmentManager
                .beginTransaction()
                .hide(partnerLoveListFragment)
                .commit()
        }.onFailure { e ->
            Log.e(tag, "supportFragmentManager shitted itself", e)
        }
    }

    private fun setViewState() {
        MainScope().launch {
            getOtherLover()?.let { otherLover ->
                setViews(otherLover)
            }
        }
    }

    private suspend fun setViews(otherLover: LoverViewDto) {
        try {
            setPublicProfileViews(otherLover.publicProfile)
            setPointsAndRank(otherLover)
            profileDisplayName.animate().alpha(0f).setDuration(250).withEndAction {
                profileDisplayName.text = otherLover.displayName
                profileDisplayName.animate().alpha(1f).duration = 250
            }
            val partnership = partnershipService.getPartnership()
            this@ViewOtherLoverActivity.partnership = partnership
            setRelationWithLover(otherLover, partnership)
            showLovesWithPartner()
            showLoverActivitiesFragment(otherLover.id)
        } catch (e: Exception) {
            Log.e(tag, "setViews shitted itself", e)
        }
    }

    private fun showLoverActivitiesFragment(loverId: Long) {
        runCatching {
            Log.i(tag, "showLoverActivitiesFragment started")
            val newsFeedFragment = NewsFeedFragment
                .newInstance(NewsFeedFragment.NewsFeedType.LOVER_ACTIVITIES, loverId)

            supportFragmentManager
                .beginTransaction()
                .add(R.id.viewOtherLoverNewsFeedContainer, newsFeedFragment)
                .disallowAddToBackStack()
                .commit()

            Log.i(tag, "showLoverActivitiesFragment finished")
        }.onFailure { e ->
            Log.e(tag, "showLoverActivitiesFragment shitted itself", e)
        }
    }

    private fun setPublicProfileViews(publicProfile: Boolean) {
        try {
            if (publicProfile) {
                Glide.with(this)
                    .load(R.drawable.ic_baseline_public_24)
                    .into(profilePublicImage)
                profilePublicToggleText.text = getString(R.string.public_profile)
            } else {
                Glide.with(this)
                    .load(R.drawable.ic_baseline_public_off_24)
                    .into(profilePublicImage)
                profilePublicToggleText.text = getString(R.string.privateProfile)
            }
        } catch (e: Exception) {
            Log.i(tag, "Glide fucked up", e)
        }
    }

    private fun setPointsAndRank(otherLover: LoverViewDto) {
        otherLoverPoints.animate().alpha(0f).setDuration(250).withEndAction {
            otherLoverPoints.text = otherLover.points.toString()
            otherLoverPoints.animate().alpha(1f).duration = 250
        }
        ProfileUtils.setRanks(
            points = otherLover.points,
            currentRank = otherLoverRank,
            animateText = false,
            pointsToNextLevel = otherLoverPointsToNextLevel,
            progressBar = otherLoverProgressBar
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
        try {
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
        } catch (e: Exception) {
            Log.e(tag, "showLovesWithPartner shitted itself", e)
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
            MainScope().launch {
                otherLover?.let {
                    relationService.followLover(it.id)?.let {
                        setRelationState(YOU_ARE_FOLLOWING_THEM)
                    }
                }
            }
        }
    }

    private fun setUnfollowButton() {
        unfollowFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    relationService.unfollowLover(it.id)?.let {
                        setRelationState(NOTHING)
                    }
                }
            }
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
        try {
            relationState = state
            when (state) {
                NOTHING -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text =
                            I18nUtils.relationStatus(RelationStatus.NOTHING, applicationContext)
                        relationText.animate().alpha(1f).duration = 250
                    }
                    enableRequestButton()
                    hideRespondView()
                    hideCancelRequestButton()
                    hideEndButton()
                    hideLoveListFragment()
                    enableFollowButton()
                    hideUnfollowButton()
                }
                YOURSELF -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text = getString(R.string.itIsYou)
                        relationText.animate().alpha(1f).duration = 250
                    }
                    disableRequestButton()
                    hideRespondView()
                    hideCancelRequestButton()
                    hideEndButton()
                    hideFollowButton()
                    hideUnfollowButton()
                }
                YOU_REQUESTED_PARTNERSHIP -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text = I18nUtils.partnershipStatus(
                            PARTNERSHIP_REQUESTED,
                            applicationContext
                        )
                        relationText.animate().alpha(1f).duration = 250
                    }
                    hideRequestButton()
                    hideRespondView()
                    showCancelRequestButton()
                    hideEndButton()
                    hideFollowButton()
                    hideUnfollowButton()
                }
                THEY_REQUESTED_PARTNERSHIP -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text = I18nUtils.partnershipStatus(
                            PARTNERSHIP_REQUESTED,
                            applicationContext
                        )
                        relationText.animate().alpha(1f).duration = 250
                    }
                    hideRequestButton()
                    showRespondView()
                    hideCancelRequestButton()
                    hideEndButton()
                    hideFollowButton()
                    hideUnfollowButton()
                }
                PARTNERSHIP -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text =
                            I18nUtils.relationStatus(RelationStatus.PARTNER, applicationContext)
                        relationText.animate().alpha(1f).duration = 250
                    }
                    hideRequestButton()
                    hideRespondView()
                    hideCancelRequestButton()
                    showEndButton()
                    hideFollowButton()
                    hideUnfollowButton()
                }
                YOU_BLOCKED_THEM -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text = getString(R.string.you_blocked_them)
                        relationText.animate().alpha(1f).duration = 250
                    }
                    disableRequestButton()
                    hideRespondView()
                    hideCancelRequestButton()
                    hideEndButton()
                    hideFollowButton()
                    hideUnfollowButton()
                }
                YOU_ARE_FOLLOWING_THEM -> {
                    relationText.animate().alpha(0f).setDuration(250).withEndAction {
                        relationText.text = getString(R.string.you_follow_this_lover)
                        relationText.animate().alpha(1f).duration = 250
                    }
                    disableRequestButton()
                    hideRespondView()
                    hideCancelRequestButton()
                    hideEndButton()
                    hideFollowButton()
                    showUnfollowButton()
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "setRelationState shitted itself", e)
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

    private fun hideFollowButton() {
        followFab.visibility = View.GONE
    }

    private fun showUnfollowButton() {
        unfollowFab.visibility = View.VISIBLE
    }

    private fun hideUnfollowButton() {
        unfollowFab.visibility = View.GONE
    }

    private fun enableFollowButton() {
        otherLover?.let {
            if (it.publicProfile) {
                followFab.visibility = View.VISIBLE
                followFab.isEnabled = true
            }
        }
    }
}
