package com.lovemap.lovemapandroid.ui.main.lovespot

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lovemap.lovemapandroid.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoveSpotWidgetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoveSpotWidgetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var widgetType: WidgetType


    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val attributes = requireActivity().obtainStyledAttributes(attrs, R.styleable.LoveSpotWidget)
        val widgetTypeAttribute: String = attributes.getString(R.styleable.LoveSpotWidget_spot_widget_type) ?: "BEST"
        widgetType = WidgetType.valueOf(widgetTypeAttribute)
        attributes.recycle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_love_spot_widget, container, false)
        val loveSpotWidgetTitle: TextView = view.findViewById(R.id.loveSpotWidgetTitle)
        loveSpotWidgetTitle.text = when (widgetType) {
            WidgetType.BEST -> getString(R.string.best_love_spots_nearby)
            WidgetType.RECENTLY_ACTIVE -> getString(R.string.hot_love_spots_nearby)
            WidgetType.CLOSEST -> getString(R.string.closest_love_spots_nearby)
        }
        return view
    }

    enum class WidgetType {
        BEST, RECENTLY_ACTIVE, CLOSEST
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoveSpotWidgetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoveSpotWidgetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}