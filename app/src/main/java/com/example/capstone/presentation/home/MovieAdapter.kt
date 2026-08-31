package com.example.capstone.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.capstone.core.R
import com.example.capstone.core.databinding.ItemMovieCardBinding
import com.example.capstone.presentation.model.MovieUiModel

class MovieAdapter(
    private val onItemClick: (MovieUiModel) -> Unit
) : ListAdapter<MovieUiModel, MovieAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class MovieViewHolder(private val binding: ItemMovieCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieUiModel) {
            binding.tvTitle.text = movie.title
            binding.tvReleaseYear.text = movie.releaseYear
            binding.tvRating.text = movie.formattedRating
            binding.tvReviews.text = movie.voteCountText

            if (movie.isFavorite) {
                binding.ivFavIndicator.visibility = View.VISIBLE
            } else {
                binding.ivFavIndicator.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(movie.posterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.bg_shimmer_card)
                .error(R.drawable.bg_shimmer_card)
                .into(binding.ivPoster)

            itemView.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieUiModel>() {
            override fun areItemsTheSame(oldItem: MovieUiModel, newItem: MovieUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieUiModel, newItem: MovieUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
