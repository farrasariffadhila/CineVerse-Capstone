package com.example.capstone.favorite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.capstone.favorite.databinding.ActivityFavoriteBinding
import com.example.capstone.favorite.di.favoriteModule
import com.example.capstone.presentation.detail.DetailActivity
import com.example.capstone.presentation.home.MovieAdapter
import com.example.capstone.presentation.model.MovieUiModel
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModel()
    private lateinit var movieAdapter: MovieAdapter

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(favoriteModule)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        movieAdapter = MovieAdapter { movie ->
            openDetail(movie)
        }

        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(this@FavoriteActivity, 2)
            adapter = movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteMovies.collect { favorites ->
                    movieAdapter.submitList(favorites)
                    if (favorites.isEmpty()) {
                        binding.layoutEmptyFavorite.visibility = View.VISIBLE
                        binding.rvFavorites.visibility = View.GONE
                    } else {
                        binding.layoutEmptyFavorite.visibility = View.GONE
                        binding.rvFavorites.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun openDetail(movie: MovieUiModel) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.id)
            putExtra(DetailActivity.EXTRA_MOVIE, movie)
        }
        startActivity(intent)
    }
}
