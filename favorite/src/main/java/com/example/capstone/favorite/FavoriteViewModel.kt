package com.example.capstone.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.core.domain.usecase.MovieUseCase
import com.example.capstone.presentation.mapper.DomainToPresentationMapper
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    val favoriteMovies: StateFlow<List<MovieUiModel>> = movieUseCase
        .getFavoriteMovies()
        .map { domainList ->
            DomainToPresentationMapper.mapDomainListToUi(domainList)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFavoriteMovie(movie: MovieUiModel, state: Boolean) {
        viewModelScope.launch {
            val domainMovie = DomainToPresentationMapper.mapUiToDomain(movie)
            movieUseCase.setFavoriteMovie(domainMovie, state)
        }
    }
}
