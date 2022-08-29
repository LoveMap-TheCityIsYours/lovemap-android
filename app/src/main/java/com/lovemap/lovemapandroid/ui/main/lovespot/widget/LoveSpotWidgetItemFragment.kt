package com.lovemap.lovemapandroid.ui.main.lovespot.widget

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.main.lovespot.LoveSpotDetailsActivity
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils

class LoveSpotWidgetItemFragment : Fragment() {

    private val appContext = AppContext.INSTANCE
    private var loveSpot: LoveSpot? = null
    private val contextMenuIds = LoveSpotUtils.ContextMenuIds()

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
        registerForContextMenu(view)
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
        view.setOnClickListener {
            if (loveSpot != null) {
                appContext.selectedLoveSpot = loveSpot
                appContext.selectedLoveSpotId = loveSpot!!.id
                startActivity(Intent(requireContext(), LoveSpotDetailsActivity::class.java))
            }
        }
    }

    fun setLoveSpot(loveSpot: LoveSpot) {
        this.loveSpot = loveSpot
        spotWidgetItemName.text = loveSpot.name
        LoveSpotUtils.setType(loveSpot.type, spotWidgetItemType)
        LoveSpotUtils.setAvailability(loveSpot.availability, spotWidgetItemAvailability)
        LoveSpotUtils.setRisk(loveSpot.averageDanger, spotWidgetItemRisk)
        LoveSpotUtils.setDistance(loveSpot, spotWidgetItemDistance)
        LoveSpotUtils.setRating(loveSpot.averageRating, spotWidgetItemRating)
        LoveSpotUtils.setTypeImage(loveSpot.type, spotWidgetItemTypeImage)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        loveSpot?.let {
            val spotId = it.id
            LoveSpotUtils.onContextItemSelected(item, contextMenuIds, spotId, requireContext())
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        loveSpot?.let {
            LoveSpotUtils.onCreateContextMenu(menu, contextMenuIds, it.name, it.addedBy)
        }
        super.onCreateContextMenu(menu, v, menuInfo)
    }
}