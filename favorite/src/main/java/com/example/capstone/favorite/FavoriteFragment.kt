package com.example.capstone.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.capstone.favorite.databinding.FragmentFavoriteBinding
import com.example.capstone.favorite.di.favoriteModule
import com.example.capstone.presentation.detail.DetailActivity
import com.example.capstone.presentation.home.MovieAdapter
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModel()
    private var _movieAdapter: MovieAdapter? = null
    private val movieAdapter get() = _movieAdapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(favoriteModule)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        _movieAdapter = MovieAdapter { movie ->
            openDetail(movie)
        }
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = _movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteMovies.collect { favorites ->
                    if (_binding == null) return@collect
                    _movieAdapter?.submitList(favorites)
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
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.id)
            putExtra(DetailActivity.EXTRA_MOVIE, movie)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFavorites.adapter = null
        _movieAdapter = null
        _binding = null
    }
}
