package com.example.capstone.core.domain.usecase

import com.example.capstone.core.domain.model.Movie
import com.example.capstone.core.domain.repository.IMovieRepository
import com.example.capstone.core.utils.Resource
import kotlinx.coroutines.flow.Flow

class MovieInteractor(private val movieRepository: IMovieRepository) : MovieUseCase {

    override fun getPopularMovies(): Flow<Resource<List<Movie>>> =
        movieRepository.getPopularMovies()

    override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> =
        movieRepository.getNowPlayingMovies()

    override fun getTopRatedMovies(): Flow<Resource<List<Movie>>> =
        movieRepository.getTopRatedMovies()

    override fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> =
        movieRepository.getMovieDetail(movieId)

    override fun searchMovies(query: String): Flow<Resource<List<Movie>>> =
        movieRepository.searchMovies(query)

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        movieRepository.getFavoriteMovies()

    override fun isFavorite(movieId: Int): Flow<Boolean> =
        movieRepository.isFavorite(movieId)

    override suspend fun setFavoriteMovie(movie: Movie, state: Boolean) =
        movieRepository.setFavoriteMovie(movie, state)
}
