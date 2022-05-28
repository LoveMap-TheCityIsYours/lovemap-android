package com.lovemap.lovemapandroid.ui.main.lovespot

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.lovespot.CreateLoveSpotRequest
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus.ALL_DAY
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus.NIGHT_ONLY
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.databinding.ActivityAddLoveSpotBinding
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewLoveSpotFragment
import com.lovemap.lovemapandroid.ui.utils.toApiString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class AddLoveSpotActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private lateinit var binding: ActivityAddLoveSpotBinding
    private lateinit var addSpotName: EditText
    private lateinit var addSpotDescription: EditText
    private lateinit var addSpotSubmit: Button
    private lateinit var addSpotCancel: Button
    private lateinit var madeLoveCheckBox: CheckBox
    private lateinit var reviewLoveSpotFragment: ReviewLoveSpotFragment
    private lateinit var recordLoveFragment: RecordLoveFragment
    private lateinit var addSpotSeparator1: TextView
    private lateinit var addSpotSeparator2: TextView

    private var availability = ALL_DAY
    private var name = ""
    private var description = ""
    private var rating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setMadeLoveCheckBox()
        setReviewRatingBar()
        setCancelButton()
        setSubmitButton()
        setNameText()
        setDescriptionText()
        setAvailabilitySpinner()
    }

    private fun initViews() {
        binding = ActivityAddLoveSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addSpotCancel = binding.addSpotCancel
        addSpotName = binding.addSpotName
        addSpotDescription = binding.addSpotDescription
        addSpotSubmit = binding.addSpotSubmit
        madeLoveCheckBox = binding.madeLoveCheckBox
        addSpotSeparator1 = binding.addSpotSeparator1
        addSpotSeparator2 = binding.addSpotSeparator2
        reviewLoveSpotFragment =
            supportFragmentManager.findFragmentById(R.id.addSpotReviewLoveSpotFragment) as ReviewLoveSpotFragment
        recordLoveFragment =
            supportFragmentManager.findFragmentById(R.id.addSpotRecordLoveFragment) as RecordLoveFragment

        addSpotSeparator1.visibility = View.GONE
        addSpotSeparator2.visibility = View.GONE
        supportFragmentManager
            .beginTransaction()
            .hide(recordLoveFragment)
            .hide(reviewLoveSpotFragment)
            .commit()
    }

    private fun setMadeLoveCheckBox() {
        madeLoveCheckBox.setOnClickListener {
            if (madeLoveCheckBox.isChecked) {
                addSpotSubmit.isEnabled = isSubmitReady()
                showFragments()
            } else {
                addSpotSubmit.isEnabled = isSubmitReady()
                hideFragments()
            }
        }
    }

    private fun showFragments() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .show(reviewLoveSpotFragment)
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .show(recordLoveFragment)
            .commit()
        addSpotSeparator1.visibility = View.VISIBLE
        addSpotSeparator2.visibility = View.VISIBLE
    }

    private fun hideFragments() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .hide(reviewLoveSpotFragment)
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .hide(recordLoveFragment)
            .commit()
        addSpotSeparator1.visibility = View.GONE
        addSpotSeparator2.visibility = View.GONE
    }

    private fun setReviewRatingBar() {
        findViewById<RatingBar>(R.id.marker_review_rating_bar)
            .setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
                rating = ratingValue.toInt()
                addSpotSubmit.isEnabled = isSubmitReady()
            }
    }

    private fun setCancelButton() {
        addSpotCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setSubmitButton() {
        addSpotSubmit.setOnClickListener {
            if (addSpotSubmit.isEnabled) {
                MainScope().launch {
                    val loveSpot = loveSpotService.create(
                        CreateLoveSpotRequest(
                            name,
                            appContext.mapCameraTarget.longitude,
                            appContext.mapCameraTarget.latitude,
                            description,
                            null,
                            availability
                        ), this@AddLoveSpotActivity
                    )
                    if (loveSpot != null) {
                        if (madeLoveCheckBox.isChecked) {
                            val reviewText =
                                findViewById<EditText>(R.id.addReviewText).text.toString()
                            val riskLevel =
                                findViewById<Spinner>(R.id.spotRiskDropdown).selectedItemPosition + 1
                            val loveRequest = CreateLoveRequest(
                                loveSpot.name,
                                loveSpot.id,
                                appContext.userId,
                                recordLoveFragment.selectedTime.toApiString(),
                                recordLoveFragment.selectedPartner(),
                                recordLoveFragment.addPrivateNote.text.toString()
                            )
                            returnHome(loveSpot)
                            val love = loveService.create(
                                loveRequest
                            )
                            love?.let {
                                val reviewedSpot = loveSpotReviewService.submitReview(
                                    LoveSpotReviewRequest(
                                        love.id,
                                        appContext.userId,
                                        loveSpot.id,
                                        reviewText,
                                        rating,
                                        riskLevel
                                    )
                                )
                                reviewedSpot?.let {
                                    loveSpotService.update(reviewedSpot)
                                }
                            }
                        } else {
                            returnHome(loveSpot)
                        }
                    }
                }
            }
        }
    }

    private fun returnHome(loveSpot: LoveSpot) {
        appContext.toaster.showToast(R.string.love_spot_added)
        appContext.shouldCloseFabs = true
        appContext.zoomOnLoveSpot = loveSpot
        onBackPressed()
    }

    private fun setNameText() {
        addSpotName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (nameValid()) {
                    name = addSpotName.text.toString()
                    addSpotSubmit.isEnabled = isSubmitReady()
                    addSpotName.error = null
                } else {
                    addSpotName.error = getString(R.string.invalid_spot_name)
                    addSpotSubmit.isEnabled = false
                }
            }
        })
    }

    private fun setDescriptionText() {
        addSpotDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (descriptionValid()) {
                    description = addSpotDescription.text.toString()
                    addSpotSubmit.isEnabled = isSubmitReady()
                    addSpotName.error = null
                } else {
                    addSpotDescription.error = getString(R.string.invalid_spot_description)
                    addSpotSubmit.isEnabled = false
                }
            }
        })
    }

    private fun setAvailabilitySpinner() {
        binding.addSpotAvailability.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    availability = if (adapterView?.selectedItemPosition == 0) {
                        ALL_DAY
                    } else {
                        NIGHT_ONLY
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }

    private fun isSubmitReady(): Boolean {
        return descriptionValid() && nameValid() &&
                ((madeLoveCheckBox.isChecked && ratingValid()) || !madeLoveCheckBox.isChecked)
    }

    private fun descriptionValid() = addSpotDescription.text.toString().length >= 3
    private fun nameValid() = addSpotName.text.toString().length >= 3
    private fun ratingValid() = rating != 0
}