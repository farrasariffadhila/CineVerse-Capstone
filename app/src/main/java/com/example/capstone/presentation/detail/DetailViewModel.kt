package com.example.capstone.presentation.detail

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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    private val _movieId = MutableStateFlow<Int?>(null)

    val movieDetail: StateFlow<Resource<MovieUiModel>> = _movieId
        .filterNotNull()
        .flatMapLatest { id ->
            movieUseCase.getMovieDetail(id).map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val uiModel = resource.data?.let { DomainToPresentationMapper.mapDomainToUi(it) }
                        if (uiModel != null) Resource.Success(uiModel) else Resource.Error("Movie not found")
                    }
                    is Resource.Loading -> {
                        val uiModel = resource.data?.let { DomainToPresentationMapper.mapDomainToUi(it) }
                        Resource.Loading(uiModel)
                    }
                    is Resource.Error -> {
                        val uiModel = resource.data?.let { DomainToPresentationMapper.mapDomainToUi(it) }
                        Resource.Error(resource.message ?: "Failed to load movie details", uiModel)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    val isFavorite: StateFlow<Boolean> = _movieId
        .filterNotNull()
        .flatMapLatest { id ->
            movieUseCase.isFavorite(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setMovieId(id: Int) {
        _movieId.value = id
    }

    fun toggleFavorite(movie: MovieUiModel) {
        viewModelScope.launch {
            val domainMovie = DomainToPresentationMapper.mapUiToDomain(movie)
            val currentState = isFavorite.value
            movieUseCase.setFavoriteMovie(domainMovie, !currentState)
        }
    }
}
