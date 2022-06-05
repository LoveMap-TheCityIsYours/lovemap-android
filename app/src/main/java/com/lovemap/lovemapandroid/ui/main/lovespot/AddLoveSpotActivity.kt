package com.lovemap.lovemapandroid.ui.main.lovespot

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.love.CreateLoveRequest
import com.lovemap.lovemapandroid.api.lovespot.Availability
import com.lovemap.lovemapandroid.api.lovespot.Availability.ALL_DAY
import com.lovemap.lovemapandroid.api.lovespot.Availability.NIGHT_ONLY
import com.lovemap.lovemapandroid.api.lovespot.CreateLoveSpotRequest
import com.lovemap.lovemapandroid.api.lovespot.UpdateLoveSpotRequest
import com.lovemap.lovemapandroid.api.lovespot.review.LoveSpotReviewRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.love.Love
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.databinding.ActivityAddLoveSpotBinding
import com.lovemap.lovemapandroid.service.LoveService
import com.lovemap.lovemapandroid.service.LoveSpotReviewService
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveActivity
import com.lovemap.lovemapandroid.ui.main.love.RecordLoveFragment
import com.lovemap.lovemapandroid.ui.main.lovespot.review.ReviewLoveSpotFragment
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import com.lovemap.lovemapandroid.utils.toApiString
import com.lovemap.lovemapandroid.utils.toFormattedString
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class AddLoveSpotActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveService = appContext.loveService
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReviewService = appContext.loveSpotReviewService

    private lateinit var binding: ActivityAddLoveSpotBinding
    private lateinit var addSpotTitle: TextView
    private lateinit var addSpotName: EditText
    private lateinit var addSpotDescription: EditText
    private lateinit var addSpotSubmit: Button
    private lateinit var addSpotCancel: Button
    private lateinit var madeLoveCheckBox: CheckBox
    private lateinit var reviewLoveSpotFragment: ReviewLoveSpotFragment
    private lateinit var recordLoveFragment: RecordLoveFragment
    private lateinit var addSpotSeparator1: TextView
    private lateinit var addSpotSeparator2: TextView
    private lateinit var addSpotAvailability: Spinner

    private var availability = ALL_DAY
    private var name = ""
    private var description = ""
    private var rating = 0

    private var editMode: Boolean = false
    private var editedLoveSpotId: Long = 0
    private var editedLoveSpot: LoveSpot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editedLoveSpotId = intent.getLongExtra(RecordLoveActivity.EDIT, 0)
        editMode = editedLoveSpotId != 0L
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
        addSpotTitle = binding.addSpotTitle
        addSpotCancel = binding.addSpotCancel
        addSpotName = binding.addSpotName
        addSpotDescription = binding.addSpotDescription
        addSpotSubmit = binding.addSpotSubmit
        madeLoveCheckBox = binding.madeLoveCheckBox
        addSpotSeparator1 = binding.addSpotSeparator1
        addSpotSeparator2 = binding.addSpotSeparator2
        addSpotAvailability = binding.addSpotAvailability
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

        if (editMode) {
            initViewsIfEditMode()
        } else {
            restoreSavedLoveSpot()
        }
    }

    private fun initViewsIfEditMode() {
        addSpotSubmit.isEnabled = true
        madeLoveCheckBox.isEnabled = false
        addSpotTitle.text = getString(R.string.editLoveSpotTitle)
        MainScope().launch {
            editedLoveSpot = loveSpotService.findLocally(editedLoveSpotId)
            editedLoveSpot?.let {
                name = it.name
                addSpotName.setText(it.name)
                description = it.description
                addSpotDescription.setText(it.description)
                availability = it.availability
                addSpotAvailability.setSelection(availabilityToPosition(it.availability))
            }
        }
    }

    private fun restoreSavedLoveSpot() {
        loveSpotService.savedCreationState?.let {
            addSpotName.setText(it.name)
            addSpotDescription.setText(it.description)
            addSpotAvailability.setSelection(availabilityToPosition(it.availability), true)
        }
        loveSpotService.savedCreationState = null
        loveService.savedCreationState?.let {
            madeLoveCheckBox.isChecked = true
            showFragments()
        }
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
            .runOnCommit { restoreSavedLoveAndReview() }
            .commit()
        addSpotSeparator1.visibility = View.VISIBLE
        addSpotSeparator2.visibility = View.VISIBLE
    }

    private fun restoreSavedLoveAndReview() {
        loveService.savedCreationState?.let {
            recordLoveFragment.addPrivateNote.text = it.note
            recordLoveFragment.recordLoveSelectPartnerDropdown.setSelection(
                it.partnerSelection,
                true
            )
            recordLoveFragment.selectedTime = it.happenedAt
            recordLoveFragment.recordLoveHappenedAt.text = it.happenedAt.toFormattedString()
        }
        loveService.savedCreationState = null

        loveSpotReviewService.savedCreationState?.let {
            reviewLoveSpotFragment.reviewText.setText(it.reviewText)
            reviewLoveSpotFragment.spotReviewRating.rating = it.rating
            reviewLoveSpotFragment.spotRiskDropdown.setSelection(it.riskSelection)
        }
        loveSpotReviewService.savedCreationState = null
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
            MainScope().launch {
                if (!editMode) {
                    createSpotAndOthers()
                } else {
                    updateLoveSpot()
                }
            }
        }
    }

    private suspend fun createSpotAndOthers() {
        if (addSpotSubmit.isEnabled) {
            val loadingBarShower = LoadingBarShower(this).show()
            backupLoveAndReview()
            val loveSpot = createLoveSpot()
            if (loveSpot != null) {
                if (madeLoveCheckBox.isChecked) {
                    val love = createLove(loveSpot)
                    submitReview(love, loveSpot)
                    goBack(loveSpot, loadingBarShower)
                }
            } else {
                loadingBarShower.onResponse()
            }
        }
    }

    private suspend fun updateLoveSpot() {
        editedLoveSpot?.let {
            val loadingBarShower = LoadingBarShower(this).show()
            val loveSpot = loveSpotService.update(
                it.id,
                UpdateLoveSpotRequest(
                    name = name,
                    description = description,
                    availability = availability
                )
            )
            if (loveSpot != null) {
                goBack(loveSpot, loadingBarShower)
            } else {
                loadingBarShower.onResponse()
            }
        }
    }

    private suspend fun createLoveSpot(): LoveSpot? {
        return loveSpotService.create(
            CreateLoveSpotRequest(
                name,
                appContext.mapCameraTarget.longitude,
                appContext.mapCameraTarget.latitude,
                description,
                null,
                availability
            )
        )
    }

    private suspend fun createLove(loveSpot: LoveSpot): Love? {
        val loveRequest = CreateLoveRequest(
            name,
            loveSpot.id,
            appContext.userId,
            recordLoveFragment.selectedTime.toApiString(),
            recordLoveFragment.selectedPartner(),
            recordLoveFragment.addPrivateNote.text.toString()
        )
        return loveService.create(
            loveRequest
        )
    }

    private suspend fun submitReview(
        love: Love?,
        loveSpot: LoveSpot
    ) {
        love?.let {
            val reviewText =
                findViewById<EditText>(R.id.addReviewText).text.toString()
            val riskLevel =
                findViewById<Spinner>(R.id.spotRiskDropdown).selectedItemPosition + 1
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
                loveSpotService.insertIntoDb(reviewedSpot)
            }
        }
    }

    private fun backupLoveAndReview() {
        if (madeLoveCheckBox.isChecked) {
            loveService.savedCreationState = LoveService.SavedCreationState(
                name,
                recordLoveFragment.recordLoveSelectPartnerDropdown.selectedItemPosition,
                recordLoveFragment.selectedTime
            )
            loveSpotReviewService.savedCreationState = LoveSpotReviewService.SavedCreationState(
                reviewLoveSpotFragment.reviewText.text.toString(),
                reviewLoveSpotFragment.spotReviewRating.rating,
                reviewLoveSpotFragment.spotRiskDropdown.selectedItemPosition
            )
        }
    }

    private fun goBack(loveSpot: LoveSpot, loadingBarShower: LoadingBarShower) {
        appContext.toaster.showToast(R.string.love_spot_added)
        appContext.shouldCloseFabs = true
        appContext.zoomOnLoveSpot = loveSpot
        loadingBarShower.onResponse()
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
                    availability = positionToAvailability(adapterView?.selectedItemPosition ?: 0)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }

    private fun positionToAvailability(position: Int) =
        if (position == 0) {
            ALL_DAY
        } else {
            NIGHT_ONLY
        }

    private fun availabilityToPosition(availability: Availability): Int {
        return when (availability) {
            ALL_DAY -> 0
            NIGHT_ONLY -> 1
        }
    }

    private fun isSubmitReady(): Boolean {
        return descriptionValid() && nameValid() &&
                ((madeLoveCheckBox.isChecked && ratingValid()) || !madeLoveCheckBox.isChecked)
    }

    private fun descriptionValid() = addSpotDescription.text.toString().length >= 3
    private fun nameValid() = addSpotName.text.toString().length >= 3
    private fun ratingValid() = rating != 0

    companion object {
        const val EDIT = "edit"
    }
}