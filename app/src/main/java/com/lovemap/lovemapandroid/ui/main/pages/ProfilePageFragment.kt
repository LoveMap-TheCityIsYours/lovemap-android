package com.lovemap.lovemapandroid.ui.main.pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverRelationsDto
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.LoggedInUser
import com.lovemap.lovemapandroid.service.lover.LoverService
import com.lovemap.lovemapandroid.service.lover.relation.RelationService
import com.lovemap.lovemapandroid.ui.login.LoginActivity
import com.lovemap.lovemapandroid.ui.lover.LoverListActivity
import com.lovemap.lovemapandroid.ui.lover.LoverRecyclerViewAdapter.Type.FOLLOWERS
import com.lovemap.lovemapandroid.ui.lover.LoverRecyclerViewAdapter.Type.FOLLOWINGS
import com.lovemap.lovemapandroid.ui.lover.ViewOtherLoverActivity
import com.lovemap.lovemapandroid.ui.main.HallOfFameActivity
import com.lovemap.lovemapandroid.ui.main.newsfeed.NewsFeedFragment
import com.lovemap.lovemapandroid.ui.utils.*
import com.lovemap.lovemapandroid.utils.instantOfApiString
import com.lovemap.lovemapandroid.utils.toFormattedDate
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ProfilePageFragment : Fragment() {

    private lateinit var profileSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var profilePartnerView: LinearLayout
    private lateinit var profilePartnershipImage: ImageView
    private lateinit var displayNameText: EditText
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
    private lateinit var profileProgressBar: ProgressBar
    private lateinit var linkSharingInfoButton: ImageButton
    private lateinit var profilePublicImage: ImageView
    private lateinit var profilePublicToggle: SwitchCompat
    private lateinit var profilePublicToggleText: TextView
    private lateinit var profileNewsFeedContainer: LinearLayout
    private lateinit var profileHallOfFameFab: ExtendedFloatingActionButton
    private lateinit var followingFab: ExtendedFloatingActionButton
    private lateinit var followersFab: ExtendedFloatingActionButton

    private lateinit var profileHofPosition: TextView
    private lateinit var profilePhotosUploaded: TextView
    private lateinit var profilePhotoLikes: TextView
    private lateinit var profilePhotoDislikes: TextView
    private lateinit var profileCreatedAt: TextView
    private lateinit var profileNumberOfFollowings: TextView
    private lateinit var profileNumberOfFollowers: TextView

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
        profilePublicToggle = view.findViewById(R.id.profilePublicToggle)

        displayNameText = view.findViewById(R.id.profileDisplayName)
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
        profilePublicImage = view.findViewById(R.id.profilePublicImage)
        profilePublicToggleText = view.findViewById(R.id.profilePublicToggleText)
        profileNewsFeedContainer = view.findViewById(R.id.profileNewsFeedContainer)

        profileHofPosition = view.findViewById(R.id.profileHofPosition)
        profilePhotosUploaded = view.findViewById(R.id.profilePhotosUploaded)
        profilePhotoLikes = view.findViewById(R.id.profilePhotoLikes)
        profilePhotoDislikes = view.findViewById(R.id.profilePhotoDislikes)
        profileCreatedAt = view.findViewById(R.id.profileCreatedAt)
        profileNumberOfFollowings = view.findViewById(R.id.profileNumberOfFollowings)
        profileNumberOfFollowers = view.findViewById(R.id.profileNumberOfFollowers)

        profileHallOfFameFab = view.findViewById(R.id.profileHallOfFameFab)
        followingFab = view.findViewById(R.id.followingFab)
        followersFab = view.findViewById(R.id.followersFab)

        linkSharingInfoButton = view.findViewById(R.id.linkSharingInfoButton)
        linkSharingInfoButton.setOnClickListener {
            val infoPopupShower = InfoPopupShower(R.string.link_sharing_hint)
            infoPopupShower.show(view)
        }

        profileHallOfFameFab.setOnClickListener {
            startActivity(Intent(requireContext(), HallOfFameActivity::class.java))
        }

        followingFab.setOnClickListener {
            RelationService.LOVER_LIST_TYPE = FOLLOWINGS
            RelationService.LOVER_ID = AppContext.INSTANCE.userId
            startActivity(Intent(requireContext(), LoverListActivity::class.java))
        }

        followersFab.setOnClickListener {
            RelationService.LOVER_LIST_TYPE = FOLLOWERS
            RelationService.LOVER_ID = AppContext.INSTANCE.userId
            startActivity(Intent(requireContext(), LoverListActivity::class.java))
        }

        showActivitiesFragment()

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
        try {
            MainScope().launch {
                val user = appContext.metadataStore.getUser()
                setDisplayNameEditText(user)

                val lover = loverService.getMyself()
                lover?.let {
                    if (isAdded && !isDetached) {
                        ProfileUtils.setRanks(
                            points = lover.points,
                            currentRank = currentRank,
                            animateText = true,
                            pointsToNextLevel = pointsToNextLevel,
                            progressBar = profileProgressBar
                        )
                        setTexts(lover)
                        setPartnerships(requireContext())
                        setLinkSharing(lover)
                        setPublicProfileViews(lover.publicProfile)
                    }
                }
                profileSwipeRefreshLayout.isRefreshing = false
            }
        } catch (e: Exception) {
            Log.e(tag, "fillViewWithData shitted itself", e)
        }
    }

    private fun showActivitiesFragment() {
        runCatching {
            Log.i(tag, "showActivitiesFragment started")
            val newsFeedFragment = NewsFeedFragment
                .newInstance(NewsFeedFragment.NewsFeedType.LOVER_ACTIVITIES, appContext.userId)

            childFragmentManager.beginTransaction()
                .add(R.id.profileNewsFeedContainer, newsFeedFragment)
                .commitAllowingStateLoss()

            Log.i(tag, "showActivitiesFragment finished")
        }.onFailure { e ->
            Log.e(tag, "showActivitiesFragment shitted itself", e)
        }
    }

    private fun setPublicProfileViews(publicProfile: Boolean) {
        profilePublicToggle.isChecked = publicProfile
        ProfileUtils.setPublicPrivateProfileImage(
            publicProfile,
            profilePublicImage,
            profilePublicToggleText
        )
        setProfileToggleChanged()
    }

    private var editingDisplayName: Boolean = false

    private fun setDisplayNameEditText(user: LoggedInUser) {
        displayNameText.setText(user.displayName)
        displayNameText.addTextChangedListener { editable: Editable? ->
            editable?.let {
                for (i in it.length - 1 downTo 0) {
                    if (editable[i] == '\n') {
                        editable.delete(i, i + 1)
                        Log.i(tag, "Enter pressed")
                        if (!editingDisplayName) {
                            editingDisplayName = true
                            editDisplayName(displayNameText)
                        }
                        break
                    }
                }
            }
        }
        displayNameText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!editingDisplayName) {
                    editingDisplayName = true
                    editDisplayName(v)
                }
            }
        }
        displayNameText.setOnKeyListener { v, keyCode, event ->
            return@setOnKeyListener if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                Log.i(tag, "Enter pressed")
                if (!editingDisplayName) {
                    editingDisplayName = true
                    editDisplayName(v)
                }
                true
            } else {
                false
            }
        }
    }

    private fun editDisplayName(view: View) {
        try {
            editingDisplayName = false
            hideKeyboard(view)
            Log.i(tag, "Editing DisplayName to '${displayNameText.text}'")
            MainScope().launch {
                loverService.updateDisplayName(displayNameText.text.toString().trim())?.let {
                    Log.i(tag, "Setting edited DisplayName to '${it.displayName}'")
                    displayNameText.setText(it.displayName)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "editDisplayName shitted itself", e)
        }
    }

    private fun setTexts(lover: LoverRelationsDto) {
        numberOfLoves.text = lover.numberOfLoves.toString()
        loveSpotsAdded.text = lover.loveSpotsAdded.toString()
        reportsSubmitted.text = lover.reportsSubmitted.toString()
        reportsReceived.text = lover.reportsReceived.toString()
        meaningfulReviews.text = lover.reviewsSubmitted.toString()
        points.text = lover.points.toString()

        profileHofPosition.text = lover.hallOfFamePosition?.toString() ?: "1000+"
        profilePhotosUploaded.text = lover.photosUploaded.toString()
        profilePhotoLikes.text = lover.photoLikesReceived.toString()
        profilePhotoDislikes.text = lover.photoDislikesReceived.toString()
        profileNumberOfFollowings.text = lover.numberOfFollowings.toString()
        profileNumberOfFollowers.text = lover.numberOfFollowers.toString()
        profileCreatedAt.text = instantOfApiString(lover.createdAt).toFormattedDate()
    }

    private fun setPartnerships(context: Context) {
        MainScope().launch {
            runCatching {
                val partnership = partnershipService.getPartnership()
                if (partnership != null) {
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
                    partner = null
                    profilePartnerName.text = getString(R.string.profilePartnersEmpty)
                    profilePartnerRelation.visibility = View.INVISIBLE
                }
            }.onFailure { e -> Log.e(tag, "setPartnerships shitted itself", e) }
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
            LoverService.otherLoverId = it.id
            LoverService.otherLover = it
            startActivity(Intent(requireContext(), ViewOtherLoverActivity::class.java))
        }
    }

    private fun setProfileToggleChanged() {
        profilePublicToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AlertDialogUtils.newDialog(requireActivity(),
                    R.string.set_profile_public_title,
                    R.string.set_profile_public_message, {
                        MainScope().launch {
                            loverService.updatePublicProfile(true)?.let {
                                setPublicProfileViews(true)
                            }
                        }
                    }, {
                        profilePublicToggle.isChecked = false
                    })
            }
            if (!isChecked) {
                MainScope().launch {
                    loverService.updatePublicProfile(false)?.let {
                        setPublicProfileViews(false)
                    }
                }
            }
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
