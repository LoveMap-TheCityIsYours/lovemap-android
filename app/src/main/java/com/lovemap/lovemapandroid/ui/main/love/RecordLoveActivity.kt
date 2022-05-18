package com.lovemap.lovemapandroid.ui.main.love

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityRecordLoveBinding
import com.lovemap.lovemapandroid.ui.main.lovespot.ReviewLoveSpotFragment
import com.lovemap.lovemapandroid.ui.utils.toApiString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RecordLoveActivity : AppCompatActivity() {
    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private lateinit var binding: ActivityRecordLoveBinding
    private lateinit var recordLoveSubmit: Button
    private lateinit var reviewLoveSpotFragment: ReviewLoveSpotFragment
    private lateinit var recordLoveFragment: RecordLoveFragment

    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        recordLoveSubmit = binding.recordLoveSubmit

        binding.addLoveCancel.setOnClickListener {
            onBackPressed()
        }

        findViewById<RatingBar>(R.id.marker_review_rating_bar).setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingValue.toInt()
            recordLoveSubmit.isEnabled = rating != 0
        }

        supportFragmentManager
            .beginTransaction()
            .hide(reviewLoveSpotFragment)
            .commit()

        appContext.selectedMarker?.let {
            val spotId = it.snippet!!.toLong()
            MainScope().launch {
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

    private fun setSubmitButton() {
        recordLoveSubmit.setOnClickListener {
            if (recordLoveSubmit.isEnabled) {
                appContext.selectedMarker?.let {
                    val spotId = it.snippet!!.toLong()
                    MainScope().launch {
                        val loveSpot = loveSpotService.findLocally(spotId)!!
                        val love = loveService.create(
                            CreateLoveRequest(
                                loveSpot.name,
                                spotId,
                                appContext.userId,
                                recordLoveFragment.selectedTime.toApiString(),
                                recordLoveFragment.selectedPartner(),
                                recordLoveFragment.addPrivateNote.text.toString()
                            )
                        )
                        if (love != null) {
                            if (!loveSpotReviewService.hasReviewedAlready(spotId)) {
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
                                    loveSpotService.update(reviewedSpot)
                                }
                            }
                            goBack()
                        }
                    }
                }
            }
        }
    }

    private fun goBack() {
        appContext.toaster.showToast(R.string.lovemaking_recorded)
        appContext.shouldMoveMapCamera = true
        onBackPressed()
    }
}