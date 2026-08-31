package com.example.capstone.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.core.domain.usecase.MovieUseCase
import com.example.capstone.core.utils.Resource
import com.example.capstone.presentation.mapper.DomainToPresentationMapper
import com.example.capstone.presentation.model.MovieUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val searchResult: StateFlow<Resource<List<MovieUiModel>>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { queryText ->
            if (queryText.trim().isEmpty()) {
                flowOf(Resource.Success(emptyList()))
            } else {
                movieUseCase.searchMovies(queryText.trim()).map { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val uiList = DomainToPresentationMapper.mapDomainListToUi(resource.data ?: emptyList())
                            Resource.Success(uiList)
                        }
                        is Resource.Loading -> {
                            Resource.Loading()
                        }
                        is Resource.Error -> {
                            Resource.Error(resource.message ?: "Failed to search movies")
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Success(emptyList())
        )

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }
}
