package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.CreateLoveSpotRequest
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus.ALL_DAY
import com.lovemap.lovemapandroid.api.lovespot.LoveSpotAvailabilityApiStatus.NIGHT_ONLY
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityAddLoveSpotBinding
import com.lovemap.lovemapandroid.ui.main.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddLoveSpotActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService

    private lateinit var binding: ActivityAddLoveSpotBinding
    private lateinit var addSpotName: EditText
    private lateinit var addSpotDescription: EditText
    private var availability = ALL_DAY
    private var name = ""
    private var description = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLoveSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val addSpotCancel = binding.addSpotCancel
        addSpotName = binding.addSpotName
        addSpotDescription = binding.addSpotDescription
        val addSpotSubmit = binding.addSpotSubmit

        setCancelButton(addSpotCancel)
        setSubmitButton(addSpotSubmit)
        setNameText(addSpotSubmit)
        setDescriptionText(addSpotSubmit)
        setAvailabilitySpinner()
    }

    private fun setCancelButton(addSpotCancel: Button) {
        addSpotCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setSubmitButton(addSpotSubmit: Button) {
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
                        )
                    )
                    if (loveSpot != null) {
                        appContext.toaster.showToast(R.string.love_spot_added)
                        appContext.shouldCloseFabs = true
                        appContext.zoomOnNewLoveSpot = loveSpot
                        onBackPressed()
//                        val intent = Intent(applicationContext, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setNameText(addSpotSubmit: Button) {
        addSpotName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (nameValid()) {
                    name = addSpotName.text.toString()
                    if (descriptionValid()) {
                        addSpotSubmit.isEnabled = true
                    }
                } else {
                    addSpotName.error = getString(R.string.invalid_spot_name)
                    addSpotSubmit.isEnabled = false
                }
            }
        })
    }

    private fun setDescriptionText(addSpotSubmit: Button) {
        addSpotDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (descriptionValid()) {
                    description = addSpotDescription.text.toString()
                    if (nameValid()) {
                        addSpotSubmit.isEnabled = true
                    }
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

    private fun descriptionValid() = addSpotDescription.text.toString().length >= 3
    private fun nameValid() = addSpotName.text.toString().length >= 3

}