package com.lovemap.lovemapandroid.ui.main.pages.newsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.ui.main.newsfeed.NewsFeedFragment
import com.lovemap.lovemapandroid.ui.utils.InfoPopupShower

class NewsFeedPageFragment : Fragment() {
    companion object {
        private const val TAG = "NewsFeedPageFragment"
    }

    private lateinit var newsFeedPersonalToggle: SwitchCompat
    private lateinit var newsFeedInfoButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_news_feed_page, container, false) as LinearLayout
        view.layoutTransition.setAnimateParentHierarchy(false)

        newsFeedInfoButton = view.findViewById(R.id.newsFeedInfoButton)
        newsFeedPersonalToggle = view.findViewById(R.id.newsFeedPersonalToggle)
        newsFeedInfoButton.setOnClickListener {
            val infoPopupShower = InfoPopupShower(R.string.personal_news_feed_info)
            infoPopupShower.show(view)
        }

        val newsFeedFragment =
            NewsFeedFragment.newInstance(NewsFeedFragment.NewsFeedType.GLOBAL, null)
        childFragmentManager.beginTransaction()
            .add(R.id.newsFeedFragmentContainer, newsFeedFragment)
            .commitAllowingStateLoss()

        setPersonalFeedToggleChangeListener()

        return view
    }

    private fun setPersonalFeedToggleChangeListener() {
        newsFeedPersonalToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            val newsFeedFragment = if (isChecked) {
                NewsFeedFragment.newInstance(NewsFeedFragment.NewsFeedType.FOLLOWING, null)
            } else {
                NewsFeedFragment.newInstance(NewsFeedFragment.NewsFeedType.GLOBAL, null)
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.newsFeedFragmentContainer, newsFeedFragment)
                .setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                .commitAllowingStateLoss()
        }
    }
}
