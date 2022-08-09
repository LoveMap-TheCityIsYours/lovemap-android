package com.lovemap.lovemapandroid.ui.main.lovespot.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReviewLoveSpotFragment : Fragment() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotReviewService = appContext.loveSpotReviewService

    lateinit var spotRiskDropdown: Spinner
    lateinit var spotReviewRating: RatingBar
    lateinit var reviewText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_review_love_spot, container, false)
        initViews(view)
        spotRiskDropdown.setSelection(1)
        return view
    }

    override fun onResume() {
        super.onResume()
        setContent()
    }

    private fun setContent() {
        MainScope().launch {
            appContext.selectedLoveSpotId?.let {
                val review = loveSpotReviewService.findByLoverAndSpotId(it)
                review?.let {
                    reviewText.setText(review.reviewText)
                    spotRiskDropdown.setSelection(review.riskLevel - 1)
                    spotReviewRating.rating = review.reviewStars.toFloat()
                }
            }
        }
    }

    private fun initViews(view: View) {
        spotRiskDropdown = view.findViewById(R.id.spotRiskDropdown)
        spotReviewRating = view.findViewById(R.id.marker_review_rating_bar)
        reviewText = view.findViewById(R.id.addReviewText)
    }
}