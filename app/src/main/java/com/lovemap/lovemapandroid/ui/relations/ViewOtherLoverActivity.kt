package com.lovemap.lovemapandroid.ui.relations

import android.os.Bundle
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
import com.lovemap.lovemapandroid.api.partnership.PartnershipReaction
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus.IN_PARTNERSHIP
import com.lovemap.lovemapandroid.api.partnership.PartnershipStatus.PARTNERSHIP_REQUESTED
import com.lovemap.lovemapandroid.api.partnership.RespondPartnershipRequest
import com.lovemap.lovemapandroid.api.relation.RelationStatus
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.LINK_PREFIX_API_CALL
import com.lovemap.lovemapandroid.config.LINK_PREFIX_VISIBLE
import com.lovemap.lovemapandroid.data.partnership.Partnership
import com.lovemap.lovemapandroid.databinding.ActivityViewOtherLoverBinding
import com.lovemap.lovemapandroid.ui.main.love.LoveListFragment
import com.lovemap.lovemapandroid.ui.relations.ViewOtherLoverActivity.RelationState.*
import com.lovemap.lovemapandroid.ui.utils.I18nUtils
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ViewOtherLoverActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loverService = AppContext.INSTANCE.loverService
    private val partnershipService = AppContext.INSTANCE.partnershipService

    private lateinit var binding: ActivityViewOtherLoverBinding
    private lateinit var otherLoverSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var profileUserName: TextView
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
    private var partnerships: List<Partnership> = emptyList()

    private var relationState: RelationState = NOTHING

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        otherLoverSwipeRefreshLayout.isRefreshing = true
        setViewState()
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
        profileUserName = binding.profileUserName
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
        (partnerLoveListFragment.view?.findViewById(R.id.loveList) as RecyclerView).isNestedScrollingEnabled = false

        supportFragmentManager
            .beginTransaction()
            .hide(partnerLoveListFragment)
            .commit()
    }

    private fun setViewState() {
        MainScope().launch {
            val otherLover: LoverViewDto? = getOtherLover()
            val partnerships = partnershipService.getPartnerships()
            this@ViewOtherLoverActivity.otherLover = otherLover
            this@ViewOtherLoverActivity.partnerships = partnerships
            setTextsBasedOnRelationState(otherLover, partnerships)
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
    }

    private suspend fun getOtherLover(): LoverViewDto? {
        val otherLover: LoverViewDto? = loverUuid?.let {
            loverService.getByUuid(it)
        } ?: loverService.getOtherById(
            appContext.otherLoverId
        )?.apply {
            appContext.otherLoverId = id
        }
        return otherLover
    }

    private fun setTextsBasedOnRelationState(
        otherLover: LoverViewDto?,
        partnerships: List<Partnership>
    ) {
        otherLover?.let {
            setPointsAndRank(it)
            profileUserName.text = it.userName
            if (userId == it.id) {
                setRelationState(YOURSELF)
            } else {
                if (hasOtherPartner(partnerships, it)) {
                    setRelationState(HAS_OTHER_PARTNER)
                } else {
                    setStateWithOtherLover(partnerships)
                }
            }
        }
    }

    private fun hasOtherPartner(
        partnerships: List<Partnership>,
        otherLover: LoverViewDto
    ): Boolean {
        val anyPartner = partnerships.find { it.partnershipStatus == IN_PARTNERSHIP }
        return anyPartner != null && (anyPartner.initiatorId != otherLover.id && anyPartner.respondentId != otherLover.id)
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

    private fun setStateWithOtherLover(
        partnerships: List<Partnership>
    ) {
        val iAmInitiator = partnerships.find { partnership -> partnership.initiatorId == userId }
        if (iAmInitiator != null) {
            setStateWhenIAmInitiator(iAmInitiator)
        } else {
            val iAmRespondent =
                partnerships.find { partnership -> partnership.respondentId == userId }
            if (iAmRespondent != null) {
                setStateWhenIAmRespondent(iAmRespondent)
            } else {
                setRelationState(NOTHING)
            }
        }
    }

    private fun setStateWhenIAmInitiator(iAmInitiator: Partnership) {
        relationText.text = I18nUtils.partnershipStatus(
            iAmInitiator.partnershipStatus,
            applicationContext
        )
        when (iAmInitiator.partnershipStatus) {
            IN_PARTNERSHIP -> {
                setRelationState(PARTNERSHIP)
            }
            PARTNERSHIP_REQUESTED -> {
                setRelationState(YOU_REQUESTED)
            }
        }
    }

    private fun setStateWhenIAmRespondent(iAmRespondent: Partnership) {
        when (iAmRespondent.partnershipStatus) {
            IN_PARTNERSHIP -> {
                setRelationState(PARTNERSHIP)
            }
            PARTNERSHIP_REQUESTED -> {
                setRelationState(OTHER_REQUESTED)
            }
        }
    }

    private fun setRequestPartnershipButton() {
        binding.requestPartnershipFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    if (partnerships
                            .any { it.partnershipStatus == IN_PARTNERSHIP }
                    ) {
                        appContext.toaster.showToast(R.string.already_have_a_partner)
                    } else {
                        val partnership = partnershipService.requestPartnership(
                            otherLover!!.id,
                            this@ViewOtherLoverActivity
                        )
                        if (partnership != null) {
                            appContext.toaster.showToast(R.string.partnership_requested)
                            setRelationState(YOU_REQUESTED)
                        }
                    }
                }
            }
        }
    }

    private fun setCancelRequestPartnershipButton() {

    }

    private fun setEndPartnershipButton() {

    }

    private fun setAcceptPartnershipButton() {
        acceptPartnershipFab.setOnClickListener {
            MainScope().launch {
                otherLover?.let {
                    val partnerships = partnershipService.respondPartnership(
                        RespondPartnershipRequest(
                            it.id, userId, PartnershipReaction.ACCEPT
                        )
                    )
                    if (partnerships.isNotEmpty()) {
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
            }
            YOURSELF -> {
                relationText.text = getString(R.string.itIsYou)
                disableRequestButton()
                hideRespondView()
                hideCancelRequestButton()
                hideEndButton()
            }
            YOU_REQUESTED -> {
                relationText.text = I18nUtils.partnershipStatus(
                    PARTNERSHIP_REQUESTED,
                    applicationContext
                )
                hideRequestButton()
                hideRespondView()
                showCancelRequestButton()
                hideEndButton()
            }
            OTHER_REQUESTED -> {
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

    enum class RelationState {
        YOURSELF, NOTHING, YOU_REQUESTED, OTHER_REQUESTED, PARTNERSHIP, HAS_OTHER_PARTNER
    }
}
