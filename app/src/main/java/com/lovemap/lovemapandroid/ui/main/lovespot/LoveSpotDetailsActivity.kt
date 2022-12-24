package com.lovemap.lovemapandroid.ui.main.lovespot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.config.MapContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.databinding.ActivityLoveSpotDetailsBinding
import com.lovemap.lovemapandroid.ui.events.ShowOnMapClickedEvent
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.love.list.LoveListActivity
import com.lovemap.lovemapandroid.ui.main.love.list.LoveListFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.report.ReportLoveSpotActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.review.LoveSpotReviewListFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewListActivity
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewLoveSpotFragment
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils
import com.lovemap.lovemapandroid.ui.utils.PhotoUploadUtils
import com.lovemap.lovemapandroid.utils.IS_CLICKABLE
import com.lovemap.lovemapandroid.utils.canEditLoveSpot
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class
LoveSpotDetailsActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService
    private val loveSpotPhotoService = appContext.loveSpotPhotoService

    private lateinit var binding: ActivityLoveSpotDetailsBinding
    private lateinit var detailsScrollView: ScrollView
    private lateinit var spotDetailsRating: RatingBar
    private lateinit var spotDetailsRisk: TextView
    private lateinit var spotDetailsAvailability: TextView
    private lateinit var spotDetailsType: TextView
    private lateinit var spotDetailsDescription: TextView
    private lateinit var spotDetailsCustomAvailabilityText: TextView
    private lateinit var spotDetailsCustomAvailability: TextView
    private lateinit var loveSpotDetailsTypeImage: ImageView

    private var detailsReviewLoveSpotFragment: ReviewLoveSpotFragment? = null
    private lateinit var haveNotMadeLoveReviewText: TextView
    private lateinit var detailsReviewButtons: LinearLayout
    private lateinit var reviewSpotSubmit: Button
    private lateinit var reviewSpotCancel: Button
    private lateinit var detailsSeeAllReviewsButton: Button

    private lateinit var detailsAddToWishlistButton: ExtendedFloatingActionButton
    private lateinit var makeLoveFabOnDetails: ExtendedFloatingActionButton
    private lateinit var detailsReviewSpotButton: ExtendedFloatingActionButton
    private lateinit var haveNotMadeLoveText: TextView

    private lateinit var spotDetailsReportButton: ExtendedFloatingActionButton
    private lateinit var spotDetailsShowOnMapButton: ExtendedFloatingActionButton
    private lateinit var spotDetailsEditButton: ExtendedFloatingActionButton

    private lateinit var detailsLoveListFragment: LoveListFragment
    private lateinit var detailsSeeAllLovesButton: Button
    private lateinit var detailsReviewListFragment: LoveSpotReviewListFragment

    private lateinit var loveSpotDetailsImagesRV: RecyclerView
    private lateinit var detailsNoPhotoViewGroup: LinearLayout
    private lateinit var spotDetailsUploadButton: ExtendedFloatingActionButton
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var loveSpotId: Long = 0
    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext.selectedLoveSpotId?.let {
            loveSpotId = appContext.selectedLoveSpotId!!
            initViews()
            spotDetailsReportButton.setOnClickListener {
                startActivity(Intent(applicationContext, ReportLoveSpotActivity::class.java))
            }
            spotDetailsEditButton.setOnClickListener {
                val intent = Intent(this, AddLoveSpotActivity::class.java)
                intent.putExtra(AddLoveSpotActivity.EDIT, loveSpotId)
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
            detailsReviewSpotButton.setOnClickListener {
                detailsScrollView.post {
                    detailsScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
            detailsSeeAllReviewsButton.setOnClickListener {
                startActivity(Intent(applicationContext, ReviewListActivity::class.java))
            }
            spotDetailsShowOnMapButton.setOnClickListener {
                EventBus.getDefault().post(ShowOnMapClickedEvent(loveSpotId))
                finish()
            }
            detailsNoPhotoViewGroup = binding.detailsNoPhotoViewGroup
            spotDetailsUploadButton = binding.spotDetailsUploadButton
            loveSpotDetailsImagesRV = binding.loveSpotDetailsImagesRV
            loveSpotDetailsImagesRV.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            setReviewRatingBar()
            setReviewSubmitButton()
            setCancelButton()
            setMakeLoveButton()
            setUploadButton()


            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data!!
                    // Use the uri to load the image
                    // Only if you are not using crop feature:
//                    contentResolver.takePersistableUriPermission(
//                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
                }
            }
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
        detailsScrollView = binding.root
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
        detailsReviewSpotButton = binding.detailsReviewSpotButton
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
            var loveSpot = loveSpotService.findLocally(loveSpotId)
            setDetails(loveSpot)

            val photosLoaded = loveSpot?.let { loadPhotos(it); true } ?: false

            loveSpot = loveSpotService.refresh(loveSpotId)
            setDetails(loveSpot)

            if (!photosLoaded) {
                loveSpot?.let { loadPhotos(it) }
            }
        }
    }

    private suspend fun loadPhotos(loveSpot: LoveSpot) {
        if (loveSpot.numberOfPhotos > 0) {
            val photos = loveSpotPhotoService.getPhotosForLoveSpot(loveSpotId)
            loveSpotDetailsImagesRV.adapter =
                PhotoRecyclerAdapter(this@LoveSpotDetailsActivity, loveSpot, photos)
            loveSpotDetailsImagesRV.invalidate()
            detailsNoPhotoViewGroup.visibility = View.GONE
            loveSpotDetailsImagesRV.visibility = View.VISIBLE
        } else {
            detailsNoPhotoViewGroup.visibility = View.VISIBLE
            loveSpotDetailsImagesRV.visibility = View.GONE
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
                val loadingBarShower = LoadingBarShower(this).show()
                MainScope().launch {
                    val love = loveService.getAnyLoveByLoveSpotId(loveSpotId)
                    love?.let {
                        val reviewedSpot = loveSpotReviewService.submitReview(
                            LoveSpotReviewRequest(
                                love.id,
                                appContext.userId,
                                loveSpotId,
                                findViewById<EditText>(R.id.addReviewText).text.toString(),
                                rating,
                                findViewById<Spinner>(R.id.spotRiskDropdown).selectedItemPosition + 1
                            )
                        )
                        reviewedSpot?.let {
                            loveSpotService.insertIntoDb(reviewedSpot)
                        }

                        appContext.toaster.showToast(R.string.love_spot_reviewed)
                        MapContext.shouldCloseFabs = true
                        onBackPressed()
                    } ?: run {
                        appContext.toaster.showToast(R.string.havent_made_love_on_this_spot_yet)
                        MapContext.shouldCloseFabs = true
                        onBackPressed()
                    }
                    loadingBarShower.onResponse()
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

    private fun setUploadButton() {
        spotDetailsUploadButton.setOnClickListener {
            MainScope().launch {
                if (PhotoUploadUtils.canUploadForSpot(loveSpotId)) {

                    val intent = ImagePicker.with(this@LoveSpotDetailsActivity)
                        .galleryOnly()
                        .setMultipleAllowed(true)
                        .createIntent()

                    launcher.launch(intent)
                }
            }
        }
    }

    private fun setDynamicTexts() {
        MainScope().launch {
            if (loveService.madeLoveAlready(loveSpotId)) {
                detailsReviewLoveSpotFragment?.view?.visibility = View.VISIBLE
                detailsReviewButtons.visibility = View.VISIBLE
                haveNotMadeLoveReviewText.visibility = View.GONE
                haveNotMadeLoveText.visibility = View.GONE
            } else {
                detailsReviewLoveSpotFragment?.view?.visibility = View.GONE
                detailsReviewButtons.visibility = View.GONE
                haveNotMadeLoveReviewText.visibility = View.VISIBLE
                haveNotMadeLoveText.visibility = View.VISIBLE
            }
        }
    }

    private fun ratingValid() = rating != 0
}