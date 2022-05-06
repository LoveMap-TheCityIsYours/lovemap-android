package com.smackmap.smackmapandroid.ui.main.smack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.smackmap.smackmapandroid.api.smack.CreateSmackRequest
import com.smackmap.smackmapandroid.api.smackspot.review.SmackSpotReviewRequest
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.databinding.ActivityAddSmackBinding
import com.smackmap.smackmapandroid.ui.main.MainActivity
import com.smackmap.smackmapandroid.ui.main.smackspot.AddSmackSpotActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddSmackActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val smackService = appContext.smackService
    private val smackSpotService = appContext.smackSpotService

    private lateinit var binding: ActivityAddSmackBinding
    private lateinit var addSmackSubmit: Button

    private var rating: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSmackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addSmackSubmit = binding.addSmackSubmit

        binding.spotRiskDropdown.setSelection(1)
        binding.spotReviewRating.setOnRatingBarChangeListener { ratingBar, ratingValue, _ ->
            rating = ratingBar.numStars
            addSmackSubmit.isEnabled = true
        }

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
                            smackSpotService.addReview(
                                SmackSpotReviewRequest(
                                    smack.id,
                                    appContext.userId,
                                    spotId,
                                    binding.addReviewText.text.toString(),
                                    rating
                                )   // TODO: add risk level to backend API
                            )
                        }

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        startActivity(intent)
                    }
                }
            }
        }
    }


}