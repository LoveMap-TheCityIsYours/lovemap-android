package com.lovemap.lovemapandroid.ui.main.love

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.api.smack.CreateSmackRequest
import com.lovemap.lovemapandroid.api.smackspot.review.SmackSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityAddLoveBinding
import com.lovemap.lovemapandroid.ui.main.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AddLoveActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val smackService = appContext.smackService
    private val smackSpotService = appContext.smackSpotService

    private lateinit var binding: ActivityAddLoveBinding
    private lateinit var addSmackSubmit: Button
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
        addSmackSubmit = binding.addSmackSubmit
        spotRiskDropdown = binding.spotRiskDropdown

        binding.addSmackCancel.setOnClickListener {
            onBackPressed()
        }

        binding.spotReviewRating.setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingValue.toInt()
            addSmackSubmit.isEnabled = true
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
            }
        }
    }

    private fun setSubmitButton() {
        addSmackSubmit.setOnClickListener {
            if (addSmackSubmit.isEnabled) {
                appContext.selectedMarker?.let {
                    val spotId = it.snippet!!.toLong()
                    MainScope().launch {
                        val smackSpot = smackSpotService.findLocally(spotId)!!
                        val smack = smackService.create(
                            CreateSmackRequest(
                                smackSpot.name,
                                spotId,
                                appContext.userId,
                                null,   // TODO: add partner
                                binding.addPrivateNote.text.toString()
                            )
                        )
                        smack?.let {
                            val reviewedSpot = smackSpotService.addReview(
                                SmackSpotReviewRequest(
                                    smack.id,
                                    appContext.userId,
                                    spotId,
                                    binding.addReviewText.text.toString(),
                                    rating,
                                    spotRiskDropdown.selectedItemPosition + 1
                                )
                            )
                            reviewedSpot?.let {
                                smackSpotService.update(reviewedSpot)
                            }
                        }
                        appContext.shouldMoveMapCamera = true
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        startActivity(intent)
                    }
                }
            }
        }
    }


}