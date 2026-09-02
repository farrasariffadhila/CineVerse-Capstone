package com.example.capstone.presentation.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.capstone.R
import com.example.capstone.core.utils.Resource
import com.example.capstone.databinding.ActivityDetailBinding
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()
    private var currentMovie: MovieUiModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0)
        val passedMovie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_MOVIE, MovieUiModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_MOVIE) as? MovieUiModel
        }

        if (passedMovie != null) {
            currentMovie = passedMovie
            populateMovieData(passedMovie)
        }

        if (movieId != 0) {
            viewModel.setMovieId(movieId)
        } else if (passedMovie != null) {
            viewModel.setMovieId(passedMovie.id)
        }

        setupButtons()
        observeData()
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnShare.setOnClickListener {
            currentMovie?.let { movie ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, movie.title)
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this movie on CineVerse!\n\n🎬 ${movie.title} (${movie.releaseYear})\n⭐ Rating: ${movie.formattedRating}/10\n\n${movie.overview}"
                    )
                }
                startActivity(Intent.createChooser(shareIntent, "Share Movie via"))
            }
        }

        binding.fabFavorite.setOnClickListener {
            currentMovie?.let { movie ->
                animateFab()
                viewModel.toggleFavorite(movie)
            } ?: run {
                Toast.makeText(this, "Movie data is still loading...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.movieDetail.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let { movie ->
                                    currentMovie = movie
                                    populateMovieData(movie)
                                }
                            }
                            is Resource.Loading -> {
                                resource.data?.let { movie ->
                                    currentMovie = movie
                                    populateMovieData(movie)
                                }
                            }
                            is Resource.Error -> {
                                resource.data?.let { movie ->
                                    currentMovie = movie
                                    populateMovieData(movie)
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.isFavorite.collect { isFav ->
                        updateFavoriteState(isFav)
                    }
                }
            }
        }
    }

    private fun populateMovieData(movie: MovieUiModel) {
        binding.tvDetailTitle.text = movie.title
        binding.tvDetailReleaseDate.text = movie.formattedReleaseDate
        binding.tvDetailRating.text = movie.formattedRating
        binding.tvDetailReviews.text = movie.voteCountText
        binding.tvDetailOverview.text = if (movie.overview.isNotEmpty()) {
            movie.overview
        } else {
            getString(R.string.overview_unavailable)
        }

        Glide.with(this)
            .load(movie.backdropUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(com.example.capstone.core.R.drawable.bg_shimmer_card)
            .into(binding.ivDetailBackdrop)

        Glide.with(this)
            .load(movie.posterUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(com.example.capstone.core.R.drawable.bg_shimmer_card)
            .into(binding.ivDetailPoster)
    }

    private fun updateFavoriteState(isFav: Boolean) {
        if (isFav) {
            binding.fabFavorite.setImageResource(com.example.capstone.core.R.drawable.ic_favorite_filled)
            binding.fabFavorite.imageTintList = ContextCompat.getColorStateList(this, com.example.capstone.core.R.color.white)
            binding.tvOfflineStatus.text = getString(R.string.status_saved_offline)
            binding.tvOfflineStatus.setTextColor(ContextCompat.getColor(this, com.example.capstone.core.R.color.accent_cyan))
        } else {
            binding.fabFavorite.setImageResource(com.example.capstone.core.R.drawable.ic_favorite)
            binding.fabFavorite.imageTintList = ContextCompat.getColorStateList(this, com.example.capstone.core.R.color.white)
            binding.tvOfflineStatus.text = getString(R.string.status_not_favorited)
            binding.tvOfflineStatus.setTextColor(ContextCompat.getColor(this, com.example.capstone.core.R.color.text_muted))
        }
    }

    private fun animateFab() {
        binding.fabFavorite.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(150)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                binding.fabFavorite.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_MOVIE = "extra_movie"
    }
}
