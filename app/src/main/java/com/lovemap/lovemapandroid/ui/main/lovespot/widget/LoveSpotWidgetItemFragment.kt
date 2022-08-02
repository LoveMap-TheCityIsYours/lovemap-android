package com.lovemap.lovemapandroid.ui.main.lovespot.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils

class LoveSpotWidgetItemFragment : Fragment() {

    private lateinit var spotWidgetItemName: TextView
    private lateinit var spotWidgetItemType: TextView
    private lateinit var spotWidgetItemAvailability: TextView
    private lateinit var spotWidgetItemRisk: TextView
    private lateinit var spotWidgetItemDistance: TextView
    private lateinit var spotWidgetItemRating: RatingBar
    private lateinit var spotWidgetItemTypeImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_love_spot_widget_item, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        spotWidgetItemName = view.findViewById(R.id.spotWidgetItemName)
        spotWidgetItemType = view.findViewById(R.id.spotWidgetItemType)
        spotWidgetItemAvailability = view.findViewById(R.id.spotWidgetItemAvailability)
        spotWidgetItemRisk = view.findViewById(R.id.spotWidgetItemRisk)
        spotWidgetItemDistance = view.findViewById(R.id.spotWidgetItemDistance)
        spotWidgetItemRating = view.findViewById(R.id.spotWidgetItemRating)
        spotWidgetItemTypeImage = view.findViewById(R.id.spotWidgetItemTypeImage)
    }

    fun setLoveSpot(loveSpot: LoveSpot) {
        spotWidgetItemName.text = loveSpot.name
        LoveSpotUtils.setType(loveSpot.type, spotWidgetItemType)
        LoveSpotUtils.setAvailability(loveSpot.availability, spotWidgetItemAvailability)
        LoveSpotUtils.setRisk(loveSpot.averageDanger, spotWidgetItemRisk)
        LoveSpotUtils.setDistance(loveSpot, spotWidgetItemDistance)
        LoveSpotUtils.setRating(loveSpot.averageRating, spotWidgetItemRating)
        LoveSpotUtils.setTypeImage(loveSpot.type, spotWidgetItemTypeImage)
    }
}