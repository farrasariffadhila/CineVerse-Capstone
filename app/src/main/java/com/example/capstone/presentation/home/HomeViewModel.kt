package com.example.capstone.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.core.domain.usecase.MovieUseCase
import com.example.capstone.core.utils.Resource
import com.example.capstone.presentation.mapper.DomainToPresentationMapper
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("popular")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val moviesState: StateFlow<Resource<List<MovieUiModel>>> = _selectedCategory
        .flatMapLatest { category ->
            when (category) {
                "now_playing" -> movieUseCase.getNowPlayingMovies()
                "top_rated" -> movieUseCase.getTopRatedMovies()
                else -> movieUseCase.getPopularMovies()
            }
        }
        .map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val uiList = DomainToPresentationMapper.mapDomainListToUi(resource.data ?: emptyList())
                    Resource.Success(uiList)
                }
                is Resource.Loading -> {
                    val uiList = resource.data?.let { DomainToPresentationMapper.mapDomainListToUi(it) }
                    Resource.Loading(uiList)
                }
                is Resource.Error -> {
                    val uiList = resource.data?.let { DomainToPresentationMapper.mapDomainListToUi(it) }
                    Resource.Error(resource.message ?: "An error occurred", uiList)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}
