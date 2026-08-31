package com.example.capstone.presentation.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.capstone.core.utils.Resource
import com.example.capstone.databinding.FragmentSearchBinding
import com.example.capstone.presentation.detail.DetailActivity
import com.example.capstone.presentation.home.MovieAdapter
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private var _movieAdapter: MovieAdapter? = null
    private val movieAdapter get() = _movieAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()
        observeData()
    }

    private fun setupRecyclerView() {
        _movieAdapter = MovieAdapter { movie ->
            openDetail(movie)
        }
        binding.rvSearchResults.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = _movieAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchInput() {
        binding.etSearch.doAfterTextChanged { text ->
            val query = text?.toString().orEmpty()
            binding.ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
            viewModel.setQuery(query)
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.setQuery("")
        }

        binding.viewSearchError.btnRetry.setOnClickListener {
            val currentQuery = binding.etSearch.text?.toString().orEmpty()
            viewModel.setQuery(currentQuery)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResult.collect { resource ->
                    if (_binding == null) return@collect
                    val currentQuery = binding.etSearch.text?.toString().orEmpty().trim()
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading(true)
                            showError(false)
                            showEmpty(false)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            showError(false)
                            val data = resource.data.orEmpty()
                            _movieAdapter?.submitList(data)

                            if (currentQuery.isNotEmpty() && data.isEmpty()) {
                                showEmpty(true, "No movies matching '$currentQuery'")
                            } else {
                                showEmpty(false)
                            }
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showEmpty(false)
                            showError(true, resource.message)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding == null) return
        if (isLoading) {
            binding.shimmerSearchContainer.startShimmer()
            binding.shimmerSearchContainer.visibility = View.VISIBLE
            binding.rvSearchResults.visibility = View.GONE
        } else {
            binding.shimmerSearchContainer.stopShimmer()
            binding.shimmerSearchContainer.visibility = View.GONE
            binding.rvSearchResults.visibility = View.VISIBLE
        }
    }

    private fun showError(isError: Boolean, message: String? = null) {
        if (_binding == null) return
        binding.viewSearchError.layoutError.visibility = if (isError) View.VISIBLE else View.GONE
        if (isError && !message.isNullOrEmpty()) {
            binding.viewSearchError.tvErrorMessage.text = message
        }
        if (isError) {
            binding.rvSearchResults.visibility = View.GONE
        }
    }

    private fun showEmpty(isEmpty: Boolean, message: String? = null) {
        if (_binding == null) return
        binding.viewSearchEmpty.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        if (isEmpty && !message.isNullOrEmpty()) {
            binding.viewSearchEmpty.tvEmptyMessage.text = message
        }
        if (isEmpty) {
            binding.rvSearchResults.visibility = View.GONE
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
        binding.rvSearchResults.adapter = null
        _movieAdapter = null
        _binding = null
    }
}
