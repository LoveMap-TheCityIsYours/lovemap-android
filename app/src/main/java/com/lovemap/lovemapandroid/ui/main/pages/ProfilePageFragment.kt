package com.lovemap.lovemapandroid.ui.main.pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.login.LoginActivity
import com.lovemap.lovemapandroid.ui.relations.ViewOtherLoverActivity
import com.lovemap.lovemapandroid.ui.utils.I18nUtils
import com.lovemap.lovemapandroid.ui.utils.InfoPopupShower
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfilePageFragment : Fragment() {

    private lateinit var profileSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var profilePartnerView: LinearLayout
    private lateinit var profilePartnershipImage: ImageView
    private lateinit var userNameView: TextView
    private lateinit var profilePartnershipWithText: TextView
    private lateinit var profilePartnerName: TextView
    private lateinit var profilePartnerRelation: TextView
    private lateinit var link: TextView
    private lateinit var linkToggle: SwitchCompat
    private lateinit var linkToggleText: TextView
    private lateinit var shareLinkButton: ImageButton
    private lateinit var numberOfLoves: TextView
    private lateinit var loveSpotsAdded: TextView
    private lateinit var reportsSubmitted: TextView
    private lateinit var reportsReceived: TextView
    private lateinit var meaningfulReviews: TextView
    private lateinit var points: TextView
    private lateinit var pointsToNextLevel: TextView
    private lateinit var currentRank: TextView
    private lateinit var profileShareDescription: TextView
    private lateinit var profileProgressBar: ProgressBar
    private lateinit var linkSharingInfoButton: ImageButton

    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService
    private val partnershipService = appContext.partnershipService

    private var partner: LoverViewDto? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = initViews(inflater, container)
        setLogoutListener(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        fillViewWithData()
    }

    private fun initViews(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)
        setRefreshListener(view)

        profilePartnerView = view.findViewById(R.id.profilePartnerView)
        profilePartnerView.setOnClickListener { onPartnerClicked() }
        profilePartnershipImage = view.findViewById(R.id.profilePartnershipImage)
        profilePartnershipImage.setOnClickListener { onPartnerClicked() }
        profilePartnershipWithText = view.findViewById(R.id.profilePartnershipWithText)
        profilePartnershipWithText.setOnClickListener { onPartnerClicked() }
        profilePartnerName = view.findViewById(R.id.profilePartnerName)
        profilePartnerName.setOnClickListener { onPartnerClicked() }
        profilePartnerRelation = view.findViewById(R.id.profilePartnerRelation)
        profilePartnerRelation.setOnClickListener { onPartnerClicked() }

        userNameView = view.findViewById(R.id.profileUserName)
        link = view.findViewById(R.id.profileShareableLink)
        linkToggle = view.findViewById(R.id.profileShareableLinkToggle)
        linkToggleText = view.findViewById(R.id.profileShareableLinkToggleText)
        shareLinkButton = view.findViewById(R.id.shareLinkButton)
        numberOfLoves = view.findViewById(R.id.profileNumberOfLoves)
        loveSpotsAdded = view.findViewById(R.id.profileNumberOfLoveSpots)
        reportsSubmitted = view.findViewById(R.id.profileReportsSubmitted)
        reportsReceived = view.findViewById(R.id.profileReportsReceived)
        meaningfulReviews = view.findViewById(R.id.profileMeaningfulReviews)
        points = view.findViewById(R.id.profilePoints)
        pointsToNextLevel = view.findViewById(R.id.profilePointsToNextLevel)
        currentRank = view.findViewById(R.id.current_rank)
        profileProgressBar = view.findViewById(R.id.profileProgressBar)

        linkSharingInfoButton = view.findViewById(R.id.linkSharingInfoButton)
        linkSharingInfoButton.setOnClickListener {
            val infoPopupShower = InfoPopupShower(R.string.link_sharing_hint)
            infoPopupShower.show(view)
        }

        return view
    }

    private fun setRefreshListener(view: View) {
        profileSwipeRefreshLayout = view.findViewById(R.id.profileSwipeRefreshLayout)
        profileSwipeRefreshLayout.setOnRefreshListener {
            profileSwipeRefreshLayout.isRefreshing = true
            fillViewWithData()
        }
    }

    private fun fillViewWithData() {
        MainScope().launch {
            val user = appContext.metadataStore.getUser()
            userNameView.text = user.userName
            val lover = loverService.getMyself()
            lover?.let {
                if (isAdded && !isDetached) {
                    ProfileUtils.setRanks(
                        lover.points,
                        currentRank,
                        pointsToNextLevel,
                        profileProgressBar
                    )
                    setTexts(lover)
                    setPartnerships(requireContext())
                    setLinkSharing(lover)
                }
            }
            profileSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setTexts(lover: LoverRelationsDto) {
        numberOfLoves.text = lover.numberOfLoves.toString()
        loveSpotsAdded.text = lover.loveSpotsAdded.toString()
        reportsSubmitted.text = lover.reportsSubmitted.toString()
        reportsReceived.text = lover.reportsReceived.toString()
        meaningfulReviews.text = lover.reviewsSubmitted.toString()
        points.text = lover.points.toString()
    }

    private fun setPartnerships(context: Context) {
        // TODO: finish this with new /partnerships API call
        MainScope().launch {
            val partnerships = partnershipService.getPartnerships()
            if (partnerships.isNotEmpty()) {
                val partnership = partnerships[0]
                partner = loverService.getOtherById(partnership.getPartnerId())
                partner?.let {
                    profilePartnerName.text = it.displayName
                    profilePartnerRelation.text =
                        I18nUtils.partnershipStatus(
                            partnership.partnershipStatus,
                            context
                        )
                    profilePartnerRelation.visibility = View.VISIBLE
                }
            } else {
                profilePartnerName.text = getString(R.string.profilePartnersEmpty)
                profilePartnerRelation.visibility = View.INVISIBLE
            }
        }
    }

    private fun setLinkSharing(lover: LoverRelationsDto) {
        if (lover.shareableLink != null) {
            linkToggle.isChecked = true
            turnOnSharing(lover.shareableLink)
        } else {
            linkToggle.isChecked = false
            turnOffSharing()
        }
        linkToggle.setOnClickListener {
            MainScope().launch {
                if (linkToggle.isChecked) {
                    val loverDto = loverService.generateLink()
                    loverDto?.shareableLink?.let {
                        turnOnSharing(it)
                    }
                } else {
                    loverService.deleteLink()?.let {
                        turnOffSharing()
                    }
                }
            }

        }
        shareLinkButton.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                putExtra(
                    Intent.EXTRA_TEXT,
                    link.text.toString() + "\n\n" + getString(R.string.share_subject)
                )
                type = "text/plain"
            }
            val shareIntent =
                Intent.createChooser(intent, getString(R.string.my_lovemap_profile_link))
            startActivity(shareIntent)
        }
    }

    private fun onPartnerClicked() {
        partner?.let {
            appContext.otherLoverId = it.id
            startActivity(Intent(requireContext(), ViewOtherLoverActivity::class.java))
        }
    }

    private fun setLogoutListener(view: View) {
        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            runBlocking {
                appContext.deleteAllData()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    private fun turnOnSharing(shareableLink: String) {
        link.animate().alpha(0f).setDuration(250).withEndAction {
            link.text = shareableLink
            link.animate().alpha(1f).duration = 250
        }
        linkToggleText.text = getString(R.string.linkShareOn)
        shareLinkButton.isEnabled = true
    }

    private fun turnOffSharing() {
        link.animate().alpha(0f).setDuration(250).withEndAction {
            link.text = getString(R.string.profileShareableLink)
            link.animate().alpha(1f).duration = 250
        }
        linkToggleText.text = getString(R.string.linkShareOff)
        shareLinkButton.isEnabled = false
    }
}
