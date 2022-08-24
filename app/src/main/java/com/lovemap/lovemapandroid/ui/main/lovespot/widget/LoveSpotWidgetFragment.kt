package com.lovemap.lovemapandroid.ui.main.lovespot.widget

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.data.lovespot.LoveSpot
import com.lovemap.lovemapandroid.ui.events.LoveSpotWidgetMoreClicked
import com.lovemap.lovemapandroid.ui.events.RecommendationsUpdated
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoveSpotWidgetFragment : Fragment() {
    private lateinit var widgetType: WidgetType
    private lateinit var loveSpotWidgetItem1: LoveSpotWidgetItemFragment
    private lateinit var loveSpotWidgetItem2: LoveSpotWidgetItemFragment
    private lateinit var loveSpotWidgetProgressBar: ProgressBar
    private lateinit var loveSpotWidgetNoResultsText: TextView

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val attributes = requireActivity().obtainStyledAttributes(attrs, R.styleable.LoveSpotWidget)
        val widgetTypeAttribute: String =
            attributes.getString(R.styleable.LoveSpotWidget_spot_widget_type) ?: "TOP_RATED"
        widgetType = WidgetType.valueOf(widgetTypeAttribute)
        attributes.recycle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_love_spot_widget, container, false)
        initTexts(view)
        initMoreButton(view)
        hideItemsAndShowProgressBar(view)
        return view
    }

    private fun initTexts(view: View) {
        val loveSpotWidgetTitle: TextView = view.findViewById(R.id.loveSpotWidgetTitle)
        loveSpotWidgetTitle.text = when (widgetType) {
            WidgetType.POPULAR -> getString(R.string.popular_love_spots)
            WidgetType.TOP_RATED -> getString(R.string.top_rated_love_spots)
            WidgetType.RECENTLY_ACTIVE -> getString(R.string.recently_active_love_spots)
            WidgetType.CLOSEST -> getString(R.string.closest_love_spots)
            WidgetType.NEWEST -> getString(R.string.newest_love_spots)
        }
        loveSpotWidgetNoResultsText = view.findViewById(R.id.loveSpotWidgetNoResultsText)
    }

    private fun initMoreButton(view: View) {
        val loveSpotWidgetMoreButton: Button = view.findViewById(R.id.loveSpotWidgetMoreButton)
        loveSpotWidgetMoreButton.setOnClickListener {
            EventBus.getDefault().post(LoveSpotWidgetMoreClicked(widgetType.toListOrdering()))
        }
    }

    private fun hideItemsAndShowProgressBar(view: View) {
        loveSpotWidgetProgressBar = view.findViewById(R.id.loveSpotWidgetProgressBar)
        loveSpotWidgetItem1 =
            childFragmentManager.findFragmentById(R.id.loveSpotWidgetItem1) as LoveSpotWidgetItemFragment
        loveSpotWidgetItem2 =
            childFragmentManager.findFragmentById(R.id.loveSpotWidgetItem2) as LoveSpotWidgetItemFragment
        childFragmentManager
            .beginTransaction()
            .hide(loveSpotWidgetItem1)
            .hide(loveSpotWidgetItem2)
            .commit()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecommendationsUpdated(event: RecommendationsUpdated) {
        transitionOutItems()
        val loveSpots: List<LoveSpot> = getLoveSpots(event)
        showNewItems(loveSpots.size)
        loveSpotWidgetProgressBar.visibility = View.GONE


        if (loveSpots.isNotEmpty()) {
            loveSpotWidgetItem1.setLoveSpot(loveSpots[0])
            if (loveSpots.size > 1) {
                loveSpotWidgetItem2.setLoveSpot(loveSpots[1])
            }
        }
    }

    private fun getLoveSpots(event: RecommendationsUpdated) =
        when (widgetType) {
            WidgetType.POPULAR -> event.recommendations.popularSpots
            WidgetType.TOP_RATED -> event.recommendations.topRatedSpots
            WidgetType.RECENTLY_ACTIVE -> event.recommendations.recentlyActiveSpots
            WidgetType.CLOSEST -> event.recommendations.closestSpots
            WidgetType.NEWEST -> event.recommendations.newestSpots
        }

    private fun transitionOutItems() {
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .hide(loveSpotWidgetItem1)
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
            .hide(loveSpotWidgetItem2)
            .commit()
    }

    private fun showNewItems(loveSpotsSize: Int) {
        if (loveSpotsSize == 0) {
            loveSpotWidgetNoResultsText.visibility = View.VISIBLE
        } else {
            loveSpotWidgetNoResultsText.visibility = View.GONE
            childFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                .show(loveSpotWidgetItem1)
                .commit()
            if (loveSpotsSize > 1) {
                childFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    .show(loveSpotWidgetItem2)
                    .commit()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    enum class WidgetType {
        POPULAR, TOP_RATED, RECENTLY_ACTIVE, CLOSEST, NEWEST;

        fun toListOrdering(): ListOrdering {
            return when (this) {
                POPULAR -> ListOrdering.POPULAR
                TOP_RATED -> ListOrdering.TOP_RATED
                RECENTLY_ACTIVE -> ListOrdering.RECENTLY_ACTIVE
                CLOSEST -> ListOrdering.CLOSEST
                NEWEST -> ListOrdering.NEWEST
            }
        }
    }
}