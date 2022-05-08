package com.lovemap.lovemapandroid.ui.main.love

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityAddLoveBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AddLoveActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService

    private lateinit var binding: ActivityAddLoveBinding
    private lateinit var addLoveSubmit: Button
    private lateinit var spotRiskDropdown: Spinner

    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setRiskDropdown()
        setSubmitButton()
    }

    private fun initViews() {
        binding = ActivityAddLoveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addLoveSubmit = binding.addLoveSubmit
        spotRiskDropdown = binding.spotRiskDropdown

        binding.addLoveCancel.setOnClickListener {
            onBackPressed()
        }

        binding.spotReviewRating.setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingValue.toInt()
            addLoveSubmit.isEnabled = true
        }
    }

    private fun setRiskDropdown() {
        spotRiskDropdown.setSelection(1)
        // TODO: i18n
        runBlocking {
            if (appContext.metadataStore.isRisksStored()) {
                val risks = appContext.metadataStore.getRisks()
                spotRiskDropdown.adapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    risks.riskList.map { it.nameEN }.toTypedArray()
                )
                spotRiskDropdown.setSelection(1)
            }
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
                                binding.addPrivateNote.text.toString()
                            )
                        )
                        love?.let {
                            val reviewedSpot = loveSpotService.addReview(
                                LoveSpotReviewRequest(
                                    love.id,
                                    appContext.userId,
                                    spotId,
                                    binding.addReviewText.text.toString(),
                                    rating,
                                    spotRiskDropdown.selectedItemPosition + 1
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