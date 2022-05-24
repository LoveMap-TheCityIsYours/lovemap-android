package com.lovemap.lovemapandroid.ui.main.lovespot.review

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.FragmentReviewItemBinding

import com.lovemap.lovemapandroid.ui.data.ReviewHolder
import com.lovemap.lovemapandroid.ui.utils.LoveSpotDetailsUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoveSpotReviewItemRecyclerViewAdapter(
    private val values: List<ReviewHolder>
) : RecyclerView.Adapter<LoveSpotReviewItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentReviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = values[position]
        holder.spotReviewText.text = review.reviewText
        LoveSpotDetailsUtils.setRating(
            review.reviewStars.toDouble(),
            holder.rating
        )
        MainScope().launch {
            LoveSpotDetailsUtils.setRisk(
                review.riskLevel.toDouble(),
                AppContext.INSTANCE.loveSpotService,
                AppContext.INSTANCE.applicationContext,
                holder.spotReviewRisk
            )
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rating: RatingBar = binding.spotReviewRating
        val spotReviewRisk: TextView = binding.spotReviewRisk
        val spotReviewText: TextView = binding.spotReviewText
    }

}