package com.lovemap.lovemapandroid.ui.main.lovespot.widget

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.ListOrdering
import com.lovemap.lovemapandroid.ui.events.LoveSpotWidgetMoreClicked
import org.greenrobot.eventbus.EventBus

class LoveSpotWidgetFragment : Fragment() {
    private lateinit var widgetType: WidgetType

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_love_spot_widget, container, false)
        val loveSpotWidgetTitle: TextView = view.findViewById(R.id.loveSpotWidgetTitle)
        loveSpotWidgetTitle.text = when (widgetType) {
            WidgetType.POPULAR -> getString(R.string.popular_love_spots)
            WidgetType.TOP_RATED -> getString(R.string.top_rated_love_spots)
            WidgetType.RECENTLY_ACTIVE -> getString(R.string.recently_active_love_spots)
            WidgetType.CLOSEST -> getString(R.string.closest_love_spots)
        }
        val loveSpotWidgetMoreButton: Button = view.findViewById(R.id.loveSpotWidgetMoreButton)
        loveSpotWidgetMoreButton.setOnClickListener {
            EventBus.getDefault().post(LoveSpotWidgetMoreClicked(widgetType.toListOrdering()))
        }
        return view
    }

    enum class WidgetType {
        POPULAR, TOP_RATED, RECENTLY_ACTIVE, CLOSEST;

        fun toListOrdering(): ListOrdering {
            return when (this) {
                POPULAR -> ListOrdering.POPULAR
                TOP_RATED -> ListOrdering.TOP_RATED
                RECENTLY_ACTIVE -> ListOrdering.RECENTLY_ACTIVE
                CLOSEST -> ListOrdering.CLOSEST
            }
        }
    }
}