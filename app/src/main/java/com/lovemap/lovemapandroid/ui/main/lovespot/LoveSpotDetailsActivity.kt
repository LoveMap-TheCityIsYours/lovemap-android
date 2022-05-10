package com.lovemap.lovemapandroid.ui.main.lovespot

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityLoveSpotDetailsBinding
import com.lovemap.lovemapandroid.ui.utils.LoveSpotDetailsUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotDetailsActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private lateinit var binding: ActivityLoveSpotDetailsBinding
    private lateinit var spotDetailsRating: RatingBar
    private lateinit var spotDetailsRisk: TextView
    private lateinit var spotDetailsAvailability: TextView
    private lateinit var spotDetailsDescription: TextView
    private lateinit var spotDetailsCustomAvailabilityText: TextView
    private lateinit var spotDetailsCustomAvailability: TextView
    private lateinit var reviewSpotSubmit: Button
    private lateinit var reviewSpotCancel: Button

    private var rating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setReviewRatingBar()
        setSubmitButton()
        setCancelButton()
        setDetails()
    }

    private fun initViews() {
        binding = ActivityLoveSpotDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reviewSpotSubmit = binding.reviewSpotSubmit
        reviewSpotCancel = binding.reviewSpotCancel
        spotDetailsRating = binding.spotDetailsRating
        spotDetailsRisk = binding.spotDetailsRisk
        spotDetailsAvailability = binding.spotDetailsAvailability
        spotDetailsDescription = binding.spotDetailsDescription
        spotDetailsCustomAvailabilityText = binding.spotDetailsCustomAvailabilityText
        spotDetailsCustomAvailability = binding.spotDetailsCustomAvailability
    }

    private fun setDetails() {
        MainScope().launch {
            val spotId = appContext.selectedMarker!!.snippet!!.toLong()
            val loveSpot = loveSpotService.refresh(spotId)
            loveSpot?.let {
                binding.loveSpotDetailsTitle.text = loveSpot.name
                spotDetailsDescription.text = loveSpot.description
                LoveSpotDetailsUtils.setRating(
                    loveSpot,
                    spotDetailsRating
                )
                LoveSpotDetailsUtils.setAvailability(
                    loveSpot,
                    applicationContext,
                    spotDetailsAvailability
                )
                LoveSpotDetailsUtils.setRisk(
                    loveSpot,
                    loveSpotService,
                    applicationContext,
                    spotDetailsRisk
                )
                LoveSpotDetailsUtils.setCustomAvailability(
                    loveSpot,
                    applicationContext,
                    spotDetailsCustomAvailabilityText,
                    spotDetailsCustomAvailability
                )
            }
        }
    }

    private fun setSubmitButton() {
        reviewSpotSubmit.setOnClickListener {
            if (reviewSpotSubmit.isEnabled) {
                MainScope().launch {
                    val spotId = appContext.selectedMarker!!.snippet!!.toLong()
                    val love = loveService.getLoveByLoveSpotId(spotId)
                    love?.let {
                        val reviewedSpot = loveSpotReviewService.addReview(
                            LoveSpotReviewRequest(
                                love.id,
                                appContext.userId,
                                spotId,
                                findViewById<EditText>(R.id.addReviewText).text.toString(),
                                rating,
                                findViewById<Spinner>(R.id.spotRiskDropdown).selectedItemPosition + 1
                            )
                        )
                        reviewedSpot?.let {
                            loveSpotService.update(reviewedSpot)
                        }

                        appContext.toaster.showToast(R.string.love_spot_reviewed)
                        appContext.shouldCloseFabs = true
                        onBackPressed()
                    } ?: run {
                        appContext.toaster.showToast(R.string.havent_made_love_on_this_spot_yet)
                        appContext.shouldCloseFabs = true
                        onBackPressed()
                    }

                }
            }
        }
    }

    private fun setCancelButton() {
        reviewSpotCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setReviewRatingBar() {
        findViewById<RatingBar>(R.id.marker_review_rating_bar).setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingValue.toInt()
            reviewSpotSubmit.isEnabled = ratingValid()
        }
    }

    private fun ratingValid() = rating != 0
}