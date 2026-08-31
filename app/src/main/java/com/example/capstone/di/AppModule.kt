package com.example.capstone.di

import com.example.capstone.core.domain.usecase.MovieInteractor
import com.example.capstone.core.domain.usecase.MovieUseCase
import com.example.capstone.presentation.detail.DetailViewModel
import com.example.capstone.presentation.home.HomeViewModel
import com.example.capstone.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<MovieUseCase> { MovieInteractor(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}
