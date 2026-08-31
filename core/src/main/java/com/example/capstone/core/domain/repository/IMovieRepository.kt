package com.example.capstone.core.domain.repository

import com.example.capstone.core.domain.model.Movie
import com.example.capstone.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IMovieRepository {

    fun getPopularMovies(): Flow<Resource<List<Movie>>>

    fun getNowPlayingMovies(): Flow<Resource<List<Movie>>>

    fun getTopRatedMovies(): Flow<Resource<List<Movie>>>

    fun getMovieDetail(movieId: Int): Flow<Resource<Movie>>

    fun searchMovies(query: String): Flow<Resource<List<Movie>>>

    fun getFavoriteMovies(): Flow<List<Movie>>

    fun isFavorite(movieId: Int): Flow<Boolean>

    suspend fun setFavoriteMovie(movie: Movie, state: Boolean)
}
