package com.lovemap.lovemapandroid.ui.main.lovespot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityReviewLoveSpotBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReviewLoveSpotActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private lateinit var binding: ActivityReviewLoveSpotBinding
    private lateinit var reviewSpotSubmit: Button
    private lateinit var reviewSpotCancel: Button

    private var rating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setReviewRatingBar()
        setSubmitButton()
        setCancelButton()
    }

    private fun initViews() {
        binding = ActivityReviewLoveSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reviewSpotSubmit = binding.reviewSpotSubmit
        reviewSpotCancel = binding.reviewSpotCancel
    }

    private fun setSubmitButton() {
        reviewSpotSubmit.setOnClickListener {
            if (reviewSpotSubmit.isEnabled) {
                MainScope().launch {
                    appContext.selectedMarker?.let {
                        val spotId = it.snippet!!.toLong()
//                        val loveSpot = loveSpotService.findLocally(spotId)
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
//                            appContext.zoomOnNewLoveSpot = loveSpot
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
    }

    private fun setCancelButton() {
        reviewSpotCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setReviewRatingBar() {
        findViewById<RatingBar>(R.id.spotReviewRating).setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingValue.toInt()
            reviewSpotSubmit.isEnabled = ratingValid()
        }
    }

    private fun ratingValid() = rating != 0
}