package com.lovemap.lovemapandroid.ui.main.love

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import kotlinx.coroutines.runBlocking

class AddLoveFragment : Fragment() {

    private val appContext = AppContext.INSTANCE

    private lateinit var spotRiskDropdown: Spinner
    private lateinit var spotReviewRating: RatingBar
    private lateinit var privateNote: EditText
    private lateinit var reviewText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_love, container, false)
        initViews(view)
        setRiskDropdown()
        return view
    }

    private fun initViews(view: View) {
        spotRiskDropdown = view.findViewById(R.id.spotRiskDropdown)
        spotReviewRating = view.findViewById(R.id.spotReviewRating)
        privateNote = view.findViewById(R.id.addPrivateNote)
        reviewText = view.findViewById(R.id.addReviewText)
    }

    private fun setRiskDropdown() {
        spotRiskDropdown.setSelection(1)
        // TODO: i18n
        runBlocking {
            if (appContext.metadataStore.isRisksStored()) {
                val risks = appContext.metadataStore.getRisks()
                spotRiskDropdown.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    risks.riskList.map { it.nameEN }.toTypedArray()
                )
                spotRiskDropdown.setSelection(1)
            }
        }
    }
}