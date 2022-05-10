package com.lovemap.lovemapandroid.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.relation.RelationStatusDto
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
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
    private lateinit var numberOfLoves: TextView
    private lateinit var loveSpotsAdded: TextView
    private lateinit var reports: TextView
    private lateinit var points: TextView
    private lateinit var pointsToNextLevel: TextView
    private lateinit var currentRank: TextView

    private val loverService = AppContext.INSTANCE.loverService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = initViews(inflater, container)
        fillViewWithData()
        setLogoutListener(view)
        return view
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
        numberOfLoves = view.findViewById(R.id.profileNumberOfLoves)
        loveSpotsAdded = view.findViewById(R.id.profileNumberOfLoveSpots)
        reports = view.findViewById(R.id.profileNumberOfReports)
        points = view.findViewById(R.id.profilePoints)
        pointsToNextLevel = view.findViewById(R.id.profilePointsToNextLevel)
        currentRank = view.findViewById(R.id.profileUserLevelText)
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
        reports.text = lover.numberOfReports.toString()
        points.text = lover.points.toString()
    }

    private fun setPartnerships(lover: LoverRelationsDto) {
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
                    val lover = loverService.generateLink()
                    lover?.shareableLink?.let {
                        turnOnSharing(it)
                    }
                } else {
                    loverService.deleteLink()?.let {
                        turnOffSharing()
                    }
                }
            }
        }
    }

    private fun setLogoutListener(view: View) {
        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            runBlocking {
                AppContext.INSTANCE.metadataStore.deleteAll()
            }
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun turnOnSharing(shareableLink: String) {
        link.text = shareableLink
        linkToggleText.text = getString(R.string.linkShareOn)
    }

    private fun turnOffSharing() {
        link.text = getString(R.string.profileShareableLink)
        linkToggleText.text = getString(R.string.linkShareOff)
    }
}
