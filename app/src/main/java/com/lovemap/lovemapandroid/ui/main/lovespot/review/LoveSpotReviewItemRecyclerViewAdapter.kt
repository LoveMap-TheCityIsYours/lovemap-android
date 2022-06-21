package com.lovemap.lovemapandroid.ui.main.lovespot.review

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lovemap.lovemapandroid.databinding.FragmentReviewItemBinding
import com.lovemap.lovemapandroid.ui.data.ReviewHolder
import com.lovemap.lovemapandroid.ui.utils.LoveSpotUtils

class LoveSpotReviewItemRecyclerViewAdapter(
    private val values: MutableList<ReviewHolder>
) : RecyclerView.Adapter<LoveSpotReviewItemRecyclerViewAdapter.ViewHolder>() {

    fun updateData(data: List<ReviewHolder>) {
        values.clear()
        values.addAll(data)
    }

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
        LoveSpotUtils.setRating(
            review.reviewStars.toDouble(),
            holder.rating
        )
        LoveSpotUtils.setRisk(
            review.riskLevel.toDouble(),
            holder.spotReviewRisk
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rating: RatingBar = binding.spotReviewRating
        val spotReviewRisk: TextView = binding.spotReviewRisk
        val spotReviewText: TextView = binding.spotReviewText
    }

}