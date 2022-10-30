package com.lovemap.lovemapandroid.ui.main.love

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.love.UpdateLoveRequest
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.databinding.ActivityRecordLoveBinding
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewLoveSpotFragment
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import com.lovemap.lovemapandroid.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RecordLoveActivity : AppCompatActivity() {
    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private var editMode: Boolean = false
    private var editedLoveId: Long = 0
    private var editedLove: Love? = null
    private lateinit var binding: ActivityRecordLoveBinding
    private lateinit var recordLoveSubmit: Button
    private lateinit var recordLoveMakingTitle: TextView
    private lateinit var recordLoveSpotName: TextView
    private lateinit var reviewLoveSpotFragment: ReviewLoveSpotFragment
    private lateinit var recordLoveFragment: RecordLoveFragment

    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editedLoveId = intent.getLongExtra(EDIT, 0)
        editMode = editedLoveId != 0L
        initViews()
        setSubmitButton()
    }

    private fun initViews() {
        binding = ActivityRecordLoveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reviewLoveSpotFragment =
            supportFragmentManager.findFragmentById(R.id.recordLoveReviewLoveSpotFragment) as ReviewLoveSpotFragment
        recordLoveFragment =
            supportFragmentManager.findFragmentById(R.id.recordLoveRecordLoveFragment) as RecordLoveFragment

        supportFragmentManager
            .beginTransaction()
            .hide(reviewLoveSpotFragment)
            .commit()

        recordLoveSubmit = binding.recordLoveSubmit
        recordLoveMakingTitle = binding.recordLoveMakingTitle
        recordLoveSpotName = binding.recordLoveSpotName

        binding.addLoveCancel.setOnClickListener {
            onBackPressed()
        }

        findViewById<RatingBar>(R.id.marker_review_rating_bar)
            .setOnRatingBarChangeListener { _, ratingValue, _ ->
                rating = ratingValue.toInt()
                recordLoveSubmit.isEnabled = rating != 0
            }

        if (editMode) {
            initViewsIfEditMode()
        } else {
            initViewsIfCreateMode()
        }
    }

    private fun initViewsIfCreateMode() {
        appContext.selectedLoveSpotId?.let {
            val spotId = appContext.selectedLoveSpotId!!
            MainScope().launch {
                val loveSpot =  loveSpotService.findLocally(spotId)
                recordLoveSpotName.text = loveSpot!!.name
                if (loveSpotReviewService.hasReviewedAlready(spotId)) {
                    recordLoveSubmit.isEnabled = true
                } else {
                    supportFragmentManager
                        .beginTransaction()
                        .show(reviewLoveSpotFragment)
                        .commit()
                }
            }
        }
    }

    private fun initViewsIfEditMode() {
        recordLoveMakingTitle.text = getString(R.string.editLoveMakingTitle)
        recordLoveSubmit.isEnabled = true
        MainScope().launch {
            editedLove = loveService.getLocally(editedLoveId)
            editedLove?.let {
                recordLoveSpotName.text = it.name
                if (loveService.isPartnerInLove(it)) {
                    recordLoveFragment.recordLoveSelectPartnerDropdown.isEnabled = false
                } else {
                    recordLoveFragment.recordLoveSelectPartnerDropdown.setSelection(0, true)
                }
                val happenedAt = instantOfApiString(it.happenedAt)
                recordLoveFragment.selectedDateTime = LocalDateTime.ofInstant(happenedAt, timeZone.toZoneId())
                recordLoveFragment.recordLoveHappenedAt.text =
                    happenedAt.toFormattedString()
                recordLoveFragment.addPrivateNote.text = it.note
            }
        }
    }

    private fun setSubmitButton() {
        recordLoveSubmit.setOnClickListener {
            if (recordLoveSubmit.isEnabled) {
                MainScope().launch {
                    val loadingBarShower = LoadingBarShower(this@RecordLoveActivity).show()
                    if (!editMode) {
                        createLoveAndReview()
                    } else {
                        val love = updateLove()
                        if (love != null) {
                            goBack()
                        }
                    }
                    loadingBarShower.onResponse()
                }
            }
        }
    }

    private suspend fun createLoveAndReview() {
        appContext.selectedLoveSpotId?.let {
            val spotId = it
            val love = createLove(spotId)
            if (love != null) {
                submitReview(spotId, love)
                goBack()
            }
        }
    }

    private suspend fun createLove(spotId: Long): Love? {
        val loveSpot = loveSpotService.findLocally(spotId)!!
        return loveService.create(
            CreateLoveRequest(
                loveSpot.name,
                spotId,
                appContext.userId,
                recordLoveFragment.selectedDateTime.toInstant().toApiString(),
                recordLoveFragment.selectedPartner(),
                recordLoveFragment.addPrivateNote.text.toString()
            )
        )
    }

    private suspend fun updateLove(): Love? {
        return editedLove?.let {
            loveService.update(
                editedLoveId,
                UpdateLoveRequest(
                    it.name,
                    recordLoveFragment.selectedDateTime.toInstant().toApiString(),
                    recordLoveFragment.selectedPartner(),
                    recordLoveFragment.addPrivateNote.text.toString(),
                )
            )
        }
    }

    private suspend fun submitReview(
        spotId: Long,
        love: Love
    ) {
        if (!loveSpotReviewService.hasReviewedAlready(spotId) && !editMode) {
            val reviewText =
                findViewById<EditText>(R.id.addReviewText).text.toString()
            val riskLevel =
                findViewById<Spinner>(R.id.spotRiskDropdown).selectedItemPosition + 1
            val reviewedSpot = loveSpotReviewService.submitReview(
                LoveSpotReviewRequest(
                    love.id,
                    appContext.userId,
                    spotId,
                    reviewText,
                    rating,
                    riskLevel
                )
            )
            reviewedSpot?.let {
                loveSpotService.insertIntoDb(reviewedSpot)
            }
        }
    }

    private fun goBack() {
        appContext.toaster.showToast(R.string.lovemaking_recorded)
        onBackPressed()
    }

    companion object {
        const val EDIT = "edit"
    }
}