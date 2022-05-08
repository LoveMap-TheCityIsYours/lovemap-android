package com.lovemap.lovemapandroid.ui.main.love

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityAddLoveBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddLoveActivity : AppCompatActivity() {
    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService

    private lateinit var binding: ActivityAddLoveBinding
    private lateinit var addLoveSubmit: Button

    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setSubmitButton()
    }

    private fun initViews() {
        binding = ActivityAddLoveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addLoveSubmit = binding.addLoveSubmit

        binding.addLoveCancel.setOnClickListener {
            onBackPressed()
        }

        findViewById<RatingBar>(R.id.spotReviewRating).setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingValue.toInt()
            addLoveSubmit.isEnabled = true
        }
    }

    private fun setSubmitButton() {
        addLoveSubmit.setOnClickListener {
            if (addLoveSubmit.isEnabled) {
                appContext.selectedMarker?.let {
                    val spotId = it.snippet!!.toLong()
                    MainScope().launch {
                        val loveSpot = loveSpotService.findLocally(spotId)!!
                        val love = loveService.create(
                            CreateLoveRequest(
                                loveSpot.name,
                                spotId,
                                appContext.userId,
                                null,   // TODO: add partner
                                findViewById<EditText>(R.id.addPrivateNote).text.toString()
                            )
                        )
                        love?.let {
                            val reviewedSpot = loveSpotService.addReview(
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
                        }
                        appContext.toaster.showToast(R.string.lovemaking_recorded)
                        appContext.shouldMoveMapCamera = true
                        onBackPressed()
                    }
                }
            }
        }
    }
}