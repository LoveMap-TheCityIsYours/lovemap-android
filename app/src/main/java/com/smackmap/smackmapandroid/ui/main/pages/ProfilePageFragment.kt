package com.smackmap.smackmapandroid.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.api.relation.RelationStatusDto
import com.smackmap.smackmapandroid.api.smacker.SmackerViewDto
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.ui.login.LoginActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfilePageFragment : Fragment() {

    private lateinit var link: TextView
    private lateinit var linkToggle: SwitchCompat
    private lateinit var linkToggleText: TextView

    private val smackerService = AppContext.INSTANCE.smackerService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)
        val userNameView = view.findViewById<TextView>(R.id.profileUserName)
        val partnersView = view.findViewById<TextView>(R.id.profilePartners)
        link = view.findViewById(R.id.profileShareableLink)
        linkToggle = view.findViewById(R.id.profileShareableLinkToggle)
        linkToggleText = view.findViewById(R.id.profileShareableLinkToggleText)

        MainScope().launch {
            val user = AppContext.INSTANCE.userDataStore.get()
            userNameView.text = user.userName
            val smacker = smackerService.getById()
            smacker?.let {
                if (smacker.relations.any { partnerFilter(it) }) {
                    partnersView.text = smacker.relations
                        .filter { partnerFilter(it) }
                        .joinToString { it.userName }
                }
                partnersView.text
                if (smacker.shareableLink != null) {
                    linkToggle.isChecked = true
                    turnOnSharing(smacker.shareableLink)
                } else {
                    linkToggle.isChecked = false
                    turnOffSharing()
                }
            }
        }

        linkToggle.setOnClickListener {
            MainScope().launch {
                if (linkToggle.isChecked) {
                    val smacker = smackerService.generateLink()
                    smacker?.shareableLink?.let {
                        turnOnSharing(it)
                    }
                } else {
                    smackerService.deleteLink()?.let {
                        turnOffSharing()
                    }
                }
            }
        }

        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            runBlocking {
                AppContext.INSTANCE.userDataStore.delete()
            }
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        return view
    }

    private fun partnerFilter(it: SmackerViewDto) =
        it.relation == RelationStatusDto.PARTNER

    private fun turnOnSharing(shareableLink: String) {
        link.text = shareableLink
        linkToggleText.text = getString(R.string.linkShareOn)
    }

    private fun turnOffSharing() {
        link.text = getString(R.string.profileShareableLink)
        linkToggleText.text = getString(R.string.linkShareOff)
    }
}