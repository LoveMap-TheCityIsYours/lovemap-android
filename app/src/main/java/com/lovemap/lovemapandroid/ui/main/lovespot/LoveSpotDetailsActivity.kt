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
import com.lovemap.lovemapandroid.ui.main.MainActivity
import com.lovemap.lovemapandroid.ui.main.love.LoveListActivity
import com.lovemap.lovemapandroid.ui.main.love.LoveListFragment
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.review.LoveSpotReviewListFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewListActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewLoveSpotFragment
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.utils.IS_CLICKABLE
import com.lovemap.lovemapandroid.utils.canEditLoveSpot
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class
LoveSpotDetailsActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private lateinit var binding: ActivityLoveSpotDetailsBinding
    private lateinit var spotDetailsRating: RatingBar
    private lateinit var spotDetailsRisk: TextView
    private lateinit var spotDetailsAvailability: TextView
    private lateinit var spotDetailsType: TextView
    private lateinit var spotDetailsDescription: TextView
    private lateinit var spotDetailsCustomAvailabilityText: TextView
    private lateinit var spotDetailsCustomAvailability: TextView
    private lateinit var loveSpotDetailsTypeImage: ImageView

    private lateinit var detailsReviewLoveSpotFragment: ReviewLoveSpotFragment
    private lateinit var haveNotMadeLoveReviewText: TextView
    private lateinit var detailsReviewButtons: LinearLayout
    private lateinit var reviewSpotSubmit: Button
    private lateinit var reviewSpotCancel: Button
    private lateinit var detailsSeeAllReviewsButton: Button

    private lateinit var detailsAddToWishlistButton: FloatingActionButton
    private lateinit var makeLoveFabOnDetails: FloatingActionButton
    private lateinit var haveNotMadeLoveText: TextView

    private lateinit var spotDetailsReportButton: ExtendedFloatingActionButton
    private lateinit var spotDetailsShowOnMapButton: ExtendedFloatingActionButton
    private lateinit var spotDetailsEditButton: ExtendedFloatingActionButton

    private lateinit var detailsLoveListFragment: LoveListFragment
    private lateinit var detailsSeeAllLovesButton: Button
    private lateinit var detailsReviewListFragment: LoveSpotReviewListFragment

    private var spotId: Long = 0
    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext.selectedLoveSpotId?.let {
            spotId = appContext.selectedLoveSpotId!!
            initViews()
            spotDetailsReportButton.setOnClickListener {
                startActivity(Intent(applicationContext, ReportLoveSpotActivity::class.java))
            }
            spotDetailsEditButton.setOnClickListener {
                val intent = Intent(this, AddLoveSpotActivity::class.java)
                intent.putExtra(AddLoveSpotActivity.EDIT, spotId)
                startActivity(intent)
            }
            detailsAddToWishlistButton.setOnClickListener {
                appContext.toaster.showToast(R.string.not_yet_implemented)
            }
            detailsSeeAllLovesButton.setOnClickListener {
                val intent = Intent(applicationContext, LoveListActivity::class.java)
                intent.putExtra(IS_CLICKABLE, false)
                startActivity(intent)
            }
            detailsSeeAllReviewsButton.setOnClickListener {
                startActivity(Intent(applicationContext, ReviewListActivity::class.java))
            }
            spotDetailsShowOnMapButton.setOnClickListener {
                appContext.zoomOnLoveSpot = appContext.selectedLoveSpot
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            setReviewRatingBar()
            setReviewSubmitButton()
            setCancelButton()
            setMakeLoveButton()
        }
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
        loveSpotDetailsTypeImage = binding.loveSpotDetailsTypeImage
        spotDetailsType = binding.spotDetailsType
        spotDetailsDescription = binding.spotDetailsDescription
        spotDetailsCustomAvailabilityText = binding.spotDetailsCustomAvailabilityText
        spotDetailsCustomAvailability = binding.spotDetailsCustomAvailability
        haveNotMadeLoveReviewText = binding.haveNotMadeLoveReviewText
        haveNotMadeLoveText = binding.haveNotMadeLoveText
        detailsReviewButtons = binding.detailsReviewButtons
        makeLoveFabOnDetails = binding.makeLoveFabOnDetails
        spotDetailsReportButton = binding.spotDetailsReportButton
        detailsAddToWishlistButton = binding.addToWishlistFabOnDetails
        detailsSeeAllReviewsButton = binding.detailsSeeAllReviewsButton
        detailsSeeAllLovesButton = binding.detailsSeeAllLovesButton
        spotDetailsEditButton = binding.spotDetailsEditButton
        spotDetailsShowOnMapButton = binding.spotDetailsShowOnMap
        detailsReviewLoveSpotFragment =
            supportFragmentManager.findFragmentById(R.id.detailsReviewLoveSpotFragment) as ReviewLoveSpotFragment

        detailsLoveListFragment =
            supportFragmentManager.findFragmentById(R.id.detailsLoveListFragment) as LoveListFragment
        (detailsLoveListFragment.view?.findViewById(R.id.loveList) as RecyclerView).isNestedScrollingEnabled =
            false

        detailsReviewListFragment =
            supportFragmentManager.findFragmentById(R.id.detailsReviewListFragment) as LoveSpotReviewListFragment
        (detailsReviewListFragment.view?.findViewById(R.id.reviewList) as RecyclerView).isNestedScrollingEnabled =
            false
    }

    private fun setDetails() {
        MainScope().launch {
            appContext.selectedLoveSpot = loveSpotService.findLocally(spotId)
            setDetails(appContext.selectedLoveSpot)
            appContext.selectedLoveSpot = loveSpotService.refresh(spotId)
            setDetails(appContext.selectedLoveSpot)
        }
    }

    private fun setDetails(loveSpot: LoveSpot?) {
        loveSpot?.let {
            binding.loveSpotTitle.text = loveSpot.name
            spotDetailsDescription.text = loveSpot.description
            LoveSpotUtils.setRating(
                loveSpot.averageRating,
                spotDetailsRating
            )
            LoveSpotUtils.setAvailability(
                loveSpot.availability,
                spotDetailsAvailability
            )
            LoveSpotUtils.setType(
                loveSpot.type,
                spotDetailsType
            )
            LoveSpotUtils.setRisk(
                loveSpot.averageDanger,
                spotDetailsRisk
            )
            LoveSpotUtils.setCustomAvailability(
                loveSpot,
                spotDetailsCustomAvailabilityText,
                spotDetailsCustomAvailability
            )
            LoveSpotUtils.setTypeImage(
                loveSpot.type,
                loveSpotDetailsTypeImage
            )
            if (canEditLoveSpot(loveSpot.addedBy)) {
                spotDetailsEditButton.visibility = View.VISIBLE
            }
        }
    }

    private fun setReviewSubmitButton() {
        reviewSpotSubmit.setOnClickListener {
            if (reviewSpotSubmit.isEnabled) {
                MainScope().launch {
                    val love = loveService.getAnyLoveByLoveSpotId(spotId)
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
                            loveSpotService.insertIntoDb(reviewedSpot)
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