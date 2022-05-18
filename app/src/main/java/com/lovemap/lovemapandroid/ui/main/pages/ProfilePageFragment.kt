package com.lovemap.lovemapandroid.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.login.LoginActivity
import com.lovemap.lovemapandroid.ui.utils.isPartner
import com.lovemap.lovemapandroid.ui.utils.partnersFromRelations
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfilePageFragment : Fragment() {

    private lateinit var userNameView: TextView
    private lateinit var partnersView: TextView
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

    private val loverService = AppContext.INSTANCE.loverService
    private val partnershipService = AppContext.INSTANCE.partnershipService

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
        userNameView = view.findViewById(R.id.profileUserName)
        partnersView = view.findViewById(R.id.profilePartners)
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
        currentRank = view.findViewById(R.id.profileUserLevelText)
        profileShareDescription = view.findViewById(R.id.profileShareDescription)
        return view
    }

    private fun fillViewWithData() {
        MainScope().launch {
            val user = AppContext.INSTANCE.metadataStore.getUser()
            userNameView.text = user.userName
            val lover = loverService.getById()
            lover?.let {
                setRanks(lover)
                setTexts(lover)
                setPartnerships(lover)
                setLinkSharing(lover)
            }
        }
    }

    private suspend fun setRanks(lover: LoverRelationsDto) {
        val ranks = loverService.getRanks()
        ranks?.let {
            val rankList = ranks.rankList
            var levelIndex = 1
            for ((index, rank) in rankList.withIndex()) {
                levelIndex = if (rank.pointsNeeded > lover.points) {
                    levelIndex = index
                    break
                } else {
                    1
                }
            }
            currentRank.text = rankList[levelIndex - 1].nameEN
            pointsToNextLevel.text = rankList[levelIndex].pointsNeeded.toString()
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

    fun setPartnerships(lover: LoverRelationsDto) {
        // TODO: finish this with new /partnerships API call 
//        val partnerships = partnershipService.getPartnerships()
//        if (partnerships.isNotEmpty()) {
//            partnersView.text =
//        }
        if (lover.relations.any { isPartner(it) }) {
            partnersView.text = partnersFromRelations(lover.relations)
                .joinToString { it.userName }
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

    private fun setLogoutListener(view: View) {
        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            runBlocking {
                AppContext.INSTANCE.deleteAllData()
            }
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun turnOnSharing(shareableLink: String) {
        link.animate().alpha(0f).setDuration(250).withEndAction {
            link.text = shareableLink
            link.animate().alpha(1f).duration = 250
        }
        linkToggleText.text = getString(R.string.linkShareOn)
        shareLinkButton.isEnabled = true
        profileShareDescription.visibility = View.VISIBLE
        profileShareDescription.animate().alpha(1f).duration = 500
    }

    private fun turnOffSharing() {
        link.animate().alpha(0f).setDuration(250).withEndAction {
            link.text = getString(R.string.profileShareableLink)
            link.animate().alpha(1f).duration = 250
        }
        linkToggleText.text = getString(R.string.linkShareOff)
        shareLinkButton.isEnabled = false
        profileShareDescription.animate().alpha(0f).apply {
            withEndAction {
                profileShareDescription.visibility = View.GONE
            }
        }.duration = 500

    }
}
