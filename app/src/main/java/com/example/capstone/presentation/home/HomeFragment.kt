package com.example.capstone.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.core.utils.Resource
import com.example.capstone.databinding.FragmentHomeBinding
import com.example.capstone.presentation.detail.DetailActivity
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private var _movieAdapter: MovieAdapter? = null
    private val movieAdapter get() = _movieAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategoryChips()
        setupSwipeRefresh()
        observeData()
    }

    private fun setupRecyclerView() {
        _movieAdapter = MovieAdapter { movie ->
            openDetail(movie)
        }
        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = _movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupCategoryChips() {
        binding.chipPopular.setOnClickListener {
            viewModel.selectCategory("popular")
            updateChipStyles("popular")
        }

        binding.chipNowPlaying.setOnClickListener {
            viewModel.selectCategory("now_playing")
            updateChipStyles("now_playing")
        }

        binding.chipTopRated.setOnClickListener {
            viewModel.selectCategory("top_rated")
            updateChipStyles("top_rated")
        }
    }

    private fun updateChipStyles(selectedCategory: String) {
        val selectedBg = ContextCompat.getDrawable(requireContext(), com.example.capstone.core.R.drawable.bg_chip_selected)
        val unselectedBg = ContextCompat.getDrawable(requireContext(), com.example.capstone.core.R.drawable.bg_chip_unselected)
        val selectedColor = ContextCompat.getColor(requireContext(), com.example.capstone.core.R.color.white)
        val unselectedColor = ContextCompat.getColor(requireContext(), com.example.capstone.core.R.color.text_secondary)

        binding.chipPopular.background = if (selectedCategory == "popular") selectedBg else unselectedBg
        binding.chipPopular.setTextColor(if (selectedCategory == "popular") selectedColor else unselectedColor)

        binding.chipNowPlaying.background = if (selectedCategory == "now_playing") selectedBg else unselectedBg
        binding.chipNowPlaying.setTextColor(if (selectedCategory == "now_playing") selectedColor else unselectedColor)

        binding.chipTopRated.background = if (selectedCategory == "top_rated") selectedBg else unselectedBg
        binding.chipTopRated.setTextColor(if (selectedCategory == "top_rated") selectedColor else unselectedColor)

        binding.tvSectionTitle.text = when (selectedCategory) {
            "now_playing" -> "Now Playing in Theaters"
            "top_rated" -> "All-Time Top Rated"
            else -> "Popular Titles"
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(com.example.capstone.core.R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            val currentCategory = viewModel.selectedCategory.value
            viewModel.selectCategory(currentCategory)
            binding.swipeRefresh.isRefreshing = false
        }

        binding.viewError.btnRetry.setOnClickListener {
            val currentCategory = viewModel.selectedCategory.value
            viewModel.selectCategory(currentCategory)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            val data = resource.data
                            if (data.isNullOrEmpty()) {
                                showLoading(true)
                                showError(false)
                                showEmpty(false)
                            } else {
                                showLoading(false)
                                populateData(data)
                            }
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            showError(false)
                            val data = resource.data
                            if (data.isNullOrEmpty()) {
                                showEmpty(true)
                            } else {
                                showEmpty(false)
                                populateData(data)
                            }
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            val data = resource.data
                            if (data.isNullOrEmpty()) {
                                showError(true, resource.message)
                            } else {
                                populateData(data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun populateData(movies: List<MovieUiModel>) {
        if (_binding == null) return
        if (movies.isNotEmpty()) {
            val featured = movies.first()
            binding.cardFeatured.visibility = View.VISIBLE
            binding.tvFeaturedTitle.text = featured.title
            binding.tvFeaturedRating.text = featured.formattedRating
            binding.tvFeaturedYear.text = featured.releaseYear

            Glide.with(this)
                .load(featured.backdropUrl)
                .placeholder(com.example.capstone.core.R.drawable.bg_shimmer_card)
                .into(binding.ivFeaturedBackdrop)

            binding.cardFeatured.setOnClickListener {
                openDetail(featured)
            }

            _movieAdapter?.submitList(movies)
        } else {
            binding.cardFeatured.visibility = View.GONE
            _movieAdapter?.submitList(emptyList())
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding == null) return
        if (isLoading) {
            binding.shimmerViewContainer.startShimmer()
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.rvMovies.visibility = View.GONE
            binding.cardFeatured.visibility = View.GONE
        } else {
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.rvMovies.visibility = View.VISIBLE
        }
    }

    private fun showError(isError: Boolean, message: String? = null) {
        if (_binding == null) return
        binding.viewError.layoutError.visibility = if (isError) View.VISIBLE else View.GONE
        if (isError && !message.isNullOrEmpty()) {
            binding.viewError.tvErrorMessage.text = message
        }
        if (isError) {
            binding.rvMovies.visibility = View.GONE
            binding.cardFeatured.visibility = View.GONE
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        if (_binding == null) return
        binding.viewEmpty.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        if (isEmpty) {
            binding.rvMovies.visibility = View.GONE
            binding.cardFeatured.visibility = View.GONE
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
        binding.rvMovies.adapter = null
        _movieAdapter = null
        _binding = null
    }
}
