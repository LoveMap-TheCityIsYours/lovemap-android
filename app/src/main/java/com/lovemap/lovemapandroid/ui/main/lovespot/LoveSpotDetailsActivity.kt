package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.databinding.ActivityLoveSpotDetailsBinding
import com.lovemap.lovemapandroid.ui.main.love.LoveListFragment
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.review.LoveSpotReviewListFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewLoveSpotFragment
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

    private lateinit var detailsReviewLoveSpotFragment: ReviewLoveSpotFragment
    private lateinit var haveNotMadeLoveReviewText: TextView
    private lateinit var detailsReviewButtons: LinearLayout
    private lateinit var reviewSpotSubmit: Button
    private lateinit var reviewSpotCancel: Button

    private lateinit var addToWishlistFabOnDetails: FloatingActionButton
    private lateinit var makeLoveFabOnDetails: FloatingActionButton
    private lateinit var haveNotMadeLoveText: TextView

    private lateinit var spotDetailsReportButton: ExtendedFloatingActionButton

    private lateinit var detailsLoveListFragment: LoveListFragment
    private lateinit var detailsReviewListFragment: LoveSpotReviewListFragment

    private var spotId: Long = 0
    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spotId = appContext.selectedLoveSpotId!!
        initViews()
        spotDetailsReportButton.setOnClickListener {
            startActivity(Intent(applicationContext, ReportLoveSpotActivity::class.java))
        }
        addToWishlistFabOnDetails.setOnClickListener {
            appContext.toaster.showToast(R.string.not_yet_implemented)
        }
        setReviewRatingBar()
        setSubmitButton()
        setCancelButton()
        setMakeLoveButton()
    }

    override fun onResume() {
        super.onResume()
        setDetails()
        setDynamicTexts()
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
        haveNotMadeLoveReviewText = binding.haveNotMadeLoveReviewText
        haveNotMadeLoveText = binding.haveNotMadeLoveText
        detailsReviewButtons = binding.detailsReviewButtons
        makeLoveFabOnDetails = binding.makeLoveFabOnDetails
        spotDetailsReportButton = binding.spotDetailsReportButton
        addToWishlistFabOnDetails = binding.addToWishlistFabOnDetails
        detailsReviewLoveSpotFragment =
            supportFragmentManager.findFragmentById(R.id.detailsReviewLoveSpotFragment) as ReviewLoveSpotFragment

        detailsLoveListFragment =
            supportFragmentManager.findFragmentById(R.id.detailsLoveListFragment) as LoveListFragment
        (detailsLoveListFragment.view as RecyclerView).isNestedScrollingEnabled = false

        detailsReviewListFragment =
            supportFragmentManager.findFragmentById(R.id.detailsReviewListFragment) as LoveSpotReviewListFragment
        (detailsReviewListFragment.view as RecyclerView).isNestedScrollingEnabled = false
    }

    private fun setDetails() {
        MainScope().launch {
            appContext.selectedLoveSpot = loveSpotService.findLocally(spotId)
            setDetails(appContext.selectedLoveSpot)
            val loveSpot = loveSpotService.refresh(spotId)
            setDetails(loveSpot)
        }
    }

    private suspend fun setDetails(loveSpot: LoveSpot?) {
        loveSpot?.let {
            binding.loveSpotDetailsTitle.text = loveSpot.name
            spotDetailsDescription.text = loveSpot.description
            LoveSpotDetailsUtils.setRating(
                loveSpot.averageRating,
                spotDetailsRating
            )
            LoveSpotDetailsUtils.setAvailability(
                loveSpot.availability,
                applicationContext,
                spotDetailsAvailability
            )
            LoveSpotDetailsUtils.setRisk(
                loveSpot.averageDanger,
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

    private fun setSubmitButton() {
        reviewSpotSubmit.setOnClickListener {
            if (reviewSpotSubmit.isEnabled) {
                MainScope().launch {
                    val love = loveService.getLoveByLoveSpotId(spotId)
                    love?.let {
                        val reviewedSpot = loveSpotReviewService.submitReview(
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

    private fun setMakeLoveButton() {
        makeLoveFabOnDetails.setOnClickListener {
            startActivity(Intent(applicationContext, RecordLoveActivity::class.java))
        }
    }

    private fun setDynamicTexts() {
        MainScope().launch {
            if (loveService.madeLoveAlready(spotId)) {
                detailsReviewLoveSpotFragment.view?.visibility = View.VISIBLE
                detailsReviewButtons.visibility = View.VISIBLE
                haveNotMadeLoveReviewText.visibility = View.GONE
                haveNotMadeLoveText.visibility = View.GONE
            } else {
                detailsReviewLoveSpotFragment.view?.visibility = View.GONE
                detailsReviewButtons.visibility = View.GONE
                haveNotMadeLoveReviewText.visibility = View.VISIBLE
                haveNotMadeLoveText.visibility = View.VISIBLE
            }
        }
    }

    private fun ratingValid() = rating != 0
}